package com.appjumper.silkscreen.ui.my;

import android.os.Bundle;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/18.
 * 关于我们
 */
public class AboutUsActivity extends BaseActivity{
    @Bind(R.id.tv_version)
    TextView tv_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        initBack();
        ButterKnife.bind(this);
        tv_version.setText("v"+getVersionName());

    }
}
