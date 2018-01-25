package com.appjumper.silkscreen.ui.home.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.AskBuyOffer;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.util.manager.MyUserManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 求购列表adapter
 * Created by Botx on 2017/10/18.
 */

public class AskBuyListAdapter extends BaseQuickAdapter<AskBuy, BaseViewHolder> {

    private MyUserManager userManager = MyApplication.appContext.getMyUserManager();

    public AskBuyListAdapter(@LayoutRes int layoutResId, @Nullable List<AskBuy> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AskBuy item) {
        String loginId = userManager.getUserId();

        Picasso.with(mContext)
                .load(item.getImg())
                .resize(DisplayUtil.dip2px(mContext, 50), DisplayUtil.dip2px(mContext, 50))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.imgViHead));

        /*String newName = "";
        if (TextUtils.isEmpty(item.getNickname())) {
            String mobile = item.getMobile();
            newName = mobile.substring(0, 3) + "***" + mobile.substring(8, 11);
        } else {
            String nickName = item.getNickname();
            int length = nickName.length();
            switch (length) {
                case 1:
                    newName = nickName + "***" + nickName;
                    break;
                case 2:
                    newName = nickName.substring(0, 1) + "***" + nickName.substring(1, 2);
                    break;
                default:
                    newName = nickName.substring(0, 1) + "***" + nickName.substring(length - 1, length);
                    break;
            }
        }*/

        TextView txtoffer = helper.getView(R.id.txtOffer);
        List<AskBuyOffer> offerList = item.getOffer_list();
        if (offerList != null && offerList.size() > 0) {
            txtoffer.setText("报价");
            txtoffer.setEnabled(true);
            for (AskBuyOffer offer : offerList) {
                if (loginId.equals(offer.getUser_id())) {
                    txtoffer.setText("已报价");
                    if (userManager.getInstance().getAdmin_purchase_add().equals("1"))
                        txtoffer.setEnabled(true);
                    else
                        txtoffer.setEnabled(false);
                    break;
                }
            }
        } else {
            txtoffer.setText("报价");
            txtoffer.setEnabled(true);
        }


        long expiryTime = AppTool.getTimeMs(item.getExpiry_date(), "yy-MM-dd HH:mm:ss");
        TextView txtState = helper.getView(R.id.txtState);
        if (System.currentTimeMillis() < expiryTime) {
            txtState.setText("报价中 " + item.getExpiry_date().substring(5, 16) + "截止");
            txtState.setTextColor(mContext.getResources().getColor(R.color.orange_color));
            if (loginId.equals(item.getUser_id()))
                txtoffer.setVisibility(View.INVISIBLE);
            else
                txtoffer.setVisibility(View.VISIBLE);
        } else {
            txtState.setText("报价结束");
            txtState.setTextColor(mContext.getResources().getColor(R.color.light_gray_color));
            txtoffer.setVisibility(View.INVISIBLE);
        }


        if (item.getPruchase_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
            helper.setText(R.id.txtName, "求购G" + item.getId());
        else
            helper.setText(R.id.txtName, "求购C" + item.getId());


        helper.setText(R.id.txtTime, item.getCreate_time().substring(5, 16))
                .setText(R.id.txtContent, item.getPurchase_content())
                .setText(R.id.txtReadNum, "浏览" + "(" + item.getConsult_num() + ")")
                .setText(R.id.txtOfferNum, "报价" + "(" + item.getOffer_num() + ")")
                .setVisible(R.id.imgViMarkSelf, loginId.equals(item.getUser_id()))
                .addOnClickListener(R.id.txtOffer);

        if (item.getPurchase_num().equals("0"))
            helper.setText(R.id.txtTitle, item.getProduct_name());
        else
            helper.setText(R.id.txtTitle, item.getProduct_name() + " " + item.getPurchase_num() + item.getPurchase_unit());


        TextView txtMark = helper.getView(R.id.txtMark);
        int infoType = Integer.valueOf(item.getPruchase_type());
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
                //helper.setText(R.id.txtName, "丝网+官方");
                break;
            default:
                break;
        }

        if (infoType != Const.INFO_TYPE_OFFICIAL) {
            String authState = item.getEnterprise_auth_status();
            if (!TextUtils.isEmpty(authState) && authState.equals("2")) {
                txtMark.setText("企业");
                txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
            } else {
                txtMark.setText("个人");
                txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
            }
        }

        final List<Avatar> imgList = item.getImg_list();
        if (imgList != null && imgList.size() > 0) {
            helper.setVisible(R.id.gridImg, true);
            GridView gridImg = helper.getView(R.id.gridImg);
            AskBuyImageAdapter imgAdapter = new AskBuyImageAdapter(mContext, imgList);
            gridImg.setAdapter(imgAdapter);

            gridImg.setClickable(false);
            gridImg.setEnabled(false);
            gridImg.setPressed(false);
        } else {
            helper.setVisible(R.id.gridImg, false);
        }
    }

}
