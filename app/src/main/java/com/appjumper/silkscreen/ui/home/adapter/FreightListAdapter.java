package com.appjumper.silkscreen.ui.home.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.manager.MyUserManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


/**
 * 空车配货列表adapter
 * Created by Botx on 2017/10/26.
 */

public class FreightListAdapter extends BaseQuickAdapter<Freight, BaseViewHolder> {

    private MyUserManager userManager = MyApplication.appContext.getMyUserManager();

    public FreightListAdapter(@LayoutRes int layoutResId, @Nullable List<Freight> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Freight item) {
        String loginId = userManager.getUserId();

        helper.setText(R.id.txtTitle, item.getFrom_name() + " - " + item.getTo_name())
                .setText(R.id.txtCarModel, item.getLengths_name() + "/" + item.getModels_name())
                .setText(R.id.txtProduct, item.getWeight() + item.getProduct_name())
                .setText(R.id.txtTime, item.getExpiry_date().substring(5, 16) + "装车")
                .setVisible(R.id.imgViCertiGreen, item.getAuth_status().equals("2"))
                .setVisible(R.id.imgViCertiBlue, item.getEnterprise_auth_status().equals("2"))
                .setVisible(R.id.imgViCertiYellow, item.getEnterprise_productivity_auth_status().equals("2"))
                .setVisible(R.id.lLaytCerti, !loginId.equals(item.getUser_id()))
                .setVisible(R.id.imgViMarkSelf, loginId.equals(item.getUser_id()));

        TextView txtName = helper.getView(R.id.txtName);
        String carNum = "/发车 " + item.getDepart_num() + "次";
        String uid = item.getUser_id();
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
        txtName.setText(newName + carNum);


        TextView txtOfferNum = helper.getView(R.id.txtOfferNum);
        int offerNum = Integer.valueOf(item.getOffer_num());
        if (offerNum > 0)
            txtOfferNum.setText(offerNum + "报价");
        else
            txtOfferNum.setText("暂无报价");

        TextView txtState = helper.getView(R.id.txtState);
        int state = Integer.valueOf(item.getExamine_status());
        switch (state) {
            case Const.FREIGHT_AUDIT_PASS:
                txtState.setText("找车中");
                txtState.setTextColor(mContext.getResources().getColor(R.color.orange_color));
                break;
            case Const.FREIGHT_DRIVER_PAYING:
                txtState.setText("已调车");
                txtState.setTextColor(mContext.getResources().getColor(R.color.green_color));
                break;
            case Const.FREIGHT_GOTO_LOAD:
                txtState.setText("已调车");
                txtState.setTextColor(mContext.getResources().getColor(R.color.green_color));
                break;
        }


        TextView txtMark = helper.getView(R.id.txtMark);
        if (!TextUtils.isEmpty(item.getCar_product_type())) {
            int infoType = Integer.valueOf(item.getCar_product_type());
            switch (infoType) {
                case Const.INFO_TYPE_PER:
                    txtMark.setText("个人");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
                    break;
                case Const.INFO_TYPE_COM:
                    txtMark.setText("企业");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
                    break;
                case Const.INFO_TYPE_OFFICIAL:
                    txtMark.setText("官方");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_official_bg);

                    String endName = "";
                    String fullName = item.getTo_name();
                    String [] arr = fullName.split(",");
                    String province = arr[1];
                    if (province.contains("省"))
                        endName = province.substring(0, province.length() - 1) + arr[2];
                    else
                        endName = province + arr[2];

                    if (endName.contains("市"))
                        endName = endName.substring(0, endName.length() - 1);

                    helper.setText(R.id.txtName, "丝网加物流专员-" + item.getAdmin_name() + carNum)
                            .setText(R.id.txtTitle, item.getFrom_name() + " - " + endName);
                    break;
                default:
                    break;
            }
        }

    }

}
