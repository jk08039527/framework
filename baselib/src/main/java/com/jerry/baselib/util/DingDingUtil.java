package com.jerry.baselib.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName DingDingUtil
 * @Description: 钉钉机器人消息通知
 * @Author Shuai.Zhang
 * @Date 2020/8/5
 * @Version
 **/
public class DingDingUtil {
    //发送超时时间10s
    private static final int TIME_OUT = 10000;

    /**
     * 钉钉机器人文档地址https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq
     *
     * @param webhook
     * @param secret     安全设置 3选1【方式一，自定义关键词 】 【方式二，加签 ，创建机器人时选择加签 secret以SE开头】【方式三，IP地址（段）】
     * @param content    发送内容
     * @param mobileList 通知具体人的手机号码列表 （可选）
     * @return
     */
    public static String sendMsg(String webhook, String secret, String content, List<String> mobileList) {
        try {
            //钉钉机器人地址（配置机器人的webhook）
            if (!TextUtils.isEmpty(secret)) {
                Long timestamp = System.currentTimeMillis();
                String sign = getSign(timestamp, secret);
                webhook = new StringBuilder(webhook)
                        .append("&timestamp=")
                        .append(timestamp)
                        .append("&sign=")
                        .append(sign)
                        .toString();
            }
            System.out.println("webhook:" + webhook);
            //是否通知所有人
            boolean isAtAll = false;
            //组装请求内容
            String reqStr = buildReqStr(content, isAtAll, mobileList);
            //推送消息（http请求）
            String result = postJson(webhook, reqStr);
            LogUtils.i("推送结果result == " + result);
            return result;
        } catch (Exception e) {
            LogUtils.e("发送群通知异常 异常原因：{}"+ Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    /**
     * 组装请求报文
     * 发送消息类型 text
     *
     * @param content
     * @return
     */
    private static String buildReqStr(String content, boolean isAtAll, List<String> mobileList) {
        //消息内容
        Map<String, String> contentMap = new ArrayMap<>();
        contentMap.put("content", content);
        //通知人
        Map<String, Object> atMap = new ArrayMap<>();
        //1.是否通知所有人
        atMap.put("isAtAll", isAtAll);
        //2.通知具体人的手机号码列表
        atMap.put("atMobiles", mobileList);
        Map<String, Object> reqMap = new ArrayMap<>();
        reqMap.put("msgtype", "text");
        reqMap.put("text", contentMap);
        reqMap.put("at", atMap);
        return JSON.toJSONString(reqMap);
    }


    private static String postJson(String url, String reqStr) {
        String body = null;
        try {
//            body = HttpRequest.post(url).body(reqStr).timeout(TIME_OUT).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * 自定义机器人获取签名
     * 创建机器人时选择加签获取secret以SE开头
     *
     * @param timestamp
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     */
    private static String getSign(Long timestamp, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(Base64.encodeToString(signData, Base64.DEFAULT));
        System.out.println("singn:" + sign);
        return sign;
    }


    public static void main(String[] args) {
        String webhook = "https://oapi.dingtalk.com/robot/send?access_token=xxxxx";
        String secret = "SECxxxxx";
        List<String> mobileList = new ArrayList<>();
        mobileList.add("18888888888");
        DingDingUtil.sendMsg(webhook, null, "小哥，你好", null);
    }
}

