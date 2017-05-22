package com.appjumper.silkscreen.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AreaBean;
import com.appjumper.silkscreen.bean.AreaBeanResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.adapter.AddressListAdapter;
import com.appjumper.silkscreen.view.QuickIndexBar;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/10/17.
 * 地址选择（村）
 */
public class AddressSelectVillageActivity extends BaseActivity {
    @Bind(R.id.lv_data)
    ListView lv_data;
    @Bind(R.id.quickIndexBar)
    QuickIndexBar quickIndexBar;
    @Bind(R.id.tv_tip)
    TextView tv_tip;
    @Bind(R.id.et_search)
    TextView et_search;
    @Bind(R.id.tv_name)
    TextView tv_name;
    private List<AreaBean> listCounty = new ArrayList<>();
    private String code;
    private List<AreaBean> items;
    private String id;
    private String type;

    private String levelName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_select);
        ButterKnife.bind(this);
        initBack();

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        id = intent.getStringExtra("id");
        type = intent.getStringExtra("type");//type=1到市 type=2到村
        et_search.setText("村选择");

        levelName = intent.getStringExtra("name");
        tv_name.setText(levelName);

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("2")) {
                    Intent intent = new Intent();
                    intent.putExtra("id", id);
                    intent.putExtra("name", levelName);
                    setResult(Integer.parseInt(code), intent);
                    finish();
                }
            }
        });

        lv_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(type.equals("2")){
                    Intent intent = new Intent();
                    intent.putExtra("id",items.get(i).getId());
                    intent.putExtra("name",items.get(i).getShortname());
                    setResult(Integer.parseInt(code),intent);
                    finish();
                }else{
                    startForResult_Activity(AddressSelectVillageActivity.this,AddressSelectVillageActivity.class,Integer.parseInt(code),new BasicNameValuePair("type",type),new BasicNameValuePair("id",items.get(i).getId()),new BasicNameValuePair("code",code));
                }
            }
        });
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        refresh();
    }
    private void refresh() {
        new Thread(run).start();
    }

    private Runnable run = new Runnable() {
        public void run() {
            AreaBeanResponse response = null;
            Map<String, String> data = new HashMap<String, String>();
            data.put("pid", id);
            try {
                response = JsonParser.getAreaBeanResponse(HttpUtil.postMsg(HttpUtil.getData(data),Url.SUB ));
            } catch (Exception e) {
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
    private  class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final AddressSelectVillageActivity activity = (AddressSelectVillageActivity) reference.get();
            if(activity == null){
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    progress.dismiss();
                    AreaBeanResponse response = (AreaBeanResponse) msg.obj;
                    if (response.isSuccess()) {
                        items = response.getData();
                        Collections.sort(items);
                        AddressListAdapter mylistAdapter = new AddressListAdapter(activity, items);
                        lv_data.setAdapter(mylistAdapter);
                        quickIndexBar.setOnLetterChangeListener(new QuickIndexBar.OnLetterChangeListener() {
                            @Override
                            public void onLetterChange(String letter) {
                                showLetter(letter);
                                for (int i = 0; i < items.size(); i++) {
                                    if (TextUtils.equals(letter, items.get(i).getFirst().charAt(0) + "")) {
                                        lv_data.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        });
                    } else {
                        activity.showErrorToast(response.getError_desc());
                    }
                    break;

                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
                default:
                    activity.showErrorToast();
                    break;
            }
        }
    };
    Handler mHandler = new Handler();

    /**
     * 显示字母提示
     *
     * @param letter
     */
    public void showLetter(String letter) {
        tv_tip.setVisibility(View.VISIBLE);
        tv_tip.setText(letter);
        //取消掉刚刚所有的演示操作
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_tip.setVisibility(View.GONE);
            }
        }, 500);
    }

}
