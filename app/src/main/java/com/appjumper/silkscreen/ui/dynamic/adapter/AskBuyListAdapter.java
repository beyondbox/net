package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 求购列表adapter
 * Created by Botx on 2017/10/18.
 */

public class AskBuyListAdapter extends BaseQuickAdapter<AskBuy, BaseViewHolder> {

    public AskBuyListAdapter(@LayoutRes int layoutResId, @Nullable List<AskBuy> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AskBuy item) {
        Picasso.with(mContext)
                .load(item.getImg())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.imgViHead));

        String newName = "";
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
        }


        helper.setText(R.id.txtName, newName)
                .setText(R.id.txtTime, item.getCreate_time().substring(5, 16))
                .setText(R.id.txtContent, item.getPurchase_content())
                .setText(R.id.txtReadNum, "浏览" + "(" + item.getConsult_num() + ")")
                .setText(R.id.txtOfferNum, "报价" + "(" + item.getOffer_num() + ")");


        TextView txtState = helper.getView(R.id.txtState);
        int state = Integer.valueOf(item.getExamine_status());
        switch (state) {
            case Const.ASKBUY_OFFERING:
                long expiryTime = AppTool.getTimeMs(item.getExpiry_date(), "yy-MM-dd HH:mm:ss");
                long currTime = System.currentTimeMillis();
                if (currTime < expiryTime)
                    txtState.setText(item.getExpiry_date().substring(5, 16) + "截止");
                else
                    txtState.setText("报价结束");
                break;
            case Const.ASKBUY_PAYED_SUB:
                txtState.setText("已交易");
                break;
            case Const.ASKBUY_PAYED_ALL:
                txtState.setText("已交易");
                break;
            default:
                break;
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
            /*gridImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    ArrayList<String> urls = new ArrayList<String>();
                    for (Avatar avatar : imgList) {
                        urls.add(avatar.getOrigin());
                    }
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, i);
                    mContext.startActivity(intent);
                }
            });*/
        } else {
            helper.setVisible(R.id.gridImg, false);
        }
    }

}
