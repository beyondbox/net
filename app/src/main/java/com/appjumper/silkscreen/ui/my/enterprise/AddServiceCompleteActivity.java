package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.my.MyReleaseActivity;
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

        initTitle("发布成功");

        Intent intent = getIntent();
        serviceType = Integer.parseInt(intent.getStringExtra(Const.KEY_SERVICE_TYPE));

        if (serviceType == Const.SERVICE_TYPE_STOCK) {
            txtMessage.setText("服务添加完成，请等待后台审核");
        }

        if (intent.hasExtra(Const.KEY_MESSAGE)) {
            txtMessage.setText(intent.getStringExtra(Const.KEY_MESSAGE));
        }
    }

    @OnClick({R.id.txtManage, R.id.txtAdd})
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.txtConfirm:
                if (ProductSelectActivity.instance != null)
                    ProductSelectActivity.instance.finish();
                finish();
                break;*/
            case R.id.txtManage:
                if (ProductSelectActivity.instance != null)
                    ProductSelectActivity.instance.finish();
                start_Activity(context, MyReleaseActivity.class);
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
            case Const.SERVICE_TYPE_LOGISTICS://物流路线
                start_Activity(this, EnterpriseReleaseActivity.class);
                finish();
                break;
            default:
                finish();
                break;
        }
    }

}
