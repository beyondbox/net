package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.util.Const;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加服务完成
 * Created by Botx on 2017/4/21.
 */

public class AddServiceCompleteActivity extends BaseActivity {

    @Bind(R.id.txtMessage)
    TextView txtMessage;

    private int serviceType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_complete);
        ButterKnife.bind(this);

        initTitle("添加成功");

        Intent intent = getIntent();
        serviceType = Integer.parseInt(intent.getStringExtra(Const.KEY_SERVICE_TYPE));
        if (intent.hasExtra(Const.KEY_MESSAGE)) {
            txtMessage.setText(intent.getStringExtra(Const.KEY_MESSAGE));
        }
    }

    @OnClick({R.id.txtConfirm, R.id.txtAdd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm:
                finish();
                break;
            case R.id.txtAdd:
                continueAdd();
                break;
            default:
                break;
        }
    }


    /**
     * 继续添加
     */
    private void continueAdd() {
        switch (serviceType) {
            case Const.SERVICE_TYPE_ORDER://丝网订做
                goToProductSelect(Const.SERVICE_TYPE_ORDER, Const.REQUEST_CODE_RELEASE_ORDER);
                break;
            case Const.SERVICE_TYPE_PROCESS://丝网加工
                goToProductSelect(Const.SERVICE_TYPE_PROCESS, Const.REQUEST_CODE_RELEASE_PROCESS);
                break;
            case Const.SERVICE_TYPE_STOCK://丝网现货
                goToProductSelect(Const.SERVICE_TYPE_STOCK, Const.REQUEST_CODE_RELEASE_STOCK);
                break;
            case Const.SERVICE_TYPE_LOGISTICS://物流路线
                start_Activity(this, EnterpriseReleaseActivity.class);
                break;
            default:
                break;
        }
    }



    /**
     * 跳转到产品选择界面
     */
    private void goToProductSelect(int serviceType, int requestCode) {
        Intent intent = new Intent(context, ProductSelectActivity.class);
        intent.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        ServiceProduct product = (ServiceProduct) data.getSerializableExtra(Const.KEY_OBJECT);
        Intent intent = null;
        switch (requestCode) {
            case Const.REQUEST_CODE_RELEASE_ORDER:
                intent = new Intent(context, SpecificationActivity.class);
                intent.putExtra("service", product);
                intent.putExtra("type", Const.SERVICE_TYPE_ORDER + "");
                break;
            case Const.REQUEST_CODE_RELEASE_PROCESS:
                intent = new Intent(context, SpecificationActivity.class);
                intent.putExtra("service", product);
                intent.putExtra("type", Const.SERVICE_TYPE_PROCESS + "");
                break;
            case Const.REQUEST_CODE_RELEASE_STOCK:
                intent = new Intent(context, SpecificationStockActivity.class);
                intent.putExtra("service", product);
                intent.putExtra("type", Const.SERVICE_TYPE_STOCK + "");
                break;
            default:
                break;
        }

        startActivity(intent);
        finish();
    }

}
