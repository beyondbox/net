package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/18.
 * 创建企业（简要介绍）
 */
public class EnterpriseIntroduceActivity extends BasePhotoGridActivity {
    @Bind(R.id.editSms)
    EditText editText;//企业简介

    private Intent intent;
    private String imgPath;
    private String enterprise_name;
    private String startdata;
    private String create_money;
    private String plant_area;
    private String number_employees;
    private String number_machine;
    private String capacity;
    private String enterprise_intro;
    private String type = "";
    private Enterprise enterprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_introduce);
        ActivityTaskManager.getInstance().putActivity(this);
        hideKeyboard();
        intent = getIntent();
        ButterKnife.bind(this);
        initBack();
        initView();
        imgPath = intent.getStringExtra("imgPath");
        enterprise_name = intent.getStringExtra("enterprise_name");
        startdata = intent.getStringExtra("startdata");
        create_money = intent.getStringExtra("create_money");
        plant_area = intent.getStringExtra("plant_area");
        number_employees = intent.getStringExtra("number_employees");
        number_machine = intent.getStringExtra("number_machine");
        capacity = intent.getStringExtra("capacity");
        type = intent.getStringExtra("type");
        if (type.equals("1")) {
            enterprise = (Enterprise) intent.getSerializableExtra("enterprise");
            editText.setText(enterprise.getEnterprise_intro());
        }
    }

    @OnClick({R.id.next_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn://下一步
                enterprise_intro = editText.getText().toString().trim();
                if (enterprise_intro.length() <= 0) {
                    showErrorToast("企业简介不能为空");
                    return;
                }
                initProgressDialog();
                progress.show();
                new Thread(new UpdateStringRun(thumbPictures)).start();
                break;
            default:
                break;
        }
    }


    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun implements Runnable {
        private ArrayList<String> thumbPictures;

        // thumbPictures 是 List<压缩图路径>
        public UpdateStringRun(ArrayList<String> thumbPictures) {
            this.thumbPictures = new ArrayList<String>();
            for (String str : thumbPictures) {
                if (!str.equals("" + BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
                    //去掉最后一个 +图片
                    this.thumbPictures.add(str);
                }
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            ImageResponse retMap = null;
            try {
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
                retMap = JsonParser.getImageResponse(HttpUtil.upload(thumbPictures, Url.UPLOADIMAGE));

            } catch (Exception e) {
                // TODO Auto-generated catch block XDEBUG_SESSION_START=1
                e.printStackTrace();
            }
            if (retMap != null) {
                handler.sendMessage(handler.obtainMessage(
                        3, retMap));
            } else {
                handler.sendMessage(handler
                        .obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
            }
        }
    }

    private MyHandler handler = new MyHandler(this);
    private String urls;

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;
        private ImageResponse imgResponse;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            EnterpriseIntroduceActivity activity = (EnterpriseIntroduceActivity) reference.get();
            if (activity == null) {
                return;
            }
            activity.progress.dismiss();
            switch (msg.what) {
                case 3://上传头像
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        Intent intent = new Intent(activity, EnterpriseContactActivity.class);
                        intent.putExtra("imgPath", imgPath);
                        intent.putExtra("enterprise_name", enterprise_name);
                        intent.putExtra("startdata", startdata);
                        intent.putExtra("create_money", create_money);
                        intent.putExtra("plant_area", plant_area);
                        intent.putExtra("number_employees", number_employees);
                        intent.putExtra("number_machine", number_machine);
                        intent.putExtra("capacity", capacity);
                        intent.putExtra("enterprise_intro", enterprise_intro);
                        intent.putExtra("thumbPictures", imags(imgResponse.getData()));
                        intent.putExtra("type", type);
                        if (type.equals("1")) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("enterprise", enterprise);
                            intent.putExtras(bundle);
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in,
                                R.anim.push_left_out);
                    } else {
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                default:
                    activity.showErrorToast();
                    break;
            }
        }
    }

    ;

    private String imags(List<ImageResponse.Data> data) {
        String str = "";
        for (int i = 0; i < data.size(); i++) {
            str += data.get(i).getImg_id();
            if (i < (data.size() - 1)) {
                str += ",";
            }
        }
        return str;
    }
}
