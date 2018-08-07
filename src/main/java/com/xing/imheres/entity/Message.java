package com.xing.imheres.entity;


import javax.persistence.*;

/**
 * @author xinG
 * @date 2018/8/2 0002 17:07
 */
@Entity
@Table(name = "table1")
public class Message {
    @Id
    @GeneratedValue
    private Integer id;
    private String account;
    private double lat;
    private double lon;
    private String location;
    private String message;
    private String time;

    public Message(){}

//    public Message(int id, String account, String location_1, String location_2, String message) {
//        this.id = id;
//        this.account = account;
//        this.location_1 = location_1;
//        this.location_2 = location_2;
//        this.message = message;
//    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {

        return id;
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
