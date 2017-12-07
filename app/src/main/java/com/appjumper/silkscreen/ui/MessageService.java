package com.appjumper.silkscreen.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.ui.money.MessageActivity;
import com.appjumper.silkscreen.ui.my.MyReleaseActivity;
import com.appjumper.silkscreen.ui.my.audit.AuditAskBuyActivity;
import com.appjumper.silkscreen.ui.my.audit.AuditDriverActivity;
import com.appjumper.silkscreen.ui.my.audit.AuditFreightActivity;
import com.appjumper.silkscreen.ui.my.audit.AuditPersonActivity;
import com.appjumper.silkscreen.ui.my.deliver.DeliverOrderListActivity;
import com.appjumper.silkscreen.ui.my.driver.DriverOrderListActivity;
import com.appjumper.silkscreen.util.Const;
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

	private static int notifyId = 0;

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
			/*String customContent = message.getCustomContent();
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
			}*/


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

		Intent intent = null;

		// 获取自定义的key-value
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				JSONObject jsonObj = new JSONObject(customContent);
				if (jsonObj.has("type")) {
					int type = jsonObj.optInt("type");
					switch (type) {
						case Const.PUSH_NEW_INQUIRY: //有新的询价
							intent = new Intent(context, MessageActivity.class);
							break;
						case Const.PUSH_NEW_OFFER: //有新的报价
							intent = new Intent(context, MessageActivity.class);
							break;
						case Const.PUSH_FREIGHT_NEW_INQUIRY: //空车配货--司机收到发货厂家的询价
							intent = new Intent(context, DriverOrderListActivity.class);
							break;
						case Const.PUSH_ASKBUY_CHOOSE_OFFER: //求购-选择报价
							intent = new Intent(context, MyReleaseActivity.class);
							break;
						case Const.PUSH_ASKBUY_CHOOSED: //求购-报价被采纳
							intent = new Intent(context, MessageActivity.class);
							break;
						case Const.PUSH_FREIGHT_NEW_OFFER: //空车配货-有司机报价
							intent = new Intent(context, DeliverOrderListActivity.class);
							break;
						case Const.PUSH_FREIGHT_CHOOSED: //空车配货-司机被采纳
							intent = new Intent(context, DriverOrderListActivity.class);
							break;
						case Const.PUSH_FREIGHT_DRIVER_PAYED: //空车配货-司机已支付
							intent = new Intent(context, DeliverOrderListActivity.class);
							break;
						case Const.PUSH_FREIGHT_DRIVER_ARRIVED: //空车配货-司机已送达
							intent = new Intent(context, DeliverOrderListActivity.class);
							break;
						case Const.PUSH_AUDIT_ASKBUY: //快速审核-求购信息
							intent = new Intent(context, AuditAskBuyActivity.class);
							break;
						case Const.PUSH_AUDIT_FREIGHT: //快速审核-空车配货
							intent = new Intent(context, AuditFreightActivity.class);
							break;
						case Const.PUSH_AUDIT_PERSON: //快速审核-个人认证
							intent = new Intent(context, AuditPersonActivity.class);
							break;
						case Const.PUSH_AUDIT_DRIVER: //快速审核-司机认证
							intent = new Intent(context, AuditDriverActivity.class);
							break;
					}

					if (intent != null) {
						intent.putExtra(Const.KEY_TYPE, type);
						intent.putExtra("id", jsonObj.optInt("id") + "");
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		makeNotify(context, intent, message.getTitle(), message.getContent());
	}


	@Override
	public void onUnregisterResult(Context arg0, int arg1) {

	}



	private void makeNotify(Context context, Intent intent, String title, String content) {
		NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		if (intent == null)
			intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentTitle(title)
				.setContentText(content)
				.setTicker(title)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
				.setContentIntent(pendingIntent)
				.setDefaults(Notification.DEFAULT_ALL);
		Notification notification = builder.build();

		notification.flags = Notification.FLAG_AUTO_CANCEL;
		nm.notify(notifyId++, notification);
	}


}
