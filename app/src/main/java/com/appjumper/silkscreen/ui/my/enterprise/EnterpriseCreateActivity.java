package com.appjumper.silkscreen.ui.my.enterprise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.Applibrary;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/18.
 * 创建企业（基本信息）
 */
public class EnterpriseCreateActivity extends BasePhotoGridActivity {
    @Bind(R.id.iv_logo)
    ImageView iv_logo;

    @Bind(R.id.et_enterprise_name)
    EditText et_enterprise_name;//企业名称
    @Bind(R.id.et_number_employees)
    EditText et_number_employees;//员工人数
    @Bind(R.id.tv_enterprise_create_time)
    TextView tv_enterprise_create_time;//注册时间
    @Bind(R.id.et_create_money)
    EditText et_create_money;//注册资本

    @Bind(R.id.et_enterprise_contacts)//联系人
    EditText et_enterprise_contacts;
    @Bind(R.id.et_enterprise_tel)//联系电话
    EditText et_enterprise_tel;
    @Bind(R.id.et_enterprise_address)//企业地址
    EditText et_enterprise_address;

    @Bind(R.id.editSms)
    EditText editText;//企业简介
    @Bind(R.id.next_btn)
    TextView next_btn;

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private String startdata = "";
    private String enterprise_name;
    private String create_money;
    private String number_employees;
    private String type = "";
    private Enterprise enterprise;
    private String enterprise_contacts;
    private String enterprise_tel;

    private String enterprise_address;
    private String enterprise_intro;


    private String logoUrl = ""; //已保存过的logo的URL
    private String logoPath = ""; //现在待上传的logo图片路径
    private String finalLogo = ""; //最终确定的logo路径

    private String imgsUrl = "";

