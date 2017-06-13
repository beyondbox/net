package com.appjumper.silkscreen.view.phonegridview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.appjumper.silkscreen.R;
import com.bumptech.glide.Glide;



public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;
	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_gallery_item, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		/*mAttacher = new PhotoViewAttacher(mImageView);
		
		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});*/
		
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(mImageUrl.equals("1")){
			Glide.with(getActivity()).load(R.mipmap.icon_business_license).into(mImageView);
		}else if(mImageUrl.equals("2")){
			Glide.with(getActivity()).load(R.mipmap.icon_front_01).into(mImageView);
		}else if(mImageUrl.equals("3")){
			Glide.with(getActivity()).load(R.mipmap.icon_contrary_02).into(mImageView);
		}else{
			Glide.with(getActivity()).load(mImageUrl).into(mImageView/*, new Callback() {
			@Override
			public void onSuccess() {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onError() {
				Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
			}
		}*/);
		}


	}

}
