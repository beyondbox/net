package com.appjumper.silkscreen.ui.inquiry;

import android.os.Bundle;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * 询价完成
 * Created by Botx on 2017/4/12.
 */

public class InquiryCompleteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(context);
        setContentView(R.layout.activity_inquiry_complete);

        initTitle("询价");
        initRightButton("完成", new RightButtonListener() {
            @Override
            public void click() {
                finish();
            }
        });
    }


}
