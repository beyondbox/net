package com.appjumper.silkscreen.ui.home.askbuy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.askbuy.AskBuyManageActivity;
import com.appjumper.silkscreen.ui.money.MessageActivity;
import com.appjumper.silkscreen.util.Const;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布成功
 * Created by Botx on 2017/10/19.
 */

public class ReleaseCompleteActivity extends BaseActivity {

    @Bind(R.id.txtMessage)
    TextView txtMessage;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.txtManage)
    TextView txtManage;


    /**
     * 求购
     */
    public static final int TYPE_ASK_BUY = 1000;
    /**
     * 报价
     */
    public static final int TYPE_OFFER = 1001;

    private int type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_complete);
        ButterKnife.bind(context);
        initBack();

        type = getIntent().getIntExtra(Const.KEY_TYPE, TYPE_ASK_BUY);
        switch (type) {
            case TYPE_ASK_BUY:
                initTitle("发布成功");
                txtTitle.setText("发布成功");
                txtMessage.setText("您发布的信息已经提交审核\n请耐心等待！");
                txtManage.setText("管理信息");
                break;
            case TYPE_OFFER:
                initTitle("报价成功");
                txtTitle.setText("报价成功");
                txtMessage.setText("您的报价已提交，请耐心等待商家选择");
                txtManage.setText("管理报价");
                break;
            default:
                break;
        }
    }


    @OnClick(R.id.txtManage)
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txtManage:
                switch (type) {
                    case TYPE_ASK_BUY:
                        intent = new Intent(context, AskBuyManageActivity.class);
                        break;
                    case TYPE_OFFER:
                        intent = new Intent(context, MessageActivity.class);
                        break;
                }

                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }


}
