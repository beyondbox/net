package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.common.adapter.FragAdapter;
import com.appjumper.silkscreen.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 动态
 * Created by Botx on 2017/10/17.
 */

public class DynamicFragment extends BaseFragment {

    @Bind(R.id.back)
    ImageView imgViBack;
    @Bind(R.id.title)
    TextView txtTitle;
    @Bind(R.id.right)
    TextView txtRight;
    @Bind(R.id.frameContent)
    FrameLayout frameContent;
    @Bind(R.id.rdoGroup)
    RadioGroup rdoGroup;

    private List<Fragment> fragList;
    private FragAdapter fragAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        ButterKnife.bind(this, view);

        txtTitle.setText("动态");
        imgViBack.setVisibility(View.INVISIBLE);
        txtRight.setText("发布");
        return view;
    }


    @Override
    protected void initData() {
        fragList = new ArrayList<>();
        fragList.add(new AskBuyFragment());
        fragList.add(new AttentionFragment());

        fragAdapter = new FragAdapter(context.getSupportFragmentManager(), fragList);

        rdoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rdoBtnAskBuy:
                        switchFragment(0);
                        txtRight.setText("发布");
                        break;
                    case R.id.rdoBtnAttention:
                        switchFragment(1);
                        txtRight.setText("管理");
                        break;
                    default:
                        break;
                }
            }
        });

        rdoGroup.check(R.id.rdoBtnAskBuy);
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


    @OnClick(R.id.right)
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.right:
                if (checkLogined()) {
                    if (txtRight.getText().toString().equals("发布")) {
                        intent = new Intent(context, ProductSelectActivity.class);
                        intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_STOCK);
                        intent.putExtra(Const.KEY_MOTION, ProductSelectActivity.MOTION_RELEASE_ASKBUY);
                    } else {
                        intent = new Intent(context, DynamicManageActivity.class);
                    }
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

}
