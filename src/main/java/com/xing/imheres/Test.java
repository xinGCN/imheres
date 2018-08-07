package com.xing.imheres;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xinG
 * @date 2018/8/7 0007 15:27
 */
public class Test {
    public static void main(String []args){

        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yy/MM/dd HH:MM");

        System.out.println("Current Date: " + ft.format(dNow));
    }
}
