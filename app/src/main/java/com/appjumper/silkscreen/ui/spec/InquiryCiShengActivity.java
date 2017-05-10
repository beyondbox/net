package com.appjumper.silkscreen.ui.spec;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 刺绳询价
 * Created by Botx on 2017/5/9.
 */

public class InquiryCiShengActivity extends BasePhotoGridActivity {

    @Bind(R.id.gridSiJing)
    GridView gridSiJing;
    @Bind(R.id.gridCiJu)
    GridView gridCiJu;
    @Bind(R.id.gridNingGu)
    GridView gridNingGu;
    @Bind(R.id.gridSiCaiZhi)
    GridView gridSiCaiZhi;
    @Bind(R.id.gridWaiBiao)
    GridView gridWaiBiao;

    @Bind(R.id.etDanKun)
    EditText etDanKun;
    @Bind(R.id.etNumber)
    EditText etNumber;

    private String[] siJingArr = {"1.2*1.2", "1.2*1.4", "1.4*1.4", "1.4*1.6", "1.6*1.6", "1.7*1.7", "2.2*2.2", "2.2*2.5", "2.2*2.8"};
    private String [] ciJuArr = {"76mm", "102mm", "128mm", "150mm"};
    private String [] ningGuArr = {"双", "正反", "单"};
    private String [] siCaiZhiArr = {"铁丝", "钢丝"};
    private String [] waiBiaoArr = {"冷镀", "热镀", "包塑"};

    private SpecSelectAdapter siJingAdapter;
    private SpecSelectAdapter ciJuAdapter;
    private SpecSelectAdapter ningGuAdapter;
    private SpecSelectAdapter siCaiZhiAdapter;
    private SpecSelectAdapter waiBiaoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_cisheng);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initBack();
        initTitle("刺绳");
        initView();
        initData();
    }


    private void initData() {
        siJingAdapter = new SpecSelectAdapter(context, Arrays.asList(siJingArr));
        siJingAdapter.setChoiceMode(SpecSelectAdapter.CHOICE_MODE_SINGLE);
        gridSiJing.setAdapter(siJingAdapter);
        gridSiJing.setOnItemClickListener(new MyItemClickImpl(siJingAdapter));

        ciJuAdapter = new SpecSelectAdapter(context, Arrays.asList(ciJuArr));
        ciJuAdapter.setChoiceMode(SpecSelectAdapter.CHOICE_MODE_SINGLE);
        gridCiJu.setAdapter(ciJuAdapter);
        gridCiJu.setOnItemClickListener(new MyItemClickImpl(ciJuAdapter));

        ningGuAdapter = new SpecSelectAdapter(context, Arrays.asList(ningGuArr));
        ningGuAdapter.setChoiceMode(SpecSelectAdapter.CHOICE_MODE_SINGLE);
        gridNingGu.setAdapter(ningGuAdapter);
        gridNingGu.setOnItemClickListener(new MyItemClickImpl(ningGuAdapter));

        siCaiZhiAdapter = new SpecSelectAdapter(context, Arrays.asList(siCaiZhiArr));
        siCaiZhiAdapter.setChoiceMode(SpecSelectAdapter.CHOICE_MODE_SINGLE);
        gridSiCaiZhi.setAdapter(siCaiZhiAdapter);
        gridSiCaiZhi.setOnItemClickListener(new MyItemClickImpl(siCaiZhiAdapter));

        waiBiaoAdapter = new SpecSelectAdapter(context, Arrays.asList(waiBiaoArr));
        waiBiaoAdapter.setChoiceMode(SpecSelectAdapter.CHOICE_MODE_SINGLE);
        gridWaiBiao.setAdapter(waiBiaoAdapter);
        gridWaiBiao.setOnItemClickListener(new MyItemClickImpl(waiBiaoAdapter));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setDefault(siJingAdapter, 0);
                setDefault(ciJuAdapter, 2);
                setDefault(ningGuAdapter, 0);
                setDefault(siCaiZhiAdapter, 0);
                setDefault(waiBiaoAdapter, 0);
            }
        }, 100);
    }


    /**
     * 设置默认项
     */
    private void setDefault(SpecSelectAdapter adapter, int position) {
        adapter.changeSelected(position);
    }

    /**
     * GridView点击事件
     */
    private class MyItemClickImpl implements AdapterView.OnItemClickListener {

        private SpecSelectAdapter adapter;

        public MyItemClickImpl(SpecSelectAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.changeSelected(position);
        }
    }

}
