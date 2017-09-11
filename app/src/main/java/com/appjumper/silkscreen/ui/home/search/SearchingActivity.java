package com.appjumper.silkscreen.ui.home.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.KeyWorks;
import com.appjumper.silkscreen.bean.KeyWorksResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.adapter.HistoryListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.HotGridViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.MyGridView;
import com.appjumper.silkscreen.view.MyListView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 正在搜索
 */
public class SearchingActivity extends BaseActivity {
    @Bind(R.id.grid_view)
    MyGridView gridView;
    @Bind(R.id.list_view)
    MyListView listView;
    @Bind(R.id.et_search)
    EditText etSearch;

    private List<String> list;
    private String key;
    private ArrayList<String> tempList = new ArrayList<>();
    private HistoryListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        ButterKnife.bind(context);

        list = new ArrayList<>();
        initHistory();
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 3) {
                /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (etSearch.getText().toString().trim().length() < 1 || etSearch.getText().toString().trim().equals("")) {
                        showErrorToast("搜索内容不能为空");
                    } else {
                        if (inputMethodManager.isActive()) {
                            inputMethodManager.hideSoftInputFromWindow(SearchingActivity.this.getCurrentFocus().getWindowToken(), 0);
                        }
                        key = etSearch.getText().toString().trim();
                        getMyApplication().getMyUserManager().saveHistory(key);
//                        initHistory();
                        start_Activity(SearchingActivity.this, SearchResultsActivity.class, new BasicNameValuePair("key", key));
                        finish();
                        return true;
                    }
                }
                return false;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new KeyworksRun()).start();
            }
        }, 80);
    }


    //热门搜索
    private class KeyworksRun implements Runnable {
        private KeyWorksResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                response = JsonParser.getKeyWorksResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.HOMEKEYWORKS));
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
    }

    private MyHandler handler = new MyHandler();
    private  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://热门搜索
                    KeyWorksResponse userResponse = (KeyWorksResponse) msg.obj;
                    if(userResponse.isSuccess()){
                        List<KeyWorks> keyworks = userResponse.getData();
                        if(keyworks!=null){
                            initGridView(keyworks);
                        }
                    }else{
                        showErrorToast(userResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    showErrorToast("数据返回有误");
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    }

    private void initGridView(final List<KeyWorks> keyworks) {
        HotGridViewAdapter adapter = new HotGridViewAdapter(SearchingActivity.this, keyworks);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                key = keyworks.get(position).getName();
                getMyApplication().getMyUserManager().saveHistory(key);
                etSearch.setText(key);
                hideKeyboard();
                start_Activity(SearchingActivity.this, SearchResultsActivity.class, new BasicNameValuePair("key", key));
                finish();
            }
        });
    }

    private void initHistory() {
        Set<String> historyData = getMyApplication().getMyUserManager().getHistory();
        if (historyData != null && historyData.size() > 0) {
            tempList.clear();
            for (String str : historyData) {
                tempList.add(str);
            }
            if (tempList != null) {
                if (mAdapter == null) {
                    mAdapter = new HistoryListViewAdapter(SearchingActivity.this, tempList);
                    listView.setAdapter(mAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            key = tempList.get(position);
                            etSearch.setText(key);
                            hideKeyboard();
                            start_Activity(SearchingActivity.this, SearchResultsActivity.class, new BasicNameValuePair("key", key));
                            finish();
                        }
                    });
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_clean})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                hideKeyboard();
                finish();
                break;
            case R.id.tv_clean://清空历史记录
                if (mAdapter == null) {
                    return;
                }
                getMyApplication().getMyUserManager().cleanHistory();
                tempList.clear();
                mAdapter.notifyDataSetChanged();
                etSearch.setText("");
                break;
            default:
                break;
        }
    }

}
