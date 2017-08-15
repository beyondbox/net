package com.appjumper.silkscreen.view;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;

import static android.R.string.cancel;


public class SureOrCancelDialog extends Dialog {

    private String titleStr;
    private String cancelStr;
    private String sureStr;

    private TextView message_tv;

    public SureOrCancelDialog(Context context, String message, SureButtonClick sureButtonClick) {
        super(context, R.style.loading_dialog);
        initView(context, message, sureButtonClick);
    }

    public SureOrCancelDialog(Context context, String title, String message, String sureStr, String cancelStr, SureButtonClick sureButtonClick) {
        super(context, R.style.loading_dialog);
        this.titleStr = title;
        this.sureStr = sureStr;
        this.cancelStr = cancelStr;
        initView(context, message, sureButtonClick);
    }

    private void initView(Context context, String message, final SureButtonClick onSureButtonClick) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_sure, null);

        message_tv = (TextView) v.findViewById(R.id.message_tv);
        message_tv.setText(message);
        TextView sure_tv = (TextView) v.findViewById(R.id.sure_tv);

        TextView cancel_tv = (TextView) v.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });

        sure_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();
                if (onSureButtonClick != null) {
                    onSureButtonClick.onSureButtonClick();
                }
            }
        });

        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        Paint paint = message_tv.getPaint();
        if (TextUtils.isEmpty(titleStr)) {
            txtTitle.setVisibility(View.GONE);
            paint.setFakeBoldText(true);
        } else {
            txtTitle.setText(titleStr);
            txtTitle.setVisibility(View.VISIBLE);
            paint.setFakeBoldText(false);
        }

        if (!TextUtils.isEmpty(sureStr))
            sure_tv.setText(sureStr);
        if (!TextUtils.isEmpty(cancelStr))
            cancel_tv.setText(cancelStr);

        setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

    }


    private void setMessage(String message) {
        if (message_tv != null)
            message_tv.setText(message);
    }


    public interface SureButtonClick {
        void onSureButtonClick();
    }
}
