package com.appjumper.silkscreen.net;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * 通用接口
 * Created by Botx on 2017/5/8.
 */

public class CommonApi {

    /**
     * 增加活跃度
     */
    public static void addLiveness(String userId, int type) {
        RequestParams params = MyHttpClient.getApiParam("collection", "addLiveness");
        params.put("uid", userId);
        params.put("liveness_type", type);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


}
