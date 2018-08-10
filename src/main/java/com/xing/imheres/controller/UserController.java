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
     * 注册流程可以留下来两张垃圾数据 ： 1为验证码验证成功后不设置密码的，这样帐号就一直处于等待设置密码状态，成为一个死帐号
     *                               2为超时的，这个可以每日定时删数据库中超时状态的帐号比较好解决
     * @param account
     * @return
     */
    @GetMapping(value = "/register/{account}")
    public RegisterResult register(@PathVariable("account")String account){
        RegisterResult result = new RegisterResult();
        User user = userRepository.findUserByAccount(account);
        if(user == null || user.getState() == User.REGISTER_OUT_TIME){
            result.setCode(RegisterResult.FAIL);
            String s = CommonUtils.generateEnsureCode();
            TencentSMS.sendSMS(account, s);
            if(user == null)
                user = new User();
            user.setAccount(account);
            //此处为了解决pass字段不能为空所以零时生成一个随机密码
            user.setPass(CommonUtils.generateEnsureCode());
            user.setEnsureCode(s);
            user.setEnsureTime(new Date().getTime());
            user.setState(User.WAIT_ENSURECODE);
            userRepository.save(user);
        }else{
            result.setCode(RegisterResult.FAIL);
        }
        return result;
    }

    /**
     *   code : RegisterResult.ACCOUNT_NOT_EXIST 4 账户不存在，根本没经过Register函数
     *          RegisterResult.ENSURECODE_OUT_TIME 3 的确刚刚在注册，但是已经超时
     *          RegisterResult.SUCCESS 1 成功
     *          RegisterResult.FAIL 0 失败
     */
    @GetMapping(value = "/register/ensureCode/{account}")
    public RegisterResult ensureCodeAfterRegister(@PathVariable("account")String account,@RequestParam("ensureCode")String ensureCode){
        RegisterResult result = new RegisterResult();
        User user = userRepository.findUserByAccount(account);
        if (user == null){
            result.setCode(RegisterResult.ACCOUNT_NOT_EXIST);
        }else{
            boolean isIn = (new Date().getTime() - user.getEnsureTime() < ENSURECODE_EXPIRATION);
            if(!isIn){
                result.setCode(RegisterResult.ENSURECODE_OUT_TIME);
                user.setState(User.REGISTER_OUT_TIME);
                userRepository.save(user);
            }else if(user.getState() == User.WAIT_ENSURECODE&&user.getEnsureCode().equals(ensureCode)){
                user.setState(User.WAIT_PASS_RESET);
                userRepository.save(user);
                result.setCode(RegisterResult.SUCCESS);
            }else if(user.getState() == User.WAIT_ENSURECODE&&!user.getEnsureCode().equals(ensureCode)){
                result.setCode(RegisterResult.ENSURECODE_ERROR);
            } else{
                result.setCode(RegisterResult.FAIL);
            }
        }
        return result;
    }

    /**
     * 重置密码
     * @param account
     * @return 检测RegisterResult.code
     * code：RegisterResult.ACCOUNT_NOT_EXIST 4 帐号不存在
     *       RegisterResult.SUCCESS 1 重置成功
     *       RegisterResult.FAIL 0 重置失败
     */
    @GetMapping(value = "/register/resetEnsureCode/{account}")
    public RegisterResult resetEnsureCode(@PathVariable("account")String account){
        RegisterResult result = new RegisterResult();
        User user = userRepository.findUserByAccount(account);
        if(user == null){
            result.setCode(RegisterResult.ACCOUNT_NOT_EXIST);
        }else{
            if(user.getState() == User.WAIT_ENSURECODE){
                String ensureCode = CommonUtils.generateEnsureCode();
                TencentSMS.sendSMS(account,ensureCode);
                user.setEnsureCode(ensureCode);
                user.setEnsureTime(new Date().getTime());
                userRepository.save(user);
                result.setCode(RegisterResult.SUCCESS);
            }else{
                result.setCode(RegisterResult.FAIL);
            }
        }
        return result;
    }

    /**
     * 成功验证验证码之后，帐号将一直处于可改密码状态，没有设置时间限制，这是个隐患
     *   code ：RegisterResult.ACCOUNT_NOT_EXIST 4 帐号不存在，改个鸡儿密码
     *          RegisterResult.SUCCESS 1 成功
     *          RegisterResult.FAIL 0 失败
     */
    @PutMapping(value = "/register/putPass/{account}")
    public RegisterResult putPassAfterEnsure(@PathVariable("account")String account,@RequestParam("pass")String pass){
        RegisterResult result = new RegisterResult();
        User user = userRepository.findUserByAccount(account);
        if(user == null){
            result.setCode(RegisterResult.ACCOUNT_NOT_EXIST);
        }else {
            if(user.getState() == User.WAIT_PASS_RESET){
                user.setState(User.COMMON);
                user.setPass(pass);
                userRepository.save(user);
                result.setCode(RegisterResult.SUCCESS);
            }else{
                result.setCode(RegisterResult.FAIL);
            }
        }
        return result;
    }

    @GetMapping(value = "/{account}")
    public void add(@PathVariable("account")String account){
        User user = userRepository.findUserByAccount(account);
        user.setPass("12345");
        userRepository.save(user);
    }

}
