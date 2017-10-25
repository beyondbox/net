package com.appjumper.silkscreen.ui.my.driver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.DriverAuth;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.MultiSelectPhotoActivity;
import com.appjumper.silkscreen.util.Applibrary;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.ImageUtil;
import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-21.
 * 司机认证第二步
 */
public class DriverAuthSecondActivity extends MultiSelectPhotoActivity{

    @Bind(R.id.iv_id_card_one)
    ImageView iv_id_card;
    @Bind(R.id.iv_id_card_two)
    ImageView iv_id_card_two;

    public static DriverAuthSecondActivity instance = null;

    String iv_id_card_one_url="";
    String iv_id_card_two_url="";

    private int mark = 0;
    private File tempImageFile;
    private DriverAuth driverAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_auth_second);
        initBack();
        ButterKnife.bind(this);
        instance = this;

        setCropSingleImage(false);
        setSingleImage(true);
        setCropTaskPhoto(false);

        driverAuth = (DriverAuth) getIntent().getSerializableExtra(Const.KEY_OBJECT);
    }


    @OnClick({R.id.iv_id_card_one,R.id.iv_id_card_two,R.id.txtNext, R.id.txtPrevious})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_id_card_one://身份证
                mark=0;
                showWindowSelectList(v);
                break;
            case R.id.iv_id_card_two://身份证反面
                mark=1;
                showWindowSelectList(v);
                break;
            case R.id.txtPrevious: //上一步
                finish();
                break;
            case R.id.txtNext://下一步
                if (iv_id_card_one_url.equals("")) {
                    showErrorToast("请上传驾驶证正面照");
                    return;
                }
                if (iv_id_card_two_url.equals("")) {
                    showErrorToast("请上传驾驶证反面照");
                    break;
                }

                intent = new Intent(context, DriverAuthThirdActivity.class);
                intent.putExtra(Const.KEY_OBJECT, driverAuth);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void requestImage(String[] path) {
        String origPath = path[0];

        if (tempImageFile != null) {
            if (tempImageFile.exists())
                tempImageFile.delete();
        }
        tempImageFile = new File(Applibrary.IMAGE_CACHE_DIR, System.currentTimeMillis() + ".jpg");
        boolean result = ImageUtil.saveBitmap(ImageUtil.compressImage(origPath, 1920, 1080), 80, tempImageFile);
        if (result) {
            initProgressDialog();
            progress.show();
            new Thread(new UpdateStringRun(tempImageFile.getPath())).start();
        }
    }


    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun implements Runnable {
        private File upLoadBitmapFile;
        public UpdateStringRun(String newPicturePath) {
            this.upLoadBitmapFile = new File(newPicturePath);
        }

        @Override
        public void run() {
            ImageResponse retMap = null;
            try {
                String url = Url.UPLOADIMAGE;
                retMap = JsonParser.getImageResponse(HttpUtil.uploadFile(url,
                        upLoadBitmapFile));
            } catch (Exception e) {
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
    private  class MyHandler extends Handler {
        private WeakReference<Context> reference;
        private ImageResponse imgResponse;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            DriverAuthSecondActivity activity = (DriverAuthSecondActivity) reference.get();
            if(activity == null){
                return;
            }

            if (isDestroyed())
                return;

            if(progress!=null){
                activity.progress.dismiss();
            }
            switch (msg.what) {
                case 3://上传头像
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        switch (mark){
                            case 0:
                                iv_id_card_one_url=imgResponse.getData().get(0).getOrigin();
                                Glide.with(activity).load(iv_id_card_one_url).placeholder(R.mipmap.icon_uploading_01).into(iv_id_card);
                                driverAuth.setImgDriver(iv_id_card_one_url);
                                break;
                            case 1:
                                iv_id_card_two_url=imgResponse.getData().get(0).getOrigin();
                                Glide.with(activity).load(iv_id_card_two_url).placeholder(R.mipmap.icon_uploading_01).into(iv_id_card_two);
                                driverAuth.setImgDriverBack(iv_id_card_two_url);
                                break;
                        }
                    } else {
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    showErrorToast("网络超时，请稍候");
                    break;
                default:
                    activity.showErrorToast();
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tempImageFile != null) {
            if (tempImageFile.exists())
                tempImageFile.delete();
        }
        instance = null;
    }

}
