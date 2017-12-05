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
 * 自定义提示对话框
 * botx
 */
public class MyAlertDialog extends Dialog {
	
	public MyAlertDialog(Context context) {
		super(context);
	}

	public MyAlertDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder {
		private Context context;
        private int icon;
		private String message;
		private String positiveText;
		private String negativeText;
		private OnClickListener positiveListener;
		private OnClickListener negativeListener;
		
		public Builder(Context context) {
			this.context = context;
		}

		public Builder setIcon(int resourceID) {
            icon = resourceID;
            return this;
        }

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}
		
		public Builder setPositiveButton(String text, OnClickListener listener) {
			positiveText = text;
			positiveListener = listener;
			return this;
		}
		
		public Builder setNegativeButton(String text, OnClickListener listener) {
			negativeText = text;
			negativeListener = listener;
			return this;
		}
		
		public MyAlertDialog create() {
			final MyAlertDialog dialog = new MyAlertDialog(context, R.style.CustomDialog);
			LayoutInflater inflater = LayoutInflater.from(context);
			View contentView = inflater.inflate(R.layout.dialog_my_alert, null);

            //图标
            if (icon != 0) {
                ImageView imgViIcon = (ImageView) contentView.findViewById(R.id.imgViIcon);
                imgViIcon.setImageResource(icon);
            }

			//标题
			if (!TextUtils.isEmpty(message)) {
				TextView txtMessage = (TextView) contentView.findViewById(R.id.txtMessage);
				txtMessage.setText(message);
			}
			
			//确定按钮
			TextView txtPositive = (TextView) contentView.findViewById(R.id.txtConfirm);
			if (TextUtils.isEmpty(positiveText)) {
				txtPositive.setVisibility(View.GONE);
			} else {
				txtPositive.setText(positiveText);
				txtPositive.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						if (positiveListener != null) {
							positiveListener.onClick(dialog, BUTTON_POSITIVE);
						}
					}
				});
			}
			
			//取消按钮
			TextView txtNegative = (TextView) contentView.findViewById(R.id.txtCancel);
			if (TextUtils.isEmpty(negativeText)) {
				txtNegative.setVisibility(View.GONE);
			} else {
				txtNegative.setText(negativeText);
				txtNegative.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						if (negativeListener != null) {
							negativeListener.onClick(dialog, BUTTON_NEGATIVE);
						}
					}
				});
			}
			
			dialog.setContentView(contentView);
			
			//设置宽度
			Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
			LayoutParams params = dialog.getWindow().getAttributes();
			params.width = (int) (display.getWidth() * 0.75);
			dialog.getWindow().setAttributes(params);
			dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			
			return dialog;
		}
	}

}
