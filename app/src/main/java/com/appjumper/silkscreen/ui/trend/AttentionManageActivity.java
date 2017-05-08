package com.appjumper.silkscreen.ui.trend;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.MaterProduct;
import com.appjumper.silkscreen.bean.MaterProductResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.trend.adapter.AttentionMyAdapter;
import com.appjumper.silkscreen.ui.trend.adapter.AttentionRecommendAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.db.DBManager;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关注管理
 * Created by Botx on 2017/4/25.
 */

public class AttentionManageActivity extends BaseActivity {

    @Bind(R.id.recyclerMy)
    RecyclerView recyclerMy;
    @Bind(R.id.recyclerRecommend)
    RecyclerView recyclerRecommend;

    private List<MaterProduct> myList;
    private List<MaterProduct> recommendList;

    private AttentionMyAdapter myAdapter;
    private AttentionRecommendAdapter recommendAdapter;

    private DBManager dbHelper;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_manage);
        ButterKnife.bind(this);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        initTitle("关注管理");
        initRecyclerMy();
        initRecyclerRecommend();

        new Thread(new AllproductRun()).start();
    }


    /**
     * 设置我的关注控件
     */
    private void initRecyclerMy() {
        dbHelper = new DBManager(context);
        myList = dbHelper.query();

        myAdapter = new AttentionMyAdapter(R.layout.item_my2, myList);
        myAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        myAdapter.isFirstOnly(false);
        ViewCompat.setLayoutDirection(recyclerMy, ViewCompat.LAYOUT_DIRECTION_LTR);
        recyclerMy.addItemDecoration(new SpacingItemDecoration(24, 24));
        recyclerMy.setLayoutManager(getLayoutManager());
        recyclerMy.setAdapter(myAdapter);
        touchHelper.attachToRecyclerView(recyclerMy);

        myAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.img_edit:
                        recommendList.add(myList.get(position));
                        myList.remove(position);

                        myAdapter.notifyItemRemoved(position);
                        myAdapter.notifyItemRangeChanged(position, myList.size() - position);

                        recommendAdapter.notifyItemInserted(recommendList.size() - 1);
                        recommendAdapter.notifyItemRangeChanged(recommendList.size() - 1, 1);
                        break;
                    default:
                        break;
                }
            }
        });

        myAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == 0) {
                    return false;
                }

                vibrator.vibrate(70);
                return true;
            }
        });
    }


    /**
     * 设置关注推荐控件
     */
    private void initRecyclerRecommend() {
        recommendList = new ArrayList<>();

        recommendAdapter = new AttentionRecommendAdapter(R.layout.item_my2, recommendList);
        recommendAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        recommendAdapter.isFirstOnly(false);
        ViewCompat.setLayoutDirection(recyclerRecommend, ViewCompat.LAYOUT_DIRECTION_LTR);
        recyclerRecommend.addItemDecoration(new SpacingItemDecoration(24, 16));
        recyclerRecommend.setLayoutManager(getLayoutManager());
        recyclerRecommend.setAdapter(recommendAdapter);

        recommendAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                myList.add(recommendList.get(position));
                recommendList.remove(position);

                recommendAdapter.notifyItemRemoved(position);
                recommendAdapter.notifyItemRangeChanged(position, recommendList.size() - position);

                myAdapter.notifyItemInserted(myList.size() - 1);
                myAdapter.notifyItemRangeChanged(myList.size() - 1, 1);
            }
        });
    }


    /**
     * 流式布局管理器
     * @return
     */
    private ChipsLayoutManager getLayoutManager() {
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(context)
                //whether RecyclerView can scroll. TRUE by default
                .setScrollingEnabled(false)
                //set maximum views count in a particular row
                //.setMaxViewsInRow(3)
                //set gravity resolver where you can determine gravity for item in position.
                //This method have priority over previous one
                .setGravityResolver(new IChildGravityResolver() {
                    @Override
                    public int getItemGravity(int position) {
                        return Gravity.CENTER;
                    }
                })
                .build();

        return chipsLayoutManager;
    }


    /**
     * 我的关注的触摸事件
     */
    private ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags; //拖拽

            if (viewHolder.getAdapterPosition() == 0) {
                dragFlags = 0;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }

            int swipeFlags = 0; //侧滑删除
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            if (toPosition == 0) {
                return false;
            }

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(myList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(myList, i, i - 1);
                }
            }

            myAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    });



    //所有产品
    private class AllproductRun implements Runnable {
        private MaterProductResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                response = JsonParser.getMaterProductResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.PRICEALLPROFUVT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };


    private MyHandler handler = new MyHandler();
    private  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    MaterProductResponse detailsResponse = (MaterProductResponse) msg.obj;
                    if(detailsResponse.isSuccess()){
                        recommendList.addAll(detailsResponse.getData());
                        recommendList.removeAll(myList);
                        recommendAdapter.notifyDataSetChanged();
                    }else{
                        showErrorToast(detailsResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    showErrorToast("数据返回错误");
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    };


    @OnClick({R.id.back, R.id.txtConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.txtConfirm:
                dbHelper.deleteProduct();
                dbHelper.addProduct(myList);
                sendBroadcast(new Intent(Const.ACTION_ATTENTION_MATER_REFRESH));
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.closeDB();
    }
}
