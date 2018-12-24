package com.xing.imheres.repository;

import com.xing.imheres.entity.sql.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xinG
 * @date 2018/8/2 0002 17:16
 */
@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {
    List<Message> findMessagesByAccount(String account);

    @Query(value = "select * from u_message where mid in (select mid from u_like where uid = :uid)",nativeQuery = true)
    List<Message> selectAllLike(@Param("uid") Integer uid);
}
