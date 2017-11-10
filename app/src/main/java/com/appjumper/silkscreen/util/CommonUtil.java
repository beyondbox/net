package com.appjumper.silkscreen.util;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.base.MyApplication;

/**
 * Created by Botx on 2017/11/6.
 */

public class CommonUtil {

    private static AMapLocationClient mLocationClient;
    private static AMapLocationListener mLocationListener;

    static {
        mLocationClient = new AMapLocationClient(MyApplication.appContext);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
    }


    /**
     * 获取当前位置，只定位一次
     */
    public static void getLocation(AMapLocationListener listener) {
        if (mLocationClient.isStarted()) {
            mLocationClient.stopLocation();
        }

        if (mLocationListener != null) {
            mLocationClient.unRegisterLocationListener(mLocationListener);
            mLocationListener = null;
        }

        mLocationListener = listener;
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();
    }
}
