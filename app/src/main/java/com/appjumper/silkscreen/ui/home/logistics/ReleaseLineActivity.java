package com.appjumper.silkscreen.ui.home.logistics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.ui.home.adapter.PassbyAdapter;
import com.appjumper.silkscreen.ui.my.enterprise.AddServiceActivity;
import com.appjumper.silkscreen.ui.my.enterprise.AddServiceCompleteActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.MyRecyclerView;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-18.
 * 物流路线发布
 */
public class ReleaseLineActivity extends BaseActivity {
    private String start_id = "214";
    private String end_id = "";

    @Bind(R.id.tv_start)//货运起点
            TextView tv_start;
    @Bind(R.id.tv_end)//货运终点
            TextView tv_end;

    @Bind(R.id.et_remark)//备注信息
            EditText et_remark;
    @Bind(R.id.gv_passby)//途经地
            MyRecyclerView gv_passby;
    @Bind(R.id.tv_hint)//提示
            TextView tvHint;

    private ArrayList<String> addressList = new ArrayList<>();
    private PassbyAdapter adapter;
    private String ids = "";
    private String names = "";

    private String type = "1";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_release_line);
        initBack();
        ButterKnife.bind(this);
        initTitle("创建线路");
        initRecycler(addressList);
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if (TextUtils.isEmpty(start_id)) {
                    showErrorToast("请选择始发地");
                    return;
                }
                if (TextUtils.isEmpty(end_id)) {
                    showErrorToast("请选择目的地");
                    return;
                }
                if (ids.equals("")) {
                    showErrorToast("请添加途经地");
                    return;
                }

                hideKeyboard();
                initProgressDialog();
                progress.show();
                progress.setMessage("正在发布...");
                new Thread(submitRun).start();
            }
        });
    }

    private void initRecycler(final ArrayList<String> addresses) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gv_passby.setLayoutManager(linearLayoutManager);
        adapter = new PassbyAdapter(this, addresses);
        gv_passby.setAdapter(adapter);
        adapter.setOnItemClickLitener(new PassbyAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                addresses.remove(addresses.get(position));
                adapter.notifyDataSetChanged();
                if (addresses.size() > 0) {
                    tvHint.setVisibility(View.GONE);
                } else {
                    tvHint.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    //发布
    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("from", start_id);
                data.put("to", end_id);
                data.put("from_name", tv_start.getText().toString());
                data.put("to_name", tv_end.getText().toString());
                data.put("type", type);
                data.put("uid", getUserID());
                data.put("passby", ids);//途经地 id
                data.put("passby_name", names);//途经地名称
                data.put("remark", et_remark.getText().toString().trim());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.LINEADD));
            } catch (Exception e) {
                progress.dismiss();
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


    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            ReleaseLineActivity activity = (ReleaseLineActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://发布
                    progress.dismiss();
                    BaseResponse baseResponse = (BaseResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        //showErrorToast("发布成功");
                        start_Activity(context, AddServiceCompleteActivity.class, new BasicNameValuePair(Const.KEY_MESSAGE, "线路添加完成"), new BasicNameValuePair(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_LOGISTICS + ""));
                        ActivityTaskManager.getInstance().getActivity(AddServiceActivity.class).finish();
                        CommonApi.addLiveness(getUserID(), 19);
                        finish();
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }

                    break;
                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
            }
        }
    }


    @OnClick({R.id.tv_start, R.id.tv_end, R.id.iv_add})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start://起点
                startForResult_Activity(this, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_end://终点
                startForResult_Activity(this, AddressSelectActivity.class, 2, new BasicNameValuePair("code", "2"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.iv_add://货运途经
                startForResult_Activity(this, AddressSelectActivity.class, 4, new BasicNameValuePair("code", "3"), new BasicNameValuePair("type", "1"));
                break;
            default:
                break;
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1://出发地
                start_id = data.getStringExtra("id");
                String start_name = data.getStringExtra("name");
                tv_start.setText(start_name);
                tv_start.setCompoundDrawables(null, null, null, null);
                break;
            case 2://目的地
                end_id = data.getStringExtra("id");
                String end_name = data.getStringExtra("name");
                tv_end.setText(end_name);
                tv_end.setCompoundDrawables(null, null, null, null);
                break;
            case 4:
                addressList.add(data.getStringExtra("name"));
                adapter.notifyDataSetChanged();
                if (addressList.size() <= 1) {
                    names += data.getStringExtra("name");
                    ids += data.getStringExtra("id");
                } else {
                    names += "," + data.getStringExtra("name");
                    ids += "," + data.getStringExtra("id");
                }
                if (addressList.size() > 0) {
                    tvHint.setVisibility(View.GONE);

                } else {
                    tvHint.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }
}
