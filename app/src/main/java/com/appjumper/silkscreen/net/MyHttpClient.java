package com.appjumper.silkscreen.net;

import com.appjumper.silkscreen.util.MD5Tool;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AsyncHttpClient操作封装类
 * Created by Botx on 2017/3/17.
 */

public class MyHttpClient {

    private static AsyncHttpClient aHttpClient = new AsyncHttpClient();

    private MyHttpClient() {};

    public static AsyncHttpClient getInstance() {
        aHttpClient.setTimeout(15 * 1000);
        return aHttpClient;
    }


    public static RequestParams getApiParam(String module, String action) {
        RequestParams params = new RequestParams();
        params.put("g", "api");
        params.put("m", module);
        params.put("a", action);
        return params;
    }


    /**
     * get请求，带参数
     * @param url
     * @param map
     * @param responseHandler
     */
    public static void get(String url, Map<String, String> map, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams(map);
        try {
            params.put("sig", getSign(map));
            aHttpClient.get(url, params, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * get请求，不带参数
     * @param url
     * @param responseHandler
     */
    public static void get(String url, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        Map<String, String> map = new HashMap<>();
        try {
            params.put("sig", getSign(map));
            aHttpClient.get(url, params, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * post请求
     * @param url
     * @param map
     * @param responseHandler
     */
    public static void post(String url, Map<String, String> map, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams(map);
        try {
            params.put("sig", getSign(map));
            aHttpClient.post(url, params, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * 请求参数加密
     * 加密规则：比如要传递的参数为  sex：1  name：张三 age：25
     * 第一步，先按key的首字母排序 并拼接成 String buffermd5 = “age=25&name=张三&sex=1”
     * 第二步，buffermd5再拼接上sig = SIG_KEY。结果为 buffermd5 = “age=25&name=张三&sex=1&sig=iTopic2015”
     * 第三步，将buffermd5做md5加密，得到一个32位小写string
     * 第四步，将上一步得到的string当做一个参数传给服务器，key是“sig”
     */
    public static String getSign(Map<String, String> map) throws UnsupportedEncodingException, NoSuchAlgorithmException {
//        map.put("XDEBUG_SESSION_START", "1");
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());

        // 排序
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        StringBuffer buffermd5 = new StringBuffer();
        for (int i = 0; i < infoIds.size(); i++) {
            String key = infoIds.get(i).getKey();
            buffermd5.append(key);
            buffermd5.append("=");

            String value = infoIds.get(i).getValue();
            value = value == null ? "" : value;
            buffermd5.append(value);
            buffermd5.append("&");
        }

        buffermd5.append("sig=" + Url.SIG_KEY);
        return MD5Tool.getMD5(buffermd5.toString()).toLowerCase();
    }

}
