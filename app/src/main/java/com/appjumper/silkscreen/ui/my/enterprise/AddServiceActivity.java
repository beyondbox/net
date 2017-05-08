package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.my.PersonalAuthenticationActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-21.
 * 添加服务
 */
public class AddServiceActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        ActivityTaskManager.getInstance().putActivity(this);
        initBack();
        ButterKnife.bind(this);
        initTitle("添加服务");
    }

    @OnClick({R.id.rl_order,R.id.rl_processing,R.id.rl_spot,R.id.rl_logistics})
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rl_order://丝网订做
                goToProductSelect(Const.SERVICE_TYPE_ORDER, Const.REQUEST_CODE_RELEASE_ORDER);
                break;
            case R.id.rl_processing://丝网加工
                goToProductSelect(Const.SERVICE_TYPE_PROCESS, Const.REQUEST_CODE_RELEASE_PROCESS);
                break;
            case R.id.rl_spot://丝网现货
                if (!getUser().getAuth_status().equals("2")) {
                    Toast.makeText(context, "您尚未通过实名认证", Toast.LENGTH_SHORT).show();
                    start_Activity(context, PersonalAuthenticationActivity.class);
                    return;
                }
                if (!getUser().getEnterprise().getEnterprise_auth_status().equals("2")) {
                    Toast.makeText(context, "您的企业尚未通过认证", Toast.LENGTH_SHORT).show();
                    start_Activity(context, EnterpriseAuthenticationActivity.class);
                    return;
                }
                goToProductSelect(Const.SERVICE_TYPE_STOCK, Const.REQUEST_CODE_RELEASE_STOCK);
                break;
            case R.id.rl_logistics://物流路线
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
    }


}
