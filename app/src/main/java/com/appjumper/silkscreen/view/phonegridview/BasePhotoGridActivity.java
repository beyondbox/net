package com.appjumper.silkscreen.view.phonegridview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.MyGridView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 需要选择 多张图片功能 的界面，继承此类
 *
 * 子类只需要调用showBottomPopupWin方法，即可在底部弹出 拍照/相册 框
 */
public class BasePhotoGridActivity extends BaseActivity implements PhotoPopupWindow.SelectPhotoListener {
	/** 从媒体库选择图片 */
	public final int PHOTO_PICKED_WITH_DATA = 0x10;
	/** 照相选择图片 */
	public final int PHOTO_CAMERA_WITH_DATA = 0x17;
	/** 剪切图片 */
	public final int PHOTO_CROP_PATH = 0x18;
	public final String SAVEPATH = Environment
			.getExternalStorageDirectory().getPath() + "/" + "picture";
	public static final int PICTURE_UPDATE_ICON = R.drawable.icon_uploading;


	private final int REQUEST_PICK = 0;
	private MyGridView gridView;

	//调用拍照，拍出来的原图的url
	private String camera_pic_path;

	// 选择的原图路径
	protected ArrayList<String> selectedPicture = new ArrayList<String>();

	// 压缩后的临时路径
	public ArrayList<String> thumbPictures = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initProgressDialog(false, null);
		if (savedInstanceState != null) {
			camera_pic_path = savedInstanceState.getString("camera_pic_path");
		}

		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Const.REQUEST_CODE_PERMISSION);
	}

	protected void initView() {
		thumbPictures.add("" + PICTURE_UPDATE_ICON);
		gridView = (MyGridView) findViewById(R.id.gridview);
		gridView.setAdapter(new PhotoReleaseGridAdapter(thumbPictures, this));

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				if (thumbPictures.get(arg2).equals("" + PICTURE_UPDATE_ICON)) {
					showBottomPopupWin();
				} else {
					Intent intent = new Intent(BasePhotoGridActivity.this, GalleryActivity.class);
					ArrayList<String> urls = new ArrayList<String>();
					for (String string : selectedPicture) {
						urls.add("file://" + string);
					}
					intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
					intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, arg2);
					startActivity(intent);
				}
			}
		});
	}

	// 采用android官方处理bitmap方式，完美解决OOM问题
	// 在子线程中 将选择的大图 -> 压缩 + 旋转 + 释放bitmap + 重新存储为临时图片
	private class CompressRun implements Runnable {
		private ArrayList<String> orgPictures;

		// orgPictures 是 刚刚选择的List<原图路径>
		public CompressRun(ArrayList<String> orgPictures) {
			this.orgPictures = orgPictures;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				//不要在子线程里修改全局变量！所以这里用临时变量
				final  ArrayList<String> tempThumbPictures = new ArrayList<String>();

				//原图必须要先压缩，重新保存，thumbnailPictures压缩后的图片路径
				for (String orgPath : orgPictures) {
					// 从picturePath取出图片 + 第一步压缩 + 旋转
					ImageCompress compress = new ImageCompress();
					ImageCompress.CompressOptions options = new ImageCompress.CompressOptions();
					options.filePath = orgPath;
					Bitmap mBitmap = compress.compressFromUri(
							BasePhotoGridActivity.this, options);
					if (mBitmap == null) {// 跳过错误的图片
						continue;
					}
					// 将上一步的图片再次压缩到100K左右（和微信微博一致） 并保存到自己的新目录下，返回新的全路径
					String newPicturePath = BitmapUtil
							.saveMyBitmapWithCompress(orgPath, mBitmap, 80);
					tempThumbPictures.add(newPicturePath);
				}
				// 处理完毕，回到主线程显示在界面上
				BasePhotoGridActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						progress.dismiss();
						//清除上次的 压缩图
						clearCurreantThumbList();
						//重新显示这次选择的
						thumbPictures.addAll(0, tempThumbPictures);
						PhotoReleaseGridAdapter adapter = new PhotoReleaseGridAdapter(thumbPictures,BasePhotoGridActivity.this);
						gridView.setAdapter(adapter);
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void showBottomPopupWin() {
		hideKeyboard();
		LayoutInflater mLayoutInfalter = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout mPopView = (LinearLayout) mLayoutInfalter.inflate(
				R.layout.bottom_photo_select_popupwindow, null);
		PhotoPopupWindow mPopupWin = new PhotoPopupWindow(this, mPopView);
		backgroundAlpha(0.4f);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		//设置SelectPicPopupWindow弹出窗体的背景
		mPopupWin.setBackgroundDrawable(dw);
		mPopupWin.setOnDismissListener(new popupDismissListener());
		mPopupWin.setAnimationStyle(R.style.BottomPopupAnimation);
		mPopupWin.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0,
				0);
		mPopupWin.setOnSelectPhotoListener(this);
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
	/** startActivityForResult方式选择图片，onActivityResult接收返回的图片文件 */
	private void pickPhotoFromCamera() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			showErrorToast("请插入sd卡");
			return;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String name = DateFormat.format("yyyyMMddhhmmss",
				Calendar.getInstance(Locale.CHINA))
				+ ".jpg";

		File file = new File(SAVEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		camera_pic_path = SAVEPATH + "/" + name;
		File mCurrentPhotoFile = new File(camera_pic_path);


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri uri = FileProvider.getUriForFile(this, Const.FILE_PROVIDER, mCurrentPhotoFile);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		} else {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
		}


		startActivityForResult(intent, PHOTO_CAMERA_WITH_DATA);
	}


	//从相册选择图片
	@Override
	public void onGallery(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(BasePhotoGridActivity.this, SelectPictureActivity.class);
		i.putExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE, selectedPicture);
		startActivityForResult(i, REQUEST_PICK);
	}

	//调用系统拍照
	@Override
	public void onCamera(View v) {
		// TODO Auto-generated method stub
		pickPhotoFromCamera();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PHOTO_CAMERA_WITH_DATA://拍照成功
					selectedPicture.add(camera_pic_path);
					// 在子线程中处理，并展示
					new Thread(new CompressRun(selectedPicture)).start();
					break;
				case REQUEST_PICK: //从相册选择图片集合
					selectedPicture = (ArrayList<String>) data.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
					// 在子线程中处理，并展示
					new Thread(new CompressRun(selectedPicture)).start();
					break;
				default:
					break;
			}
