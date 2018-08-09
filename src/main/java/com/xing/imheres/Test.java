package com.xing.imheres;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xinG
 * @date 2018/8/7 0007 15:27
 */
public class Test {
    public static void main(String []args){
        for(int j = 0; j< 100; j++){
            System.out.println((int)((Math.random()*9+1)*100000));
        }
    }
}
