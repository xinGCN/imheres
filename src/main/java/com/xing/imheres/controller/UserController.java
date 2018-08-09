package com.xing.imheres.controller;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.sun.xml.internal.bind.v2.TODO;
import com.xing.imheres.entity.LoginResult;
import com.xing.imheres.entity.Message;
import com.xing.imheres.entity.RegisterResult;
import com.xing.imheres.entity.User;
import com.xing.imheres.repository.MessageRepository;
import com.xing.imheres.repository.UserRepository;
import com.xing.imheres.util.CommonUtils;
import com.xing.imheres.util.TencentSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author xinG
 * @date 2018/8/9 0009 10:20
 */
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    private static final int SECOND = 1000;
    //设置验证码过期时间，短信提示为1分钟，实际时长为75s
    private static final int ENSURECODE_EXPIRATION = SECOND * 75;

    @GetMapping(value = "/login")
    public LoginResult login(@RequestParam("account") String account,@RequestParam("pass") String pass){
        LoginResult result = new LoginResult();
        if(userRepository.existsByAccountAndPass(account,pass)){
            result.setCode(LoginResult.SUCCESS);
            result.setUser(userRepository.findUserByAccount(account));
            result.setMsgs(messageRepository.findMessagesByAccount(account));
        }else{
            result.setCode(LoginResult.FAIL);
        }
        return result;
    }

    /**
     * 注册函数分为3个阶段，1为register，2为ensureCodeAfterRegister，3为putPassAfterEnsure
     * register函数通过接受帐号，判断该账号是否已被注册
     * 被注册情况下直接返回RegisterResult.FAIL
     * 注册情况下 1)返回RegisterResult.SUCCESS并且启动腾讯云的短信服务发送验证码到用户手机
     *           2)在启动信息服务后，记录下验证码和时间戳保存在数据库中,并将用户状态设置为User.WAIT_ENSURECODE
     *           3)启动延迟时间为ENSURECODE_EXPIRATION的定时任务，到时检测是否用户状态为User.WAIT_ENSURECODE或者User.WAIT_PASS_RESET，
     *           如果是代表用户未能在规定时间完成注册，所以删除临时用户
     *
     * ensureCodeAfterRegister函数通过接受帐号以及验证码进行判断验证码的正确性
     * 正确的情况下将用户状态设置为User.WAIT_PASS_RESET，开启用户重设密码权限
     *
     * putPassAfterEnsure函数通过接受帐号以及密码，常规判断通过后重设密码，将用户状态设置为User.Common
     *
     * putPassAfterRegister
     * @param account
     * @return
     */
    @GetMapping(value = "/register/{account}")
    public RegisterResult register(@PathVariable("account")String account){
        RegisterResult result = new RegisterResult();
        if(userRepository.existsByAccount(account)){
            result.setCode(RegisterResult.FAIL);
            String s = CommonUtils.generateEnsureCode();
            TencentSMS.sendSMS(account, s);
            User user = new User();
            user.setAccount(account);

            //此处为了解决pass字段不能为空所以零时生成一个随机密码
            user.setPass(CommonUtils.generateEnsureCode());

            user.setEnsureCode(s);
            user.setEnsureTime(new Date().getTime());
            user.setState(User.WAIT_ENSURECODE);
            userRepository.save(user);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    User usr = userRepository.findUserByAccount(account);
                    if(usr.getState() == User.WAIT_ENSURECODE || usr.getState() == User.WAIT_PASS_RESET)
                        userRepository.delete(usr);
                }
            },ENSURECODE_EXPIRATION);
        }else {
            result.setCode(RegisterResult.FAIL);

        }
        return result;
    }

    @GetMapping(value = "/register/ensureCode/{account}")
    public RegisterResult ensureCodeAfterRegister(@PathVariable("account")String account,@RequestParam("ensureCode")String ensureCode){
        RegisterResult result = new RegisterResult();
        User user = userRepository.findUserByAccount(account);
        if (user == null){
            result.setCode(RegisterResult.FAIL);
        }else{
            if(user.getState() == User.WAIT_ENSURECODE && (new Date().getTime() - user.getEnsureTime() < ENSURECODE_EXPIRATION)){
                user.setState(User.WAIT_PASS_RESET);
                userRepository.save(user);
                result.setCode(RegisterResult.SUCCESS);
            }else{
                result.setCode(RegisterResult.FAIL);
            }
        }
        return result;
    }

    @PutMapping(value = "/register/putPass/{account}")
    public RegisterResult putPassAfterEnsure(@PathVariable("account")String account,@RequestParam("pass")String pass){
        RegisterResult result = new RegisterResult();
        User user = userRepository.findUserByAccount(account);
        if(user != null && user.getEnsureTime() != 0
                && (new Date().getTime() - user.getEnsureTime())<ENSURECODE_EXPIRATION){
            user.setPass(pass);
            user.setEnsureCode("");
            user.setEnsureTime(0);
            userRepository.save(user);

            result.setCode(RegisterResult.SUCCESS);
            result.setUser(user);
        }else{
            result.setCode(RegisterResult.FAIL);
        }
        if(user == null){
            result.setCode(RegisterResult.FAIL);
        }else {
            if(user.getState() == User.WAIT_PASS_RESET && (new Date().getTime() - user.getEnsureTime() < ENSURECODE_EXPIRATION)){
                user.setState(User.COMMON);
                user.setPass(pass);
                userRepository.save(user);
            }
        }
        return result;
    }

    //TODO
    //考虑验证码重置的问题，以及重新考虑取消本次注册的定时任务的决定性条件
    //返回更清晰的错误代码
}
