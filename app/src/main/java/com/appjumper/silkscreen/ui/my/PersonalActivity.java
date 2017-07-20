package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.bean.UserResponse;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.common.MultiSelectPhotoActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/22.
 * 个人资料
 */
public class PersonalActivity extends MultiSelectPhotoActivity {
    @Bind(R.id.iv_img)//头像
            ImageView iv_img;

    @Bind(R.id.tv_nickname)//昵称
            TextView tv_nickname;

    @Bind(R.id.tv_sex)//性别
            TextView tv_sex;

    @Bind(R.id.tv_mobile)//手机号
            TextView tv_mobile;

//    private String imgPath;

    private String[] expiry={"保密","男","女"};
    private int expiry_date;
    private String imgPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        initBack();
    }

    @OnClick({R.id.rl_img,R.id.rl_nickname,R.id.rl_sex})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_img://选择头像
                setCropSingleImage(true);
                setSingleImage(true);
                setCropTaskPhoto(true);
                showWindowSelectList(v);
                break;
            case R.id.rl_nickname://昵称
                start_Activity(PersonalActivity.this,UserEditActivity.class,new BasicNameValuePair("title","昵称"),new BasicNameValuePair("hinttitle","点击输入昵称"),new BasicNameValuePair("key","nickname"));
                break;
            case R.id.rl_sex://性别
                Intent intent = new Intent(PersonalActivity.this,InformationSelectActivity.class);
                Bundle b=new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title","性别");
                startActivityForResult(intent,3);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = getUser();
        tv_nickname.setText(user.getUser_nicename());
        tv_mobile.setText(user.getMobile());
        String sex = user.getSex();
        if(sex.equals("1")){
            tv_sex.setText("男");
        }else if(sex.equals("2")){
            tv_sex.setText("女");
        }else{
            tv_sex.setText("保密");
        }
        if(user.getAvatar()!=null){
            Picasso.with(this)
                    .load(user.getAvatar().getOrigin())
                    .placeholder(R.mipmap.img_error_head)
                    .transform(new PicassoRoundTransform())
                    .resize(DisplayUtil.dip2px(context, 65), DisplayUtil.dip2px(context, 65))
                    .centerCrop()
                    .into(iv_img);
        }
    }


    @Override
    protected void requestImage(String[] path) {
        if (path != null && path.length > 0) {
            imgPath = path[0];
            initProgressDialog();
            progress.show();
            progress.setMessage("正在更新头像...");
            new Thread(new UpdateStringRun(path[0])).start();
        }
    }


    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun implements Runnable {
        private String newPicturePath;
        public UpdateStringRun(String newPicturePath) {
            this.newPicturePath = newPicturePath;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            UserResponse retMap = null;
            try {
                String url = Url.UPLOADAVATAR;
                Map<String,String> map = new HashMap<>();
                map.put("uid",getUserID());
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
                retMap = JsonParser.getUserResponse(HttpUtil.uploadImageScreen(map,newPicturePath,
                        url));
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
    private String urls;
    private  class MyHandler extends Handler {
        private WeakReference<Context> reference;
        private ImageResponse imgResponse;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            PersonalActivity activity = (PersonalActivity) reference.get();
            if(activity == null){
                return;
            }
            activity.progress.dismiss();
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://修改性别
                    progress.dismiss();
                    BaseResponse userResponse = (BaseResponse) msg.obj;
                    if(userResponse.isSuccess()){
                        showErrorToast("更新成功");
                        User user = getUser();
                        user.setSex(expiry_date+"");
                        tv_sex.setText(expiry[expiry_date]);
                        getMyApplication().getMyUserManager()
                                .storeUserInfo(user);
                    }else{
                        showErrorToast(userResponse.getError_desc());
                    }
                    break;
                case 3://上传头像
                    UserResponse usersResponse = (UserResponse) msg.obj;
                    if (usersResponse.isSuccess()) {
                        // 删除临时的100K左右的图片
                        File thumbnailPhoto = new File(imgPath);
                        if (thumbnailPhoto.exists())
                            thumbnailPhoto.delete();
                        User user = usersResponse.getData();
                        getMyApplication().getMyUserManager()
                                .storeUserInfo(user);
                        Picasso.with(PersonalActivity.this).load(user.getAvatar().getOrigin()).placeholder(R.mipmap.img_error_head).transform(new PicassoRoundTransform()).into(iv_img);
                    } else {
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    activity.showErrorToast();
                    break;

                default:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 3://性别
                expiry_date = Integer.parseInt(data.getStringExtra("val"));
                initProgressDialog();
                progress.show();
                progress.setMessage("正在更新");
                new Thread(new EditRun(expiry_date+"")).start();
                break;
            default:
                break;
        }
    }

    //修改
    private class EditRun implements Runnable {
        private String str;
        private File upLoadBitmapFile;
        public EditRun(String str) {
            this.str = str;
        }
        private BaseResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("sex", str);
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.USEREDIT));
            } catch (Exception e) {
                progress.dismiss();
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

}
