package com.appjumper.silkscreen.view.phonegridview;

import android.content.Context;

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.ui.common.MultiSelectPhotoActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;






/**
 * Collection of utility functions used in this package.
 */
public class BitmapUtil {
	private static final String TAG = "Util";
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_UP = 2;
	public static final int DIRECTION_DOWN = 3;
	public static final String SAVEPATH = Environment
			.getExternalStorageDirectory().getPath() + "/" + "picture";


	/**
	 * 读取旋转角度
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			case ExifInterface.ORIENTATION_NORMAL:

				degree = 0;
				break;
			case ExifInterface.ORIENTATION_UNDEFINED:

				degree = 0;
				break;
			case -1:

				degree = 0;
				break;
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		return degree;
	}


	/**
	 * 旋转
	 * @param bm
	 * @param orientationDegree
	 * @return
	 */
	public static Bitmap adjustPhotoRotation(Bitmap bm,
			final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}
		final float[] values = new float[9];
		m.getValues(values);
		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];
		m.postTranslate(targetX - x1, targetY - y1);
		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),
				Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);
		bm.recycle();
		bm = null;
		return bm1;
	}
	
	public static void resetLayoutParams(Context mContext, View iv,
			Avatar bean) {
		int max_photo_width = (int) mContext.getResources().getDimension(R.dimen.max_photo_width);
		 int width = Integer.parseInt(bean.getWidth());
		 int height = Integer.parseInt(bean.getHeight());
		if (height == 0) {
			bean.setHeight(max_photo_width+"");
		}
		if (width == 0) {
			bean.setWidth(max_photo_width+"");
		}
		if (height <width) {
			// 图片很扁
			iv.getLayoutParams().width = width > max_photo_width ? max_photo_width
					: width;
			iv.getLayoutParams().height = (height * iv
					.getLayoutParams().width) / width;
		} else {
			// 图片很高
			iv.getLayoutParams().height = height > max_photo_width ? max_photo_width
					:height;
			iv.getLayoutParams().width = (width * iv
					.getLayoutParams().height) / height;
		}
	}

	public static String saveMyBitmap(String sourcePath,Bitmap mBitmap) {
		return saveMyBitmapWithCompress(sourcePath, mBitmap, 50);
	}
	

	public static String saveMyBitmapWithCompress(String sourcePath,Bitmap mBitmap,int compress) {
		String newFileName = System.currentTimeMillis() + new Random().nextInt(512) + ".png";
		File file = new File(SAVEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		File f = new File(SAVEPATH + "/" + newFileName);
		try {
			f.createNewFile();
			FileOutputStream fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, compress, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return sourcePath;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return sourcePath;
		}
		return SAVEPATH + "/" + newFileName;
	}


}
