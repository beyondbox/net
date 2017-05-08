package com.appjumper.silkscreen.ui;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MessageService extends XGPushBaseReceiver {

	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
	}



	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {


		if (context == null || message == null) {
			return;
		}
		if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
			// 获取自定义key-value
			String customContent = message.getCustomContent();
			if (customContent != null && customContent.length() != 0) {
				try {
					JSONObject obj = new JSONObject(customContent);
					// key1为前台配置的key
					if (!obj.isNull("status")) {
						String value = obj.getString("status");
						String title = obj.getString("title");
//						Intent intent = new Intent(context, OrderActivity.class);
//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						intent.putExtra("status", value);
//						intent.putExtra("title", title);
//						context.startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			// APP自主处理的过程。。。
//			Toast.makeText(context, text, duration)
//			context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		} else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
			// 通知被清除啦。。。。
			// APP自己处理通知被清除后的相关动作
		}
	}

	@Override
	public void onNotifactionShowedResult(final Context context, XGPushShowedResult arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRegisterResult(Context arg0, int arg1, XGPushRegisterResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
	}
	// 消息透传
	@Override
	public void onTextMessage(final Context context, XGPushTextMessage message) {
		String text = "收到消息:" + message.toString();
		// 获取自定义key-value
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				JSONObject obj = new JSONObject(customContent);
				// key1为前台配置的key
				if (!obj.isNull("status")) {
					final String value = obj.getString("status");
					final String title = obj.getString("title");

				}
				// ...
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		Log.e("log", text);
		show(context, text);
	}
	private void show(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
