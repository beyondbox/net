package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.HomeBanner;
import com.appjumper.silkscreen.net.Url;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 轮播图适配器
 * @author Botx
 *
 */
public class BannerAdapter extends PagerAdapter {
	
	private List<View> views;
	private List<HomeBanner> dataList;
	private Context context;

	public BannerAdapter(Context context, List<View> views, List<HomeBanner> dataList) {
		this.context = context;
		this.views = views;
		this.dataList = dataList;
	}
	
	@Override
	public int getCount() {
		return (views != null) ? views.size() : 0;
	}
	
	public View getItem(int position) {
		return (views != null) ? views.get(position) : null;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		ImageView imgView = (ImageView) views.get(position);
		((ViewPager)container).addView(imgView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		int count = dataList.size();
		if (position == 0)
			position = count - 1;
		else if (position == count + 1)
			position = 0;
		else
			position = position - 1;

		String imgPath = dataList.get(position).getSlide_pic().getOrigin();
		String url = imgPath.startsWith("http") ? imgPath : Url.IP + "/data/upload/" + imgPath;

		Picasso.with(context)
				.load(url)
				.placeholder(R.mipmap.img_error)
				.error(R.mipmap.img_error)
				.into(imgView);

		return imgView;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager)container).removeView(views.get(position));
	}

}