    private File cameraFile;
    private String cameraPath = Environment.getExternalStorageDirectory().getPath() + "/" + "picture";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_create2);
        ActivityTaskManager.getInstance().putActivity(this);
        hideKeyboard();
        ButterKnife.bind(this);
        initBack();
        initView();
        initLocation();
        type = getIntent().getStringExtra("type");
        if (type.equals("1")) {
            initProgressDialog(false, "正在提交...");
            initTitle("编辑企业");
            next_btn.setText("完成");

            enterprise = (Enterprise) getIntent().getSerializableExtra("enterprise");
            startdata=enterprise.getEnterprise_reg_date();
            et_enterprise_name.setText(enterprise.getEnterprise_name());
            tv_enterprise_create_time.setText(startdata);
            et_create_money.setText(enterprise.getEnterprise_reg_money());
            et_number_employees.setText(enterprise.getEnterprise_staff_num());
            logoUrl = enterprise.getEnterprise_logo().getOrigin();
            imgsUrl = enterprise.getEnterprise_imgs();

            et_enterprise_contacts.setText(enterprise.getEnterprise_contacts());
            et_enterprise_tel.setText(enterprise.getEnterprise_tel());
            et_enterprise_address.setText(enterprise.getEnterprise_address());
            editText.setText(enterprise.getEnterprise_intro());

            /*Picasso.with(this).load(logoUrl).placeholder(R.mipmap.icon_logo_image61).transform(new PicassoRoundTransform()).resize(60, 60)
                    .centerCrop().into(iv_logo);*/

            Picasso.with(this).load(logoUrl).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(iv_logo);
        } else {
            initProgressDialog(false, "正在创建企业...");
            initTitle("创建企业");
            next_btn.setText("创建企业");
        }
    }

    @OnClick({R.id.next_btn, R.id.rl_logo, R.id.lv_enterprise_create_time})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_enterprise_create_time://注册时间
                hideKeyboard();
                viewData();
                break;
            case R.id.next_btn://创建企业
                enterprise_name = et_enterprise_name.getText().toString().trim();
                create_money = et_create_money.getText().toString().trim();
                number_employees = et_number_employees.getText().toString().trim();
                enterprise_contacts = et_enterprise_contacts.getText().toString();
                enterprise_tel = et_enterprise_tel.getText().toString();
                enterprise_address = et_enterprise_address.getText().toString();
                enterprise_intro = editText.getText().toString().trim();

                if (enterprise_name.length() <= 0) {
                    showErrorToast("企业名称不能为空");
                    return;
                }
                if (startdata.length() <= 0) {
                    showErrorToast("注册时间不能为空");
                    return;
                }
                if (create_money.length() <= 0) {
                    showErrorToast("注册资本不能为空");
                    return;
                }
                if (number_employees.length() <= 0) {
                    showErrorToast("员工人数不能为空");
                    return;
                }
                if (enterprise_contacts.length() <= 0) {
                    showErrorToast("联系人不能为空");
                    return;
                }
                if (enterprise_tel.length() <= 0) {
                    showErrorToast("联系电话不能为空");
                    return;
                }
                if (enterprise_address.length() <= 0) {
                    showErrorToast("企业地址不能为空");
                    return;
                }
                if (enterprise_intro.length() <= 0) {
                    showErrorToast("企业简介不能为空");
                    return;
                }
                if (type.equals("1")) {
                    if (logoUrl.equals("") && logoPath.equals("")) {
                        showErrorToast("请上传公司logo");
                        return;
                    }
                } else {
                    if (logoPath.length() <= 0) {
                        showErrorToast("请上传公司logo");
                        return;
                    }
                }

                if (!logoPath.equals("")) {
                    progress.show();
                    new Thread(new UpdateStringRun(logoPath)).start();
                } else if (selectedPicture.size() > 0) {
                    finalLogo = logoUrl;
                    progress.show();
                    new Thread(new UpdateStringRun2(thumbPictures)).start();
                } else {
                    finalLogo = logoUrl;
                    progress.show();
                    new Thread(new SubmitRun()).start();
                }
                break;
            case R.id.rl_logo://上传logo
                /*setCropSingleImage(true);
                setSingleImage(true);
                setCropTaskPhoto(true);*/
                showWindowSelectList(v);
                break;
            default:
                break;
        }
    }

    private void viewData() {
        final Calendar time = Calendar.getInstance(Locale.CHINA);
        LinearLayout dateTimeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_date_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.new_act_date_picker);
        DatePicker.OnDateChangedListener dateListener = new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                time.set(Calendar.YEAR, year);
                time.set(Calendar.MONTH, monthOfYear);
                time.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            }
        };

        datePicker.init(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), dateListener);

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择注册时间").setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        time.set(Calendar.YEAR, datePicker.getYear());
                        time.set(Calendar.MONTH, datePicker.getMonth());
                        time.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        Date datatime = time.getTime();
                        startdata = format.format(datatime);
                        tv_enterprise_create_time.setText(startdata);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    protected void requestImage(String[] path) {
        /*if (!logoPath.equals("")) {
            // 删除临时的100K左右的图片
            File thumbnailPhoto = new File(logoPath);
            thumbnailPhoto.deleteOnExit();
        }*/
        if (!TextUtils.isEmpty(logoPath)) {
            // 删除临时的100K左右的图片(企业logo)
            File thumbnailPhoto = new File(logoPath);
            if (thumbnailPhoto.exists())
                thumbnailPhoto.delete();
        }


        logoPath = path[0];
        logoUrl = "";
        Log.e("Log", logoPath);
        // 加载图片
        /*Picasso.with(this).load(new File(logoPath)).placeholder(R.mipmap.icon_logo_image61).transform(new PicassoRoundTransform()).resize(60, 60)
                .centerCrop().into(iv_logo);*/
        Picasso.with(this)
                .load(new File(logoPath))
                .resize(DisplayUtil.dip2px(context, 70), DisplayUtil.dip2px(context, 70))
                .centerCrop()
                .placeholder(R.mipmap.icon_logo_image61)
                .error(R.mipmap.icon_logo_image61)
                .into(iv_logo);
    }

    //上传logo
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
            ImageResponse retMap = null;
            try {
                String url = Url.UPLOADIMAGE;
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
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




    //上传多张图片
    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun2 implements Runnable {
        private ArrayList<String> thumbPictures;

        // thumbPictures 是 List<压缩图路径>
        public UpdateStringRun2(ArrayList<String> thumbPictures) {
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
                        4, retMap));
            } else {
                handler.sendMessage(handler
                        .obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
            }
        }
    }




    private class SubmitRun implements Runnable {

        @Override
        public void run() {
            BaseResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("enterprise_name", enterprise_name);
                data.put("enterprise_logo", finalLogo);
                data.put("enterprise_imgs", imgsUrl);
                data.put("enterprise_reg_date", startdata);
                data.put("enterprise_reg_money", create_money);
                data.put("enterprise_staff_num", number_employees);
                data.put("enterprise_intro", enterprise_intro);
                data.put("enterprise_contacts", enterprise_contacts);
                data.put("enterprise_tel", enterprise_tel);
                data.put("enterprise_address", enterprise_address);
                data.put("lng", longitude + "");
                data.put("lat", latitude + "");

                // 一次http请求将所有图片+参数上传
                String url;
                if (type.equals("1")) {
                    url = Url.ENTERPRISEADD;
                } else {
                    url = Url.ENTERPRISEA_EDIT;
                }
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), url + "?XDEBUG_SESSION_START=1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
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
            EnterpriseCreateActivity activity = (EnterpriseCreateActivity) reference.get();
            if (activity == null) {
                return;
            }

            if (isDestroyed())
                return;

            switch (msg.what) {
                case 3://上传logo回调
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        finalLogo = imgResponse.getData().get(0).getOrigin();

                        if (selectedPicture.size() > 0)
                            new Thread(new UpdateStringRun2(thumbPictures)).start();
                        else
                            new Thread(new SubmitRun()).start();

                    } else {
                        progress.dismiss();
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;

                case 4://上传多张图片
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        imgsUrl = imags(imgResponse.getData());
                        new Thread(new SubmitRun()).start();
                    } else {
                        progress.dismiss();
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;

                case NETWORK_SUCCESS_PAGER_RIGHT://创建企业回调
                    progress.dismiss();
                    BaseResponse userResponse = (BaseResponse) msg.obj;
                    if (userResponse.isSuccess()) {
                        if (type.equals("1")) {
                            showErrorToast("修改成功");
                            setResult(Const.RESULT_CODE_NEED_REFRESH);
                        } else {
                            showErrorToast("创建成功");
                        }

                        activity.finish();
                    } else {
                        showErrorToast(userResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
                default:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
            }
        }
    }


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null)
            progress.dismiss();

        if (!TextUtils.isEmpty(logoPath)) {
            // 删除临时的100K左右的图片(企业logo)
            File thumbnailPhoto = new File(logoPath);
            if (thumbnailPhoto.exists())
                thumbnailPhoto.delete();
        }
    }




















    /**
     * 以下是编辑企业logo调用部分
     */


    private void init() {
        int nextsize = random.nextInt(20);
        Log.e("log",nextsize+"随机数");
        file = new File(Applibrary.IMAGE_CACHE_DIR, System.currentTimeMillis() + nextsize + ".jpg");
        imageUri= Uri.fromFile(file);

        cameraFile = new File(cameraPath, System.currentTimeMillis() + random.nextInt(30) + ".jpg");
    }


    public void showWindowSelectList(View v) {
        init();
        View view = getLayoutInflater().inflate(R.layout.bottom_photo_select_popupwindow, null);
        final PopupWindow pop = new PopupWindow(this);
        pop.setAnimationStyle(R.style.BottomPopupAnimation);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        backgroundAlpha(0.4f);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        pop.setBackgroundDrawable(dw);
        pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);


        View camera = view.findViewById(R.id.btn_take_photo);
        View photo = view.findViewById(R.id.btn_pick_photo);
        View cancel = view.findViewById(R.id.btn_cancel);
        pop.setOnDismissListener(new popupDismissListener());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                startPickImage();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                startTakePhoto();
            }
        });
    }


    class popupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }


    /**
     * 相册单选模式
     */
    private void startPickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }


    /**
     * 开始照相
     */
    public void startTakePhoto() {

        // 检测sd是否可用
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "SD卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(this, Const.FILE_PROVIDER, cameraFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        }

        startActivityForResult(intent, REQUEST_TAKE_PHOTO);

    }



    /**
     * 裁剪
     */
    public void startPhotoZoom(Uri uri) {
        if(uri != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "image/*");
            //intent.setDataAndType(Uri.parse("file:///" + AppTool.getImageAbsolutePath(this, uri)), "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            intent.putExtra("outputX", aspectX * 100);
            intent.putExtra("outputY", aspectY * 100);
            intent.putExtra("return-data", false);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());// 返回格式
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUESTCODE_CUTTING);
        }
    }


    private void saveRequestImage(){
        if (file != null && file.exists()) {
            String path = file.getAbsolutePath();
            requestImage(new String[]{path});
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {// 单张 图片 ，拍照后返回

            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: // 拍照
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri uri = FileProvider.getUriForFile(this, Const.FILE_PROVIDER, cameraFile);//通过FileProvider创建一个content类型的Uri
                            startPhotoZoom(uri);
                        } else {
                            startPhotoZoom(Uri.fromFile(cameraFile));
                        }
                    break;
                case REQUEST_CODE_PICK_IMAGE: //单选
                        startPhotoZoom(data.getData());
                    break;
                case REQUESTCODE_CUTTING://裁剪后相片
                    saveRequestImage();
                    break;
            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    private static final int REQUEST_CODE_PICK_IMAGE = 160;
    private static final int REQUESTCODE_CUTTING = 150;
    public static final int REQUEST_TAKE_PHOTO = 120;

    private File file;

    private Random random = new Random();

    private Uri imageUri;

    // 裁剪比例
    private int aspectX = 5;
    private int aspectY = 5;
}