//			if(progress != null){
//				progress.show();
//			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		clearCurreantThumbList();
		super.onDestroy();
	}

	private void clearCurreantThumbList() {
		// 删除临时的100K左右的图片
		for (String thumbnailPath : thumbPictures) {
			File thumbnailPhoto = new File(thumbnailPath);
			//thumbnailPhoto.deleteOnExit();
			if (thumbnailPhoto.exists())
				thumbnailPhoto.delete();
		}
		thumbPictures.clear();
		thumbPictures.add("" + PICTURE_UPDATE_ICON);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("camera_pic_path", camera_pic_path);
		super.onSaveInstanceState(outState);
	}

	public class PhotoReleaseGridAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		private ArrayList<String> bitmapPathList;
//		private DisplayImageOptions options;

		public PhotoReleaseGridAdapter(ArrayList<String> bitmapPathList,Context mContext) {
			this.bitmapPathList = bitmapPathList;
			this.mLayoutInflater = LayoutInflater.from(mContext);
//			this.options = getITopicApplication().getOtherManage().getRectDisplayImageOptions();
		}

		@Override
		public int getCount() {
			return bitmapPathList.size() > SelectPictureActivity.MAX_NUM ? SelectPictureActivity.MAX_NUM
					: bitmapPathList.size();
		}

		@Override
		public String getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			MyGridViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.griditem_picture_delete,
						parent, false);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.imageview);
				viewHolder.delete_btn = convertView
						.findViewById(R.id.delete_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyGridViewHolder) convertView.getTag();
			}
			String photoRecourse = bitmapPathList.get(position).equals(
					"" + BasePhotoGridActivity.this.PICTURE_UPDATE_ICON) ? "drawable://" : "file://";

			//加号图片 去掉删除按钮
			viewHolder.delete_btn.setVisibility((bitmapPathList.get(position).equals(
					"" + BasePhotoGridActivity.this.PICTURE_UPDATE_ICON))?View.GONE:View.VISIBLE);

			viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					bitmapPathList.remove(position);
					selectedPicture.remove(position); //删除掉，这样再次进入选择图片，这张图不会被勾
					gridView.setAdapter(new PhotoReleaseGridAdapter(thumbPictures,
							BasePhotoGridActivity.this));
				}
			});
			if(photoRecourse.equals("drawable://")){
				Picasso.with(BasePhotoGridActivity.this).load(R.mipmap.icon_uploading).into(viewHolder.imageView);
			}else{
				Picasso.with(BasePhotoGridActivity.this).load(photoRecourse + bitmapPathList.get(position)).into(viewHolder.imageView);
			}
			Log.e("log",photoRecourse + bitmapPathList.get(position));
//			ImageLoader.getInstance().displayImage(
//					photoRecourse + bitmapPathList.get(position),
//					viewHolder.imageView,options);

			return convertView;
		}
	}
	private static class MyGridViewHolder {
		private ImageView imageView;
		private View delete_btn;
	}


}
