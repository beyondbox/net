package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.my.adapter.MultipleChoiceListViewAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-11-22.
 */
public class MultipleChoiceActivity extends BaseActivity {
    @Bind(R.id.list_view)
    ListView listView;
    private String[] list ;
    private ArrayList<String> resultList = new ArrayList<>();
    private MultipleChoiceListViewAdapter mAdapter;
    private String title;
    private String fieldname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice);
        initBack();
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        fieldname = intent.getStringExtra("fieldname");
        initTitle(title + "(多选)");
        ButterKnife.bind(this);
         list = intent.getStringExtra("list").split(",");
        mAdapter = new MultipleChoiceListViewAdapter(this, listView, list);
        listView.setAdapter(mAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        initRightButton("完成", new RightButtonListener() {
            @Override
            public void click() {
                long[] choices = getListCheckedItemIds(listView);
                if (choices.length < 1) {
                    showErrorToast("请至少选择一种" + title + "！");
                    return;
                }
                for (int i = 0; i < choices.length; i++) {
                    resultList.add(list[(int) choices[i]]);
                }
                Intent intent1 = new Intent();
                intent1.putStringArrayListExtra("list", resultList);
                intent1.putExtra("fieldname", fieldname);
                setResult(10, intent1);
                finish();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mAdapter.notifyDataSetChanged();
            }
        });

    }


    public long[] getListCheckedItemIds(ListView listView) {

        long[] ids = new long[listView.getCount()];//getCount()即获取到ListView所包含的item总个数
        //定义用户选中Item的总个数
        int checkedTotal = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            //如果这个Item是被选中的
            if (listView.isItemChecked(i)) {
                ids[checkedTotal] = i;
                checkedTotal++;
            }
        }

        if (checkedTotal < listView.getCount()) {
            //定义选中的Item的ID数组
            long[] selectedIds = new long[checkedTotal];
            //数组复制 ids
            System.arraycopy(ids, 0, selectedIds, 0, checkedTotal);
            return selectedIds;
        } else {
            //用户将所有的Item都选了
            return ids;
        }
    }
}
