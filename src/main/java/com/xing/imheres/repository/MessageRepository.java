package com.xing.imheres.repository;

import com.xing.imheres.entity.Message;
import org.springframework.data.jpa.mapping.JpaPersistentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xinG
 * @date 2018/8/2 0002 17:16
 */
@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {
    List<Message> findMessagesByAccount(String account);
}
