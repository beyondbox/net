package com.appjumper.silkscreen.ui.my.driver;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.FreightOfferRecordAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 司机--已报价
 * Created by Botx on 2017/10/27.
 */

public class OfferedActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;

    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.txtName)
    TextView txtName;
    @Bind(R.id.txtTime)
    TextView txtTime;
    @Bind(R.id.txtState)
    TextView txtState;
    @Bind(R.id.txtOrderId)
    TextView txtOrderId;
    @Bind(R.id.txtCarNum)
    TextView txtCarNum;
    @Bind(R.id.txtCarModel)
    TextView txtCarModel;
    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.txtLoadTime)
    TextView txtLoadTime;
    @Bind(R.id.txtPayedType)
    TextView txtPayedType;

    @Bind(R.id.llRecord)
    LinearLayout llRecord;
    @Bind(R.id.lvRecord)
    ListView lvRecord;
    @Bind(R.id.txtRecord)
    TextView txtRecord;
    @Bind(R.id.txtMyOffer)
    TextView txtMyOffer;

    private String id;
    private Freight data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offered);
        ButterKnife.bind(context);
        initTitle("详情");
        initBack();
        initProgressDialog(false, null);

        id = getIntent().getStringExtra("id");
        getData();
    }



    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details_car_product");
        params.put("id", id);

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
                        llContent.setVisibility(View.VISIBLE);
                        data = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), Freight.class);
                        setData();
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
                if (isDestroyed())
                    return;

                progress.dismiss();
            }
        });
    }


    /**
     * 渲染数据
     */
    private void setData() {
        txtTitle.setText(data.getFrom_name() + " - " + data.getTo_name());
        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtOrderId.setText("订单编号 : " + data.getOrder_id());
        txtCarNum.setText("已发车" + data.getDepart_num() + "次");
        txtCarModel.setText(data.getLengths_name() + "/" + data.getModels_name());
        txtProduct.setText(data.getWeight() + data.getProduct_name());
        txtLoadTime.setText(data.getExpiry_date().substring(5, 16) + "装车");

        txtState.setText("已报价");

        String uid = data.getUser_id();
        String newName = "";
        switch (uid.length()) {
            case 1:
                newName = "发货厂家000" + uid;
                break;
            case 2:
                newName = "发货厂家00" + uid;
                break;
            case 3:
                newName = "发货厂家0" + uid;
                break;
            case 4:
                newName = "发货厂家" + uid;
                break;
            default:
                newName = "发货厂家" + uid.substring(0, 1) + uid.substring(uid.length() - 2, uid.length());
                break;
        }
        txtName.setText("来自 : " + newName);


        if (data.getPay_type().equals("0"))
            txtPayedType.setText("发货厂家支付运费");
        else
            txtPayedType.setText("货主支付运费");


        if (data.getCar_product_type().equals(Const.INFO_TYPE_OFFICIAL + "")) {
            String endName = "";
            String fullName = data.getTo_name();
            String [] arr = fullName.split(",");
            String province = arr[1];
            if (province.contains("省"))
                endName = province.substring(0, province.length() - 1) + arr[2];
            else
                endName = province + arr[2];

            if (endName.contains("市"))
                endName = endName.substring(0, endName.length() - 1);

            txtTitle.setText(data.getFrom_name() + " - " + endName);
            txtName.setText("来自 : 丝网加物流专员-" + data.getAdmin_name());
        }


        List<FreightOffer> offerList = data.getOffer_list();
        if (offerList != null && offerList.size() > 0) {
            llRecord.setVisibility(View.VISIBLE);

            for (int i = 0; i < offerList.size(); i++) {
                FreightOffer offer = offerList.get(i);
                if (getUserID().equals(offer.getUser_id())) {
                    txtMyOffer.setText("我的报价 : " + offer.getMoney() + offer.getMoney_unit());
                    offerList.remove(i);
                    break;
                }
            }

            FreightOfferRecordAdapter recordAdapter = new FreightOfferRecordAdapter(context, offerList);
            lvRecord.setAdapter(recordAdapter);
            if (offerList.size() > 0)
                txtRecord.setText("报价列表（" + offerList.size() + "）");
        } else {
            llRecord.setVisibility(View.GONE);
        }

    }



    @OnClick({R.id.txtCall})
    public void onClick(View view) {
        if (data == null)
            return;

        if (!checkLogined())
            return;

        switch (view.getId()) {
            case R.id.txtCall: //联系客服
                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                break;
            default:
                break;
        }
    }


}
