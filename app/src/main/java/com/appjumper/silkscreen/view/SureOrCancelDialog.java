package com.appjumper.silkscreen.view;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;


public class SureOrCancelDialog extends Dialog {

    public SureOrCancelDialog(Context context, String message, SureButtonClick sureButtonClick) {
        super(context, R.style.loading_dialog);
        initView(context, message, sureButtonClick);
    }

    private void initView(Context context, String message, final SureButtonClick onSureButtonClick) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_sure, null);

        TextView message_tv = (TextView) v.findViewById(R.id.message_tv);
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

        setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

    }

    public interface SureButtonClick {
        void onSureButtonClick();
    }
}
