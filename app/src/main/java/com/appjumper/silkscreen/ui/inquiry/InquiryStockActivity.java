package com.appjumper.silkscreen.ui.inquiry;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.home.stock.StockDetailActivity;
import com.appjumper.silkscreen.ui.my.adapter.ViewOrderListViewAdapter;
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
import butterknife.OnClick;

import static com.appjumper.silkscreen.R.id.et_remark;


/**
 * 针对已发布的现货产品进行询价
 * Created by Botx on 2017/6/30.
 */

public class InquiryStockActivity extends BaseActivity {

    @Bind(R.id.lvSpec)
    ListView lvSpec;
    @Bind(R.id.tv_info_length)
    TextView tvInfoLength;
    @Bind(et_remark)
    EditText etRemark;

    private ServiceProduct product;
    private String user_ids;
    private String type;

    private long expiry_datatime = 3600;
    private String[] expiry = {"1小时", "5小时", "12小时", "1天", "2天", "3天"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_stock);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        product = (ServiceProduct) intent.getSerializableExtra(Const.KEY_OBJECT);
        type = intent.getStringExtra("type");
        user_ids = intent.getStringExtra("eid");

        initTitle(product.getName());
        initListView();
        initBack();
        initProgressDialog(false, "提交中...");

        etRemark.setHint(product.getRemark());
    }


    private void initListView() {
        List<Spec> service_spec = product.getProduct_spec();
        //过滤空字段
        List<Spec> tempList = new ArrayList<>();

        for (int i = 0; i < service_spec.size(); i++) {
            Spec spec = service_spec.get(i);
            if (!TextUtils.isEmpty(spec.getValue().trim())) {
                tempList.add(spec);
            }
        }

        lvSpec.setAdapter(new ViewOrderListViewAdapter(context, tempList));
    }


    /**
     * 提交
     */
    private void submit() {
        RequestParams params = MyHttpClient.getApiParam("inquiry", "add");
        for (Spec spec : product.getProduct_spec()) {
            params.put(spec.getFieldname(), spec.getValue());
        }

        params.put("uid", getUserID());
        params.put("remark", etRemark.getText().toString().trim());
        params.put("expiry_date", expiry_datatime + "");
        params.put("product_id", product.getId());
        params.put("user_ids", user_ids);
        params.put("type", type);

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
                        start_Activity(context, InquiryCompleteActivity.class);
                        CommonApi.addLiveness(getUserID(), 5);
                        finish();

                        if (StockDetailActivity.instance != null)
                            StockDetailActivity.instance.finish();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        switch (requestCode) {
            case 3://信息时长
                int expiry_date = Integer.parseInt(data.getStringExtra("val"));
                switch (expiry_date) {
                    case 0://一小时
                        expiry_datatime = 3600 * 1;
                        break;
                    case 1://5小时
                        expiry_datatime = 3600 * 5;
                        break;
                    case 2://12小时
                        expiry_datatime = 3600 * 12;
                        break;
                    case 3://一天
                        expiry_datatime = 3600 * 24;
                        break;
                    case 4://两天
                        expiry_datatime = 3600 * 48;
                        break;
                    case 5://三天
                        expiry_datatime = 3600 * 72;
                        break;
                }
                tvInfoLength.setText(expiry[expiry_date]);
                break;
            default:
                break;
        }
    }


    @OnClick({R.id.txtConfirm, R.id.tv_info_length})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm:
                submit();
                break;
            case R.id.tv_info_length:
                Intent intent = new Intent(context, InformationSelectActivity.class);
                Bundle b = new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title", "信息时长");
                startActivityForResult(intent, 3);
                break;
            default:
                break;
        }
    }


}
