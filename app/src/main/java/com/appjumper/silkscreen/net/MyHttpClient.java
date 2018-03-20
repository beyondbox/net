package com.appjumper.silkscreen.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

/**
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

}
