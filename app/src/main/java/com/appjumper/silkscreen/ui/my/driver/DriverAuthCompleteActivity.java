package com.appjumper.silkscreen.ui.my.driver;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.AppTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 司机认证提交成功
 * Created by Botx on 2017/10/26.
 */

public class DriverAuthCompleteActivity extends BaseActivity {

    @Bind(R.id.txtMessage)
    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_auth_complete);
        ButterKnife.bind(context);
        initTitle("提交成功");
        initBack();

        String timeStr = AppTool.dateFormat(System.currentTimeMillis(), "yyyy年MM月dd日 HH:mm");
        txtMessage.setText("提交时间 : " + timeStr);
    }

    @OnClick({R.id.txtCall})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCall:
                AppTool.dial(context, "03187878000");
                break;
        }
    }
}
