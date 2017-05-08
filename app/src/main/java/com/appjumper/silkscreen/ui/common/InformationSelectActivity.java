package com.appjumper.silkscreen.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.common.adapter.InformationListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/21.
 * 所有固定信息选择
 */
public class InformationSelectActivity extends BaseActivity {
    @Bind(R.id.listview)
    ListView listview;
    private String[] val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_select);
        initBack();
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        initTitle(title);
         val = intent.getExtras().getStringArray("val");
        ButterKnife.bind(this);
        listview.setAdapter(new InformationListAdapter(this,val));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("val",i+"");
                setResult(3,intent);
                finish();
            }
        });
    }
}
