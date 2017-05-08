package com.appjumper.silkscreen.view.banner;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

/**
 * ImageView创建工厂
 */
public class ViewFactory {

	/**
	 * 获取ImageView视图的同时加载显示url
	 * 
	 * @param
	 * @return
	 */
	public static ImageView getImageView(Context context, String url) {
		ImageView imageView = (ImageView)LayoutInflater.from(context).inflate(
				R.layout.view_banner, null);
		Glide.with(context).load(url).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(imageView);
		return imageView;
	}
}
