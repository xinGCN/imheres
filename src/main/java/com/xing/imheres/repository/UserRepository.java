package com.xing.imheres.repository;

import com.xing.imheres.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author xinG
 * @date 2018/8/9 0009 10:18
 */
@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
    User findUserByAccount(String account);

    boolean existsByAccount(String account);

    boolean existsByAccountAndPass(String account,String pass);

}
