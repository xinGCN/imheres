package com.xing.imheres.entity.sql;

import javax.persistence.*;
import java.util.List;

/**
 * @author xinG
 * @date 2018/8/9 0009 10:04
 */
@Entity
@Table(name = "user")
public class User {
    @Id
    @SequenceGenerator(name="user_seq", sequenceName="user_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="user_seq")
    private Integer uid;
    private String account;
    private String pass;
    private String username;
    @Column(name = "ensure_code")
    private String ensureCode;
    @Column(name = "ensure_time")
    private long ensureTime;
    private short state;
    private String friends;
    private String usersig;


    public String getUsersig() {
        return usersig;
    }

    public void setUsersig(String usersig) {
        this.usersig = usersig;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", account='" + account + '\'' +
                ", pass='" + pass + '\'' +
                ", username='" + username + '\'' +
                ", ensureCode='" + ensureCode + '\'' +
                ", ensureTime=" + ensureTime +
                ", state=" + state +
                ", friends='" + friends + '\'' +
                ", usersig='" + usersig + '\'' +
                '}';
    }

    //等待验证码状态
    public static final short WAIT_ENSURECODE = 1;

    //等待重置密码状态
    public static final short WAIT_PASS_RESET = 2;

    //普通状态，无特殊事情发生
    public static final short COMMON = 0;

    //注册失败状态，定时会被销毁
    public static final short REGISTER_OUT_TIME = 3;

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public String getEnsureCode() {
        return ensureCode;
    }

    public void setEnsureCode(String ensureCode) {
        this.ensureCode = ensureCode;
    }

    public long getEnsureTime() {
        return ensureTime;
    }

    public void setEnsureTime(long ensureTime) {
        this.ensureTime = ensureTime;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

}
