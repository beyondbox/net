package com.appjumper.silkscreen.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;


/**
 * 版本更新对话框
 * botx
 */
public class UpdateDialog extends Dialog {

	public UpdateDialog(Context context) {
		super(context);
	}

	public UpdateDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder {
		private Context context;
		private String message;
		private OnClickListener positiveListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setPositiveListener(OnClickListener positiveListener) {
			this.positiveListener = positiveListener;
			return this;
		}

		public UpdateDialog create() {
			final UpdateDialog dialog = new UpdateDialog(context, R.style.CustomDialog);
			LayoutInflater inflater = LayoutInflater.from(context);
			View contentView = inflater.inflate(R.layout.dialog_update, null);

			//内容
			if (!TextUtils.isEmpty(message)) {
				TextView txtMessage = (TextView) contentView.findViewById(R.id.txtContent);
				txtMessage.setText(message);
			}
			
			//确定按钮
			TextView txtPositive = (TextView) contentView.findViewById(R.id.txtConfirm);
			txtPositive.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (positiveListener != null) {
						positiveListener.onClick(dialog, BUTTON_POSITIVE);
					}
				}
			});

			//取消按钮
			ImageView imgViClose = (ImageView) contentView.findViewById(R.id.imgViClose);
			imgViClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.setContentView(contentView);
			
			//设置宽度
			Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
			LayoutParams params = dialog.getWindow().getAttributes();
			params.width = (int) (display.getWidth() * 0.9);
			dialog.getWindow().setAttributes(params);
			dialog.getWindow().getAttributes().gravity = Gravity.CENTER;

			return dialog;
		}
	}

}
