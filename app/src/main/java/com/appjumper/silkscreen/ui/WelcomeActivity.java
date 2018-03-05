package com.appjumper.silkscreen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.HomeDataResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.SPUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 欢迎页
 */
public class WelcomeActivity extends BaseActivity {

	@Bind(R.id.imgViWelcome)
	ImageView imgViWelcome;
	@Bind(R.id.imgViGuide)
	ImageView imgViGuide;

	private int oldCode;
	private int currCode;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppTool.setFullScreen(this);
		setContentView(R.layout.activity_welcome);
		ButterKnife.bind(context);
		//Context context = getApplicationContext();
		//XGPushManager.registerPush(context);

		oldCode = SPUtil.getInt(null, Const.KEY_VERSION_CODE, 0);
		currCode = getVersionCode();

		if (oldCode != currCode) {
			imgViWelcome.setVisibility(View.GONE);
			imgViGuide.setVisibility(View.VISIBLE);
			SPUtil.putInt(null, Const.KEY_VERSION_CODE, currCode);
		} else {
			imgViGuide.setVisibility(View.GONE);
			imgViWelcome.setVisibility(View.VISIBLE);
		}

		new Thread(new HomeDataRun()).start();
	}

	//首页
	private class HomeDataRun implements Runnable {
		private HomeDataResponse response;

		@SuppressWarnings("unchecked")
		public void run() {
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("uid", getUserID());
				response = JsonParser.getHomeDataResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), Url.HOMEDATA));
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

	private MyHandler handler = new MyHandler();

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (isDestroyed()) return;

			switch (msg.what) {
				case NETWORK_SUCCESS_PAGER_RIGHT:
					HomeDataResponse detailsResponse = (HomeDataResponse) msg.obj;
					if (detailsResponse.isSuccess()) {
						getMyApplication().getMyUserManager().storeHomeInfo(detailsResponse);
					}

					if (oldCode != currCode) return;

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(200);
								startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
								WelcomeActivity.this.finish();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
					break;
				case NETWORK_SUCCESS_DATA_ERROR:

					showErrorToast();
					break;
				default:
					if (oldCode != currCode) return;
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(200);
								startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
								WelcomeActivity.this.finish();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
//					showErrorToast();
					break;
			}
		}
	};


	@OnClick(R.id.imgViGuide)
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.imgViGuide:
				start_Activity(context, MainActivity.class);
				finish();
				break;
		}
	}

}
