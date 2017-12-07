package com.appjumper.silkscreen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.HomeDataResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;

import java.util.HashMap;
import java.util.Map;

/**
 * 欢迎页
 */
public class WelcomeActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		//Context context = getApplicationContext();
		//XGPushManager.registerPush(context);

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
			switch (msg.what) {
				case NETWORK_SUCCESS_PAGER_RIGHT:
					HomeDataResponse detailsResponse = (HomeDataResponse) msg.obj;
					if (detailsResponse.isSuccess()) {
						getMyApplication().getMyUserManager().storeHomeInfo(detailsResponse);
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(300);
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
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(300);
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

}
