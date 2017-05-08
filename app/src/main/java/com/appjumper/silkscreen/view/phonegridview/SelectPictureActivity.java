package com.appjumper.silkscreen.view.phonegridview;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class SelectPictureActivity extends BaseActivity {

	/**
	 * 最多选择图片的个数
	 */
	public static final int MAX_NUM = 9;

	public static final String INTENT_MAX_NUM = "intent_max_num";
	public static final String INTENT_SELECTED_PICTURE = "intent_selected_picture";

	private Context context;
	private GridView gridview;
	private PictureAdapter adapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashMap<String, Integer> tmpDir = new HashMap<String, Integer>();
	private ArrayList<ImageFloder> mDirPaths = new ArrayList<ImageFloder>();

	private ContentResolver mContentResolver;
	private Button btn_select;
	private TextView btn_ok;
	private ListView listview;
	private FolderAdapter folderAdapter;
	private ImageFloder imageAll, currentImageFolder;

	/**
	 * 已选择的图片
	 */
	private ArrayList<String> selectedPicture;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_picture);
		selectedPicture = (ArrayList<String>) getIntent()
				.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
		if(selectedPicture.size()!=0){
			if(selectedPicture.get((selectedPicture.size()-1)).equals("2130837643")){
				selectedPicture.remove((selectedPicture.size()-1));	
			}
		}
		context = this;
		mContentResolver = getContentResolver();

		initView();
	}

	public void select(View v) {
		if (listview.getVisibility()==View.VISIBLE) {
			hideListAnimation();
		} else {
			listview.setVisibility(View.VISIBLE);
			showListAnimation();
			folderAdapter.notifyDataSetChanged();
		}
	}

	public void showListAnimation() {
		TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 1f, 1,
				0f);
		ta.setDuration(200);
		listview.startAnimation(ta);
	}

	public void hideListAnimation() {
		TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 0f, 1,
				1f);
		ta.setDuration(200);
		listview.startAnimation(ta);
		ta.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				listview.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 点击完成按钮
	 * 
	 * @version 1.0
	 * @author zyh
	 * @param v
	 */
	public void ok(View v) {
		Intent data = new Intent();
		data.putExtra(INTENT_SELECTED_PICTURE, selectedPicture);
		setResult(RESULT_OK, data);
		this.finish();
	}

	private void initView() {
		imageAll = new ImageFloder();
		imageAll.setDir("/所有图片");
		currentImageFolder = imageAll;
		mDirPaths.add(imageAll);
		btn_ok = (TextView) findViewById(R.id.btn_ok);
		btn_select = (Button) findViewById(R.id.btn_select);
		btn_ok.setText("完成" + selectedPicture.size() + "/"+ MAX_NUM);


		gridview = (GridView) findViewById(R.id.gridview);
		adapter = new PictureAdapter();
		gridview.setAdapter(adapter);

		listview = (ListView) findViewById(R.id.albumlistview);
		folderAdapter = new FolderAdapter();
		listview.setAdapter(folderAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentImageFolder = mDirPaths.get(position);
				hideListAnimation();
				adapter.notifyDataSetChanged();
				btn_select.setText(currentImageFolder.getName());
			}
		});
		getThumbnail();
	}



	public void back(View v) {
		onBackPressed();
	}

	/**
	 * gridview
	 * 
	 * @author dongjin
	 * 
	 */
	class PictureAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return currentImageFolder.images.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.griditem_picture_checked,
						null);
				holder = new ViewHolder();
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);
				holder.checkBox = convertView.findViewById(R.id.check);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkBox.setVisibility(View.VISIBLE);
			final ImageItem item = currentImageFolder.images.get(position);
			Glide.with(SelectPictureActivity.this).load(new File(item.path)).placeholder(R.mipmap.img_error).into(holder.iv);

			holder.checkBox
					.setBackgroundResource(selectedPicture.contains(item.path) ? R.mipmap.icon_selected
							: R.mipmap.icon_unselected);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					final boolean isSelected = selectedPicture.contains(item.path);
					if (!isSelected && selectedPicture.size() + 1 > MAX_NUM) {
						Toast.makeText(context, "最多选择" + MAX_NUM + "张",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (isSelected) {
						selectedPicture.remove(item.path);
						arg0.findViewById(R.id.check).setBackgroundResource(
										R.mipmap.icon_unselected);
					} else {
						selectedPicture.add(item.path);
						arg0.findViewById(R.id.check).setBackgroundResource(
										R.mipmap.icon_selected);
					}
					btn_ok.setText("完成" + selectedPicture.size() + "/"
							+ MAX_NUM);

				}
			});
			return convertView;
		}
	}

	private static class ViewHolder {
		ImageView iv;
		View checkBox;
	}

	/**
	 * listview adapter
	 * 
	 * @author dongjin
	 * 
	 */
	class FolderAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDirPaths.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FolderViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.listitem_grallery,null);
				holder = new FolderViewHolder();
				holder.id_dir_item_image = (ImageView) convertView
						.findViewById(R.id.id_dir_item_image);
				holder.id_dir_item_name = (TextView) convertView
						.findViewById(R.id.id_dir_item_name);
				holder.id_dir_item_count = (TextView) convertView
						.findViewById(R.id.id_dir_item_count);
				holder.choose = (ImageView) convertView
						.findViewById(R.id.choose);
				convertView.setTag(holder);
			} else {
				holder = (FolderViewHolder) convertView.getTag();
			}
			ImageFloder item = mDirPaths.get(position);
