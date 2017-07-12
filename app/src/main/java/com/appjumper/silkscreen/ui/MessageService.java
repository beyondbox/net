package com.appjumper.silkscreen.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.ui.money.MessageActivity;
import com.appjumper.silkscreen.util.LogHelper;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.NOTIFICATION_SERVICE;


public class MessageService extends XGPushBaseReceiver {

	private int notifyId = 0;

	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
	}


	@Override
	public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {

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
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			// APP自主处理的过程。。。
//			context.startActivity(new Intent(context, MessageActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

		} else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
			// 通知被清除啦。。。。
			// APP自己处理通知被清除后的相关动作
		}
	}

	@Override
	public void onNotifactionShowedResult(final Context context, XGPushShowedResult arg1) {
	}

	@Override
	public void onRegisterResult(Context arg0, int arg1, XGPushRegisterResult arg2) {

	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
	}

	// 消息透传
	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		String text = "收到消息:" + message.toString();
		LogHelper.e("Notification", text);

		Intent intent = new Intent(context, MessageActivity.class);
		makeNotify(context, intent, message.getTitle(), message.getContent());



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


	}


	@Override
	public void onUnregisterResult(Context arg0, int arg1) {

	}



	private void makeNotify(Context context, Intent intent, String title, String content) {
		NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentTitle(title)
				.setContentText(content)
				.setTicker(title)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
				.setContentIntent(pendingIntent);
		Notification notification = builder.build();

		notification.flags = Notification.FLAG_AUTO_CANCEL;
		nm.notify(notifyId++, notification);
	}


}
