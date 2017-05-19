package com.appjumper.silkscreen.ui.inquiry;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 询价完成
 * Created by Botx on 2017/4/12.
 */

public class InquiryCompleteActivity extends BaseActivity {

    @Bind(R.id.back)
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_complete);
        ButterKnife.bind(context);

        back.setVisibility(View.INVISIBLE);
        initTitle("询价");
        initRightButton("完成", new RightButtonListener() {
            @Override
            public void click() {
                finish();
            }
        });
    }


}
