package com.xing.imheres.repository;

import com.xing.imheres.entity.sql.Message;
import com.xing.imheres.entity.sql.User;
import org.hibernate.annotations.SQLInsert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xinG
 * @date 2018/8/9 0009 10:18
 */
@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
    User findUserByAccount(String account);

    boolean existsByAccount(String account);

    boolean existsByAccountAndPass(String account,String pass);

    //@Query("select t from User t where t.name = :name")

    @Modifying
    @Query(value = "insert into u_like (uid,mid) values (:uid,:mid);",nativeQuery = true)
    void likeByMid(@Param("uid") Integer uid,@Param("mid") Integer mid);

    @Modifying
    @Query(value="delete from u_like where uid=?1 and mid=?2",nativeQuery = true)
    void cancelLikeByMid(Integer uid,Integer mid);
}