//			loader.displayImage("file://" + item.getFirstImagePath(),
//					holder.id_dir_item_image, options);
			Picasso.with(SelectPictureActivity.this).load("file://" + item.getFirstImagePath()).placeholder(R.mipmap.img_error).transform(new PicassoRoundTransform())
					.centerCrop().into(holder.id_dir_item_image);
			holder.id_dir_item_count.setText(item.images.size() + "张");
			holder.id_dir_item_name.setText(item.getName());
			holder.choose.setVisibility(currentImageFolder == item ? View.VISIBLE : View.GONE);
			return convertView;
		}
	}

	private static class FolderViewHolder {
		ImageView id_dir_item_image;
		ImageView choose;
		TextView id_dir_item_name;
		TextView id_dir_item_count;
	}

	/**
	 * 得到缩略图
	 */
	private void getThumbnail() {
		Cursor mCursor = mContentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.ImageColumns.DATA }, "", null,
				MediaStore.MediaColumns.DATE_ADDED + " DESC");
		// Log.e("TAG", mCursor.getCount() + "");
		if (mCursor.moveToFirst()) {
			int _date = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
			do {
				// 获取图片的路径
				String path = mCursor.getString(_date);
				// Log.e("TAG", path);
				imageAll.images.add(new ImageItem(path));
				// 获取该图片的父路径名
				File parentFile = new File(path).getParentFile();
				if (parentFile == null) {
					continue;
				}
				ImageFloder imageFloder = null;
				String dirPath = parentFile.getAbsolutePath();
				if (!tmpDir.containsKey(dirPath)) {
					// 初始化imageFloder
					imageFloder = new ImageFloder();
					imageFloder.setDir(dirPath);
					imageFloder.setFirstImagePath(path);
					mDirPaths.add(imageFloder);
					// Log.d("zyh", dirPath + "," + path);
					tmpDir.put(dirPath, mDirPaths.indexOf(imageFloder));
				} else {
					imageFloder = mDirPaths.get(tmpDir.get(dirPath));
				}
				imageFloder.images.add(new ImageItem(path));
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		tmpDir = null;
	}

	class ImageFloder {
		/**
		 * 图片的文件夹路径
		 */
		private String dir;

		/**
		 * 第一张图片的路径
		 */
		private String firstImagePath;
		/**
		 * 文件夹的名称
		 */
		private String name;

		public List<ImageItem> images = new ArrayList<ImageItem>();

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
			int lastIndexOf = this.dir.lastIndexOf("/");
			this.name = this.dir.substring(lastIndexOf);
		}

		public String getFirstImagePath() {
			return firstImagePath;
		}

		public void setFirstImagePath(String firstImagePath) {
			this.firstImagePath = firstImagePath;
		}

		public String getName() {
			return name;
		}

	}

	class ImageItem {
		String path;

		public ImageItem(String p) {
			this.path = p;
		}
	}

}
