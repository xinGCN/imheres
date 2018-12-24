package com.xing.imheres.entity.sql;


import javax.persistence.*;

/**
 * @author xinG
 * @date 2018/8/2 0002 17:07
 */
@Entity
@Table(name = "u_message")
public class Message {
    @Id
    @SequenceGenerator(name="msg_seq", sequenceName="msg_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="msg_seq")
    private Integer mid;
    private String account;
    private double lat;
    private double lon;
    private String location;
    private String message;
    private String time;
    @Column(name = "like_sum")
    private int like;

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public Message(){}

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public String getAccount() {
        return account;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
