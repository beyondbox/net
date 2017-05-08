package com.appjumper.silkscreen.view;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appjumper.silkscreen.R;


public class SureOrCancelEditDialog extends Dialog {

    public SureOrCancelEditDialog(Context context, String message, SureButtonClick sureButtonClick) {
        super(context, R.style.loading_dialog);
        initView(context, message, sureButtonClick);
    }


    private void initView(final Context context, String message, final SureButtonClick onSureButtonClick) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.dialog_sure_edit, null);

        TextView message_tv = (TextView) v.findViewById(R.id.message_tv);
        message_tv.setText(message);
        TextView sure_tv = (TextView) v.findViewById(R.id.sure_tv);

        TextView cancel_tv = (TextView) v.findViewById(R.id.cancel_tv);
        final EditText etAmount = (EditText) v.findViewById(R.id.et_amount);
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
                if (etAmount.getText().toString().trim().length() < 1) {
                    Toast.makeText(context, "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (onSureButtonClick != null) {
                    onSureButtonClick.onSureButtonClick(etAmount.getText().toString().trim());
                }
                dismiss();
            }
        });

        setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

    }

    public interface SureButtonClick {
        void onSureButtonClick(String amount);
    }
}
