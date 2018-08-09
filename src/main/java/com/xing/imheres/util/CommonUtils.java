package com.xing.imheres.util;

import java.security.MessageDigest;

/**
 * @author xinG
 * @date 2018/8/9 0009 19:34
 */
public class CommonUtils {
    /**
     * 通过经纬度获得两点之间的距离，只适用于小范围，并不准确
     * from https://blog.csdn.net/xiejm2333/article/details/73297004
     * 单位：米
     */
    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        double Lat1 = rad(latitude1); // 纬度
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;//两点纬度之差
        double b = rad(longitude1) - rad(longitude2); //经度之差
        double s = 2 * Math.asin(Math
                .sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));//计算两点距离的公式
        s = s * 6378137.0;//弧长乘地球半径（半径为米）
        s = Math.round(s * 10000d) / 10000d;//精确距离的数值
        return s;
    }
    private static double rad(double d) {
        return d * Math.PI / 180.00; //角度转换成弧度
    }


    /**
     * MD5加密
     * MD5加密的密文总是32位
     * @param s 明文
     * @return 密文
     */
    public static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 生成随机6位数字
     */
    public static String generateEnsureCode(){
        return (int)((Math.random()*9+1)*100000)+"";
    }

    public static void main(String []args){
        for (int i = 0 ; i < 100; i++)
            System.out.println(generateEnsureCode());
    }


}
