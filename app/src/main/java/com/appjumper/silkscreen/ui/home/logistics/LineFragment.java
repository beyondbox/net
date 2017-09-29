package com.appjumper.silkscreen.ui.home.logistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * 物流-货站
 * Created by Botx on 2017/9/29.
 */

public class LineFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {

    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
