package com.appjumper.silkscreen.net;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentReleaseActivity;
import com.appjumper.silkscreen.ui.home.logistics.PersonalReleaseActivity;
import com.appjumper.silkscreen.ui.home.logistics.ReleaseLineActivity;
import com.appjumper.silkscreen.ui.home.logistics.TruckReleaseActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitReleaseActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopReleaseActivity;
import com.appjumper.silkscreen.ui.my.MyReleaseActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.MProgressDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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


    /**
     * 发布信息前检查已发布的条数
     */
    public static void releaseCheck(final Context context, String uid, final int type) {
        if (!MyApplication.appContext.checkMobile(context))
            return;

        RequestParams params = MyHttpClient.getApiParam("service", "addCheck");
        params.put("uid", uid);
        params.put("type", type);

        final MProgressDialog progress = new MProgressDialog(context, false);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        int num = dataObj.optInt("shuliang");
                        int max;
                        if (type == Const.SERVICE_TYPE_LOGISTICS_PER)
                            max = 1;
                        else if (type == Const.SERVICE_TYPE_LOGISTICS_CAR || type == Const.SERVICE_TYPE_WORKSHOP)
                            max = 2;
                        else
                            max = 5;

                        if (num < max) {
                            switch (type) {
                                case Const.SERVICE_TYPE_ORDER: //订做
                                    goToProductSelect(context, Const.SERVICE_TYPE_ORDER, ProductSelectActivity.MOTION_RELEASE_SERVICE);
                                    break;
                                case Const.SERVICE_TYPE_PROCESS: //加工
                                    goToProductSelect(context, Const.SERVICE_TYPE_PROCESS, ProductSelectActivity.MOTION_RELEASE_SERVICE);
                                    break;
                                case Const.SERVICE_TYPE_STOCK: //现货
                                    goToProductSelect(context, Const.SERVICE_TYPE_STOCK, ProductSelectActivity.MOTION_RELEASE_SERVICE);
                                    break;
                                case Const.SERVICE_TYPE_LOGISTICS: //物流-货站
                                    //start_Activity(context, PersonalReleaseActivity.class, new BasicNameValuePair("type", "1"));
                                    start_Activity(context, ReleaseLineActivity.class);
                                    break;
                                case Const.SERVICE_TYPE_LOGISTICS_PER: //物流-个人
                                    start_Activity(context, PersonalReleaseActivity.class, new BasicNameValuePair("type", "2"));
                                    break;
                                case Const.SERVICE_TYPE_LOGISTICS_CAR: //物流-找车
                                    start_Activity(context, TruckReleaseActivity.class);
                                    break;
                                case Const.SERVICE_TYPE_WORKSHOP: //厂房
                                    start_Activity(context, WorkshopReleaseActivity.class);
                                    break;
                                case Const.SERVICE_TYPE_DEVICE: //设备
                                    start_Activity(context, EquipmentReleaseActivity.class);
                                    break;
                                case Const.SERVICE_TYPE_JOB: //招聘
                                    start_Activity(context, RecruitReleaseActivity.class);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            if (max == 1)
                                Toast.makeText(context, "您已发布过该版块信息，不能继续发布了", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, "您在该版块发布信息达到" + max + "条，不能继续发布了", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, MyReleaseActivity.class));
                        }
                    } else {
                        Toast.makeText(context, jsonObj.getString(Const.KEY_ERROR_DESC), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, context.getResources().getString(R.string.requst_fail), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }



    /**
     * 跳转到产品选择界面
     */
    private static void goToProductSelect(Context context, int serviceType, int motion) {
        Intent intent = new Intent(context, ProductSelectActivity.class);
        intent.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
        intent.putExtra(Const.KEY_MOTION, motion);
        context.startActivity(intent);
    }


    /**
     * 打开Activity
     */
    public static void start_Activity(Context context, Class<?> cls, BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        context.startActivity(intent);
    }

}
