package com.appjumper.silkscreen.ui.my.askbuy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.Address;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.adapter.CommonLocationAdapter;
import com.appjumper.silkscreen.ui.my.adapter.CommonStationAdapter;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 添加地址
 * Created by Botx on 2017/12/12.
 */

public class AddAddressActivity extends BaseActivity implements MyBaseAdapter.OnWhichClickListener{

    @Bind(R.id.edtTxtConsigner)
    EditText edtTxtConsigner;
    @Bind(R.id.edtTxtMobile)
    EditText edtTxtMobile;
    @Bind(R.id.chk0)
    CheckBox chk0;
    @Bind(R.id.chk1)
    CheckBox chk1;
    @Bind(R.id.llStation)
    LinearLayout llStation;
    @Bind(R.id.llLocation)
    LinearLayout llLocation;
    @Bind(R.id.edtTxtLocation)
    EditText edtTxtLocation;
    @Bind(R.id.txtStation)
    TextView txtStation;
    @Bind(R.id.lvStation)
    ListView lvStation;
    @Bind(R.id.lvLocation)
    ListView lvLocation;
    @Bind(R.id.llCommonStation)
    LinearLayout llCommonStation;
    @Bind(R.id.llCommonLocation)
    LinearLayout llCommonLocation;


    private String type = "0";  //0货站   1地点
    private LineList selectedStation;

    private List<LineList> stationList; //常用货站
    private CommonStationAdapter stationAdapter;

    private List<Address> locationList; //常用地点
    private CommonLocationAdapter locationAdapter;

    private int threadCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(context);
        initBack();
        initTitle("添加地址");
        initProgressDialog(false, null);
        initListView();

        Address address = (Address) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        if (address != null) {
            edtTxtConsigner.setText(address.getName());
            edtTxtMobile.setText(address.getMobile());
        }

        progress.show();
        getCommonStation();
        getCommonLocation();
    }


    private void initListView() {
        stationList = new ArrayList<>();
        stationAdapter = new CommonStationAdapter(context, stationList);
        stationAdapter.setOnWhichClickListener(this);
        lvStation.setDividerHeight(0);
        lvStation.setAdapter(stationAdapter);

        lvStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStation = stationList.get(i);
                txtStation.setText(selectedStation.getOfficial_name() + " " + selectedStation.getOfficial_address());
            }
        });


        locationList = new ArrayList<>();
        locationAdapter = new CommonLocationAdapter(context, locationList);
        locationAdapter.setOnWhichClickListener(this);
        lvLocation.setDividerHeight(0);
        lvLocation.setAdapter(locationAdapter);

        lvLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edtTxtLocation.setText(locationList.get(i).getAddress());
            }
        });
    }


    /**
     * 获取常用货站
     */
    private void getCommonStation() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "purchase_station_list");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<LineList> list = GsonUtil.getEntityList(jsonObj.getJSONObject("data").getJSONArray("items").toString(), LineList.class);
                        stationList.clear();
                        stationList.addAll(list);
                        stationAdapter.notifyDataSetChanged();

                        if (stationList.size() > 0) {
                            llCommonStation.setVisibility(View.VISIBLE);
                            selectedStation = stationList.get(0);
                            txtStation.setText(selectedStation.getOfficial_name() + " " + selectedStation.getOfficial_address());
                        } else {
                            llCommonStation.setVisibility(View.GONE);
                        }
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                threadCount--;
                if (threadCount == 0)
                    progress.dismiss();
            }
        });
    }

    /**
     * 删除常用货站
     */
    private void deleteCommonStation(String id) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "delete_purchase_station");
        params.put("uid", getUserID());
        params.put("station_id", id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        getCommonStation();
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 获取常用地点
     */
    private void getCommonLocation() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "purchase_place_list");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<Address> list = GsonUtil.getEntityList(jsonObj.getJSONObject("data").getJSONArray("items").toString(), Address.class);
                        locationList.clear();
                        locationList.addAll(list);
                        locationAdapter.notifyDataSetChanged();

                        if (locationList.size() > 0) {
                            llCommonLocation.setVisibility(View.VISIBLE);
                            edtTxtLocation.setText(locationList.get(0).getAddress());
                        } else {
                            llCommonLocation.setVisibility(View.GONE);
                        }
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                threadCount--;
                if (threadCount == 0)
                    progress.dismiss();
            }
        });
    }


    /**
     * 删除常用地点
     */
    private void deleteCommonLocation(String id) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "delete_purchase_place");
        params.put("uid", getUserID());
        params.put("place_id", id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        getCommonLocation();
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    @OnCheckedChanged({R.id.chk0, R.id.chk1})
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            switch (compoundButton.getId()) {
                case R.id.chk0: //指定货站
                    llStation.setVisibility(View.VISIBLE);
                    llLocation.setVisibility(View.GONE);
                    chk1.setChecked(false);
                    type = "0";
                    break;
                case R.id.chk1: //指定地点
                    llStation.setVisibility(View.GONE);
                    llLocation.setVisibility(View.VISIBLE);
                    chk0.setChecked(false);
                    type = "1";
                    break;
            }
        }
    }


    @Override
    public void onWhichClick(View view, int position, int tag) {
        switch (view.getId()) {
            case R.id.imgViDelete:
                if (type.equals("0"))
                    deleteCommonStation(stationList.get(position).getId());
                else
                    deleteCommonLocation(locationList.get(position).getId());
                break;
        }
    }


    @OnClick({R.id.txtConfirm, R.id.txtStation})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txtConfirm: //确定
                if (TextUtils.isEmpty(edtTxtConsigner.getText().toString().trim())) {
                    showErrorToast("请输入收货人姓名");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtMobile.getText().toString().trim())) {
                    showErrorToast("请输入联系电话");
                    return;
                }
                if (type.equals("0")) {
                    if (selectedStation == null) {
                        showErrorToast("请选择货站");
                        return;
                    }
                } else {
                    if (TextUtils.isEmpty(edtTxtLocation.getText().toString().trim())) {
                        showErrorToast("请输入指定地点");
                        return;
                    }
                }

                Address data = new Address();
                data.setName(edtTxtConsigner.getText().toString().trim());
                data.setMobile(edtTxtMobile.getText().toString().trim());
                data.setAddress_type(type);

                if (type.equals("0")) {
                    data.setStation_id(selectedStation.getId());
                    data.setAddress(selectedStation.getOfficial_name() + " " + selectedStation.getOfficial_address());
                } else {
                    data.setAddress(edtTxtLocation.getText().toString().trim());
                }

                intent = new Intent();
                intent.putExtra(Const.KEY_OBJECT, data);
                setResult(0, intent);
                finish();
                break;
            case R.id.txtStation: //选择货站
                intent = new Intent(context, ChooseStationActivity.class);
                startActivityForResult(intent, Const.REQUEST_CODE_SELECT_STATION);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        if (requestCode == Const.REQUEST_CODE_SELECT_STATION) {
            selectedStation = (LineList) data.getSerializableExtra(Const.KEY_OBJECT);
            txtStation.setText(selectedStation.getOfficial_name() + " " + selectedStation.getOfficial_address());
        }
    }

}
