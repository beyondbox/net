package com.appjumper.silkscreen.ui.money.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.AskBuyOffer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.appjumper.silkscreen.util.Applibrary.mContext;

/**
 * 求购报价列表adapter
 * Created by Botx on 2017/10/19.
 */

public class AskBuyOfferAdapter extends MyBaseAdapter<AskBuyOffer> {

    public AskBuyOfferAdapter(Context context, List<AskBuyOffer> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_askbuy_offer, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        AskBuyOffer item = list.get(position);

        Picasso.with(mContext)
                .load(item.getProduct_img())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(vh.imgViHead);

        String newName = "";
        if (TextUtils.isEmpty(item.getPurchase_user_nicename())) {
            String mobile = item.getPurchase_user_mobile();
            if (!TextUtils.isEmpty(mobile))
                newName = mobile.substring(0, 3) + "***" + mobile.substring(8, 11);
        } else {
            String nickName = item.getPurchase_user_nicename();
            if (!TextUtils.isEmpty(nickName)) {
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
            }
        }

        vh.txtName.setText(newName);
        vh.txtTime.setText(item.getCreate_time().substring(5, 16));
        vh.txtContent.setText(item.getOffer_content());
        vh.txtPrice.setText("报价金额：" + item.getMoney() + "元");

        if (TextUtils.isEmpty(item.getOffer_status())) {
            vh.txtState.setText("");
        } else {
            switch (item.getOffer_status()) {
                case "0":
                    vh.txtState.setText("尚未采用");
                    break;
                case "1":
                    vh.txtState.setText("已付订金");
                    break;
                case "2":
                    vh.txtState.setText("全额支付");
                    break;
                case "3":
                    vh.txtState.setText("交易结束");
                    break;
            }
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.txtName)
        TextView txtName;
        @Bind(R.id.imgViHead)
        ImageView imgViHead;
        @Bind(R.id.txtTime)
        TextView txtTime;
        @Bind(R.id.txtContent)
        TextView txtContent;
        @Bind(R.id.txtPrice)
        TextView txtPrice;
        @Bind(R.id.txtState)
        TextView txtState;
    }
}
