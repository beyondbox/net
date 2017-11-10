package com.appjumper.silkscreen.ui.common.adapter;

import android.os.CountDownTimer;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 含有倒计时的适配器的ViewHolder
 * Created by Botx on 2017/11/9.
 */

public class CountDownViewHolder extends BaseViewHolder {

    public CountDownTimer countDownTimer;

    public CountDownViewHolder(View view) {
        super(view);
    }

}
