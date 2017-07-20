package com.appjumper.silkscreen.ui.common;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.Applibrary;
import com.appjumper.silkscreen.util.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 选择多张照片，拍照
 */
public abstract class MultiSelectPhotoActivity extends BaseActivity {

    /**
     * 拍照
     */
    public static final int REQUEST_TAKE_PHOTO = 12;
    public static final int REQUEST_SELECT_PHOTO = 13;
    private static final int REQUESTCODE_CUTTING = 15;
    private static final int REQUEST_CODE_PICK_IMAGE = 16;
    protected int mMaxImageNum = 8;
    public ArrayList<String> mPhotoList = new ArrayList<>();
    public static final int MAX_IMAGE_NUM = 8;
    // 相册选择模式,是否支持多选
    protected boolean mSingleImage = false;
    // 照相模式,是否需要裁剪
    private boolean mCropTaskPhoto = false;
    // 相册单选模式,是否需要裁剪
    private boolean mCropSingleImage = false;
    // 裁剪比例
    private int aspectX = 5;
    private int aspectY = 5;
    private Uri imageUri;
    private File file;
    private Random random;

    private File cameraFile;
    private String cameraPath = Environment.getExternalStorageDirectory().getPath() + "/" + "picture";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         random=new Random();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Const.REQUEST_CODE_PERMISSION);
    }

    private void init(){
        int nextsize = random.nextInt(20);
        Log.e("log",nextsize+"随机数");
        file = new File(Applibrary.IMAGE_CACHE_DIR, System.currentTimeMillis() + nextsize + ".jpg");
        imageUri= Uri.fromFile(file);

        cameraFile = new File(cameraPath, System.currentTimeMillis() + random.nextInt(30) + ".jpg");
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
        /*if(mCropTaskPhoto){
            startCropTakePhoto();
        }else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }*/

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

    public void startCropTakePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

  /*  public void showDeleteWindow(final ItemOnClick mItemOnClick, View v) {
        final View view = getLayoutInflater().inflate(R.layout.item_popup_delete, null);
        final PopupWindow pop = new PopupWindow(this);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.activity_translate_in));
        pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        View delete = view.findViewById(R.id.btn_delete);
        View cancel = view.findViewById(R.id.btn_cancel);
        View holder = view.findViewById(R.id.view_holder);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                view.clearAnimation();
                if (mItemOnClick != null) {
                    mItemOnClick.onClick(1, v);
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                view.clearAnimation();
                if (mItemOnClick != null) {
                    mItemOnClick.onClick(0, v);
                }

            }
        });
    }*/

    public interface ItemOnClick {
        void onClick(int position, View v);
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
                if (mSingleImage) {
                    startPickImage();
                } else {
//                    ImageBrowseActivity.openImageBrowser(MultiSelectPhotoActivity.this, mMaxImageNum);
                }
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
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {// 单张 图片 ，拍照后返回
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: // 拍照
                    if(mCropTaskPhoto){
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri uri = FileProvider.getUriForFile(this, Const.FILE_PROVIDER, cameraFile);//通过FileProvider创建一个content类型的Uri
                            startPhotoZoom(uri);
                        } else {
                            startPhotoZoom(Uri.fromFile(cameraFile));
                        }

                    }else{
                        saveRequestImage();
                    }
                    break;
                case REQUEST_SELECT_PHOTO: // 多选
                    //String imagepath[] = data.getStringArrayExtra(ImageBrowseActivity.RESULT_IMAGE_URL);
                    //requestImage(imagepath);
                    break;
                case REQUEST_CODE_PICK_IMAGE: //单选
                    if(mCropSingleImage){//裁剪
                        startPhotoZoom(data.getData());
                    }else{
                        Uri uri = data.getData();
                        String path = getRealPathFromURI(uri);
                        requestImage(new String[]{path});
                    }
                    break;
                case REQUESTCODE_CUTTING://裁剪后相片
                    saveRequestImage();
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void saveRequestImage(){
        if (file != null && file.exists()) {
            String path = file.getAbsolutePath();
            requestImage(new String[]{path});
        }
    }

    private void bitmapToJpg(Intent data){
        String path = saveBitmapToJPG(data);
        if(!TextUtils.isEmpty(path)){
            requestImage(new String[]{path});
        }
    }

    private String saveBitmapToJPG(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            FileOutputStream b = null;
            File file = new File(Applibrary.IMAGE_CACHE_DIR);
            /* 检测文件夹是否存在，不存在则创建文件夹 */
            if (!file.exists() && !file.isDirectory())
                file.mkdirs();
            String fileName = file.getPath() + "/" + System.currentTimeMillis() + ".jpg";
            Log.i(MultiSelectPhotoActivity.class.getSimpleName(), "camera file path:" + fileName);
            try {
                b = new FileOutputStream(fileName);
                // 把数据写入文件
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(b != null){
                        b.flush();
                        b.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    protected abstract void requestImage(String path[]);

    public boolean isSingleImage() {
        return mSingleImage;
    }

    public void setSingleImage(boolean mSingleImage) {
        this.mSingleImage = mSingleImage;
    }

    public int getAspectY() {
        return aspectY;
    }

    public void setAspect(int aspectX, int aspectY) {
        this.aspectY = aspectY;
        this.aspectX = aspectX;
    }

    public int getAspectX() {
        return aspectX;
    }

    public boolean isCropSingleImage() {
        return mCropSingleImage;
    }

    public void setCropSingleImage(boolean mCropSingleImage) {
        this.mCropSingleImage = mCropSingleImage;
    }

    public boolean isCropTaskPhoto() {
        return mCropTaskPhoto;
    }

    public void setCropTaskPhoto(boolean mCropTaskPhoto) {
        this.mCropTaskPhoto = mCropTaskPhoto;
    }

    public void setPhotoModel(boolean isSingleImage, boolean isCropTaskPhoto, boolean isCropSingleImage){
        this.mCropSingleImage = isCropSingleImage;
        this.mSingleImage = isSingleImage;
        this.mCropTaskPhoto = isCropTaskPhoto;
    }
}
