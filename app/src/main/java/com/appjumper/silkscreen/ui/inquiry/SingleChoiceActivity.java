package com.appjumper.silkscreen.ui.inquiry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.inquiry.adapter.SingleRecyclerAdapter;
import com.appjumper.silkscreen.view.MyRecyclerView;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 编辑简历选择
 */
public class SingleChoiceActivity extends BaseActivity {
    @Bind(R.id.recycler_view)
    MyRecyclerView myRecyclerView;

    private SingleRecyclerAdapter adapter;
    private String[] choiceList;
    private String choiceValue;
    private String fieldname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_choice);
        initBack();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        initTitle(title + "（单选）");
        fieldname = intent.getStringExtra("fieldname");
        choiceList = intent.getStringExtra("list").split(",");
        choiceValue = choiceList[0];
        initRecyclerView();
        initRightButton("完成", new RightButtonListener() {
            @Override
            public void click() {
                setComplete();
            }
        });
    }


    /**
     * 选择返回值
     */
    private void setComplete() {
        Intent intent = new Intent();
        intent.putExtra("choice", choiceValue);
        intent.putExtra("fieldname", fieldname);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SingleRecyclerAdapter(this, choiceList);
        myRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new SingleRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

                choiceValue = choiceList[position];
            }
        });
    }
}
