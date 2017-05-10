package com.appjumper.silkscreen.ui.spec;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 刺绳发布
 * Created by Botx on 2017/5/9.
 */

public class ReleaseCiShengActivity extends BasePhotoGridActivity {

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

    @Bind(R.id.etDanKunStart)
    EditText etDanKunStart;
    @Bind(R.id.etDanKunEnd)
    EditText etDanKunEnd;
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
        setContentView(R.layout.activity_release_cisheng);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initBack();
        initTitle("刺绳");
        initView();
        initData();
    }


    private void initData() {
        siJingAdapter = new SpecSelectAdapter(context, Arrays.asList(siJingArr));
        gridSiJing.setAdapter(siJingAdapter);

        ciJuAdapter = new SpecSelectAdapter(context, Arrays.asList(ciJuArr));
        gridCiJu.setAdapter(ciJuAdapter);

        ningGuAdapter = new SpecSelectAdapter(context, Arrays.asList(ningGuArr));
        gridNingGu.setAdapter(ningGuAdapter);

        siCaiZhiAdapter = new SpecSelectAdapter(context, Arrays.asList(siCaiZhiArr));
        gridSiCaiZhi.setAdapter(siCaiZhiAdapter);

        waiBiaoAdapter = new SpecSelectAdapter(context, Arrays.asList(waiBiaoArr));
        gridWaiBiao.setAdapter(waiBiaoAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setDefault(gridSiJing, 0);
                setDefault(gridCiJu, 2);
                setDefault(gridNingGu, 0);
                setDefault(gridSiCaiZhi, 0);
                setDefault(gridWaiBiao, 0);
            }
        }, 100);
    }


    /**
     * 设置默认项
     */
    private void setDefault(GridView gridView, int position) {
        ((CheckBox)((LinearLayout)gridView.getChildAt(position)).getChildAt(0)).setChecked(true);
    }

}
