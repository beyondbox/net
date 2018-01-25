package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.bean.PersonalAuthInfo;
import com.appjumper.silkscreen.bean.PersonalAuthInfoResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.MultiSelectPhotoActivity;
import com.appjumper.silkscreen.util.Applibrary;
import com.appjumper.silkscreen.util.ImageUtil;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-21.
 * 个人认证
 */
public class PersonalAuthenticationActivity extends MultiSelectPhotoActivity{

    @Bind(R.id.iv_id_card_one)
    ImageView iv_id_card;

    @Bind(R.id.iv_id_card_two)
    ImageView iv_id_card_two;


    @Bind(R.id.enterprise_auth_status_name)
    TextView enterprise_auth_status_name;

    @Bind(R.id.tv_submit)
    TextView tv_submit;

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.et_idcard)
    EditText et_idcard;

    @Bind(R.id.view2)
    View view2;


    String iv_id_card_one_url="";
    String iv_id_card_two_url="";

    int mark=0;
    private String str;
    private String enterprise_auth_status="";

    private File tempImageFile;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_authentication);
        initBack();
        ButterKnife.bind(this);
        new Thread(new CompanyAuthInfoRun()).start();
    }

    private void initView(PersonalAuthInfo data){
        enterprise_auth_status = data.getAuth_status();
        enterprise_auth_status_name.setText(data.getAuth_status_name());
        if(enterprise_auth_status.equals("1")||enterprise_auth_status.equals("2")){
            tv_submit.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
        }
        if(enterprise_auth_status.equals("1")||enterprise_auth_status.equals("2")){
            iv_id_card_one_url = data.getIdcard_img().getOrigin();
            iv_id_card_two_url = data.getIdcard_img_back().getOrigin();
            Glide.with(this).load(iv_id_card_one_url).placeholder(R.mipmap.icon_uploading_01).into(iv_id_card);
            Glide.with(this).load(iv_id_card_two_url).placeholder(R.mipmap.icon_uploading_01).into(iv_id_card_two);
            et_name.setText(data.getName());
            et_name.setFocusable(false);
            et_idcard.setText(data.getIdcard());
            et_idcard.setFocusable(false);
        }
    }

    @OnClick({R.id.iv_id_card_one,R.id.iv_id_card_two,R.id.tv_submit,R.id.iv_idcard1,R.id.iv_idcard2})
    public void onClick(View v) {
        Intent intent;
        ArrayList<String> urls;
        switch (v.getId()) {
            case R.id.iv_idcard1:
                intent = new Intent(PersonalAuthenticationActivity.this, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls .add("2");
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 1);
                startActivity(intent);
                break;
            case R.id.iv_idcard2:
                intent = new Intent(PersonalAuthenticationActivity.this, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls .add("3");
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 1);
                startActivity(intent);
                break;
            case R.id.iv_id_card_one://身份证
                mark=0;
                if(enterprise_auth_status.equals("0") || enterprise_auth_status.equals("3")){
                    setCropSingleImage(false);
                    setSingleImage(true);
                    setCropTaskPhoto(false);
                    showWindowSelectList(v);
                }
                break;
            case R.id.iv_id_card_two://身份证反面
                mark=1;
                if(enterprise_auth_status.equals("0") || enterprise_auth_status.equals("3")){
                    setCropSingleImage(false);
                    setSingleImage(true);
                    setCropTaskPhoto(false);
                    showWindowSelectList(v);
                }
                break;
            case R.id.tv_submit://提交
                if(enterprise_auth_status.equals("")||enterprise_auth_status.equals("0")||enterprise_auth_status.equals("3")){
                    if(iv_id_card_one_url.equals("")){
                        showErrorToast("请上传身份证正面照");
                        return;
                    }
                    if(iv_id_card_two_url.equals("")){
                        showErrorToast("请上传身份证反面照");
                        break;
                    }
                }
                if(et_name.getText().toString().length()<1){
                    showErrorToast("真实姓名不能为空");
                    return;
                }
                if(et_idcard.getText().toString().length()<1){
                    showErrorToast("身份证号不能为空");
                    return;
                }

                if (et_idcard.getText().toString().length() < 18) {
                    showErrorToast("请输入正确的身份证号");
                    return;
                }

                initProgressDialog();
                progress.show();
                progress.setMessage("正在提交认证...");
                new Thread(new PersonalAuthRun()).start();
                break;
        }
    }

    //获取企业认证
    private class CompanyAuthInfoRun implements Runnable {
        private PersonalAuthInfoResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid",getUserID());
                response = JsonParser.getPersonalAuthInfoResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.USERAUTHINFO));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        4, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    //个人认证
    private class PersonalAuthRun implements Runnable {
        private BaseResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid",getUserID());
                data.put("name",et_name.getText().toString());
                data.put("idcard",et_idcard.getText().toString());
                data.put("idcard_img",iv_id_card_one_url);
                data.put("idcard_img_back",iv_id_card_two_url);
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.USERAUTH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    @Override
    protected void requestImage(String[] path) {
        str = path[0];


        if (tempImageFile != null) {
            if (tempImageFile.exists())
                tempImageFile.delete();
        }
        tempImageFile = new File(Applibrary.IMAGE_CACHE_DIR, System.currentTimeMillis() + ".jpg");
        boolean result = ImageUtil.saveBitmap(ImageUtil.compressImage(str, 1920, 1080), 80, tempImageFile);
        if (result) {
            initProgressDialog();
            progress.show();
            new Thread(new UpdateStringRun(tempImageFile.getPath())).start();
        }


        /*initProgressDialog();
        progress.show();
        new Thread(new UpdateStringRun(str)).start();*/
    }


    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun implements Runnable {
        private String newPicturePath;
        private File upLoadBitmapFile;
        public UpdateStringRun(String newPicturePath) {
            this.newPicturePath = newPicturePath;
            this.upLoadBitmapFile = new File(newPicturePath);
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            ImageResponse retMap = null;
            try {
                String url = Url.UPLOADIMAGE;
                retMap = JsonParser.getImageResponse(HttpUtil.uploadFile(url,
                        upLoadBitmapFile));
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
    private  class MyHandler extends Handler {
        private WeakReference<Context> reference;
        private ImageResponse imgResponse;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            PersonalAuthenticationActivity activity = (PersonalAuthenticationActivity) reference.get();
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
                                break;
                            case 1:
                                iv_id_card_two_url=imgResponse.getData().get(0).getOrigin();
                                Glide.with(activity).load(iv_id_card_two_url).placeholder(R.mipmap.icon_uploading_01).into(iv_id_card_two);
                                break;
                        }
//                        // 删除临时的100K左右的图片
                        File thumbnailPhoto = new File(str);
                        thumbnailPhoto.deleteOnExit();
                    } else {
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case 4://获取企业认证
                    PersonalAuthInfoResponse authinforesponse = (PersonalAuthInfoResponse) msg.obj;
                    if(authinforesponse.isSuccess()){
                        initView(authinforesponse.getData());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT://企业认证
                    BaseResponse baseresponse = (BaseResponse) msg.obj;
                    if(baseresponse.isSuccess()){
                        showErrorToast("个人认证提交成功");
                        finish();
                    }else{
                        activity.showErrorToast(baseresponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    activity.showErrorToast("数据返回错误");
                    break;

                default:
                    activity.showErrorToast();
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tempImageFile != null) {
            if (tempImageFile.exists())
                tempImageFile.delete();
        }
    }
}
