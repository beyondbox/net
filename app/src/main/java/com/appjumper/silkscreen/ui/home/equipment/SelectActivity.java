package com.appjumper.silkscreen.ui.home.equipment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.EquipmentCategory;
import com.appjumper.silkscreen.bean.EquipmentCategoryResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.SelectRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SubListViewAdapter;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.MyRecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-11-22.
 * 设备分类
 */
public class SelectActivity extends BaseActivity {
    @Bind(R.id.recycler_view)
    MyRecyclerView recyclerView;
    @Bind(R.id.list_view)
    ListView listView;

    private SelectRecyclerAdapter adapter;
    private List<EquipmentCategory> list;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        ActivityTaskManager.getInstance().putActivity(SelectActivity.this);
        initBack();
        ButterKnife.bind(this);
        initTitle(getIntent().getStringExtra("title"));
        type = getIntent().getStringExtra("type");
        initProgressDialog();
        progress.show();
        new Thread(run).start();

    }

    private Runnable run = new Runnable() {
        private EquipmentCategoryResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                String url = "";
                if (type.equals("1")) {
                    url = Url.EQUIPMENT_CATEGORY;
                } else if (type.equals("2")) {
                    url = Url.JOB_TYPE;
                }
                response = JsonParser.getEquipmentCategoryResponse(HttpUtil.getMsg(
                        url));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_DATA_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            SelectActivity activity = (SelectActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT://设备名称列表
                    progress.dismiss();
                    EquipmentCategoryResponse response = (EquipmentCategoryResponse) msg.obj;
                    if (response.isSuccess()) {
                        if (response.getData() != null && response.getData().size() > 0) {
                            list = response.getData();
                            initRecyclerView(list);
                            initListView(list.get(0).getSublist());
                        }
                    } else {
                        showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    if (progress != null) {
                        progress.dismiss();
                    }
                    activity.showErrorToast();
                    break;
            }
        }
    }

    /**
     * 初始化RecyclerView
     *
     * @param data
     */
    private void initRecyclerView(final List<EquipmentCategory> data) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SelectRecyclerAdapter(this, data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new SelectRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                initListView(data.get(position).getSublist());
            }
        });
    }

    private void initListView(final List<EquipmentCategory> data) {
        final SubListViewAdapter adapter = new SubListViewAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setCurrentSelectPosition(position);
                adapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra("name", data.get(position).getName());
                intent.putExtra("id", data.get(position).getId());
                setResult(13, intent);
                finish();
            }
        });
    }

}
