package com.xing.imheres.util;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import java.io.IOException;

/**
 * @author xinG
 * @date 2018/8/9 0009 19:20
 *
 * 关于返回值
 * result	是	number	错误码，0 表示成功(计费依据)，非 0 表示失败，参考 错误码
 * errmsg	是	string	错误消息，result 非 0 时的具体错误信息
 * ext	    否	string	用户的 session 内容，腾讯 server 回包中会原样返回
 * fee	    否	number	短信计费的条数，"fee" 字段计费说明
 * sid	    否	string	本次发送标识 id，标识一次短信下发记录
 *
 * result错误码查询地址：https://cloud.tencent.com/document/product/382/3771
 */
public class TencentSMS {
    private static final String appkey = "11eccbf9a3e682456a745eaa243b024a";
    private static final int appid = 1400124107;
    private static final String template = "为您的注册验证码，请于1分钟内填写。如非本人操作，请忽略本短信。";

    public static SmsSingleSenderResult sendSMS(String tel,String ensureCode){
        try {
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            return ssender.send(0, "86", tel, ensureCode + template, "", "");
        } catch (HTTPException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
