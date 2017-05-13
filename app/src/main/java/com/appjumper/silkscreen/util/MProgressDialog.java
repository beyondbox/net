package com.appjumper.silkscreen.util;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.appjumper.silkscreen.R;


public class MProgressDialog extends AlertDialog {

	private TextView mMessageView;

	private boolean mCancelAble = true;

	private String mMessage;



	public MProgressDialog(Context context, boolean cancelAble) {
		super(context, R.style.MDialog);

		mCancelAble = cancelAble;

	}

	public MProgressDialog(Context context, boolean cancelAble, String message) {
		super(context, R.style.MDialog);

		mCancelAble = cancelAble;
		mMessage = message;
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_progress_dialog);

		mMessageView = (TextView) findViewById(R.id.progress_message);
		if (!TextUtils.isEmpty(mMessage))
			mMessageView.setText(mMessage);

		setCancelable(mCancelAble);
		setCanceledOnTouchOutside(false);

	}

	public void setMessage(String message) {
			mMessageView.setText(message);
	}
}
