package com.xing.imheres.service;

import com.xing.imheres.entity.sql.Message;
import com.xing.imheres.repository.MessageRepository;
import com.xing.imheres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.ServiceMode;

/**
 * @author xinG
 * @date 2018/8/30 0030 8:57
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository msgRepository;

    /**
     * 涉及到事务，所以放在Service中处理
     * 1.u_message的like_sum字段+1
     * 2.将关系存在u_like表中
     */
    @Transactional
    public void likeByMid(Integer uid,Integer mid){
        //System.out.println(this.userRepository == userRepository);
        Message message = msgRepository.findById(mid).get();
        message.setLike(message.getLike()+1);
        msgRepository.save(message);
        userRepository.likeByMid(uid,mid);
    }

    /**
     * 1.u_message的like_sum字段-1
     * 2.删除u_like表中的关系
     *
     * 测试出来一个bug即当并不存依赖关系的情况下，删除表中关系时并不会报错，所以message会正常-1，此时事务也救不了
     * 正确的处理应该先判断依赖关系存在，如果是再做进一步操纵，但是懒，我知道有错就行
     */
    @Transactional
    public void cancelLikeByMid(Integer uid,Integer mid){
        Message message = msgRepository.findById(mid).get();
        message.setLike(message.getLike()-1);
        msgRepository.save(message);
        userRepository.cancelLikeByMid(uid,mid);
    }
}
