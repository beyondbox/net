package com.appjumper.silkscreen.ui.home.logistics;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.SureOrCancelDialog;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 物流
 * Created by Botx on 2017/9/29.
 */

public class LogisticsListActivity extends BaseActivity {

    @Bind(R.id.frameContent)
    FrameLayout frameContent;
    @Bind(R.id.rdoGroup)
    RadioGroup rdoGroup;

    public static LogisticsListActivity instance = null;
    private SureOrCancelDialog comCreateDialog;

    private List<Fragment> fragList;
    private FragAdapter fragAdapter;
    private String type = "1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_list);
        ButterKnife.bind(context);
        instance = this;

        initData();
        initBack();
        initDialog();

        int comingType = getIntent().getIntExtra(Const.KEY_TYPE, 1);
        if (comingType == 2)
            rdoGroup.check(R.id.rb1);
        else
            rdoGroup.check(R.id.rb0);
    }


    private void initData() {
        fragList = new ArrayList<>();
        fragList.add(new LineFragment());
        fragList.add(new FreightFragment());

        fragAdapter = new FragAdapter(getSupportFragmentManager());

        rdoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb0:
                        type = "1";
                        switchFragment(0);
                        break;
                    case R.id.rb1:
                        type = "2";
                        switchFragment(1);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    /**
     * 初始化对话框
     */
    private void initDialog() {
        comCreateDialog = new SureOrCancelDialog(context, "提示", "您尚未完善企业信息，暂时不能在该板块发布信息，请完善企业信息后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    }
                });
    }


    /**
     * 切换Fragment
     * @param position
     */
    private void switchFragment(int position) {
        Fragment fragment = (Fragment) fragAdapter.instantiateItem(frameContent, position);
        fragAdapter.setPrimaryItem(frameContent, position, fragment);
        fragAdapter.finishUpdate(frameContent);
    }


    /**
     * Fragment管理适配器
     */
    private class FragAdapter extends FragmentPagerAdapter {

        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }
    }


    @OnClick({R.id.right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.right: //发布
                if (!checkLogined())
                    return;

                switch (type) {
                    case "1":
                        if (getUser().getEnterprise() == null) {
                            comCreateDialog.show();
                            return;
                        }
                        CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS);
                        break;
                    case "2":
                        if (!MyApplication.appContext.checkMobile(context)) return;
                        if (!MyApplication.appContext.checkCertifyPer(context)) return;
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
                            showErrorToast("您尚未开启本应用的定位权限");
                            return;
                        }
                        start_Activity(context, ReleaseFreightActivity.class);
                        break;
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
