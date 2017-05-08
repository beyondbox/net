package com.appjumper.silkscreen.ui.money.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.view.MyViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016-10-11.
 */
public class InquirySpecificationAdapter extends BaseAdapter {
    private BaseActivity mContext;
    private ArrayList<HashMap<String, Object>> mList;

    public InquirySpecificationAdapter(BaseActivity context, ArrayList<HashMap<String, Object>> list) {
        super();
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获取list_item布局文件的视图
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.item_lv_inquiry_specification, null, true);
            holder = new ViewHolder();

            // 设置控件集到convertView
            holder.vgPropContents = (MyViewGroup) convertView.findViewById(R.id.my_view_group);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (this.mList != null) {
            ArrayList<String> labels = (ArrayList<String>) this.mList.get(position).get("label");
            ArrayList<String> values = (ArrayList<String>) this.mList.get(position).get("value");

            //动态加载标签
            //判断布局中的子控件是否为0，如果不为0，就不添加了，防止ListView滚动时重复添加
            if (holder.vgPropContents.getChildCount() == 0) {
                TextView[] textViews = new TextView[labels.size()];
                TextView[] textValues = new TextView[values.size()];
                //设置每个标签的文本和布局

                for (int i = 0; i < labels.size(); i++) {
                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layout.setLayoutParams(params);

                    TextView textView = new TextView(mContext);
                    textView.setGravity(17);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, mContext.dip(mContext,9), mContext.dip(mContext,12));
                    textView.setLayoutParams(lp);
                    textViews[i] = textView;
                    textViews[i].setText(labels.get(i));
                    textViews[i].setTag(i);
                    textViews[i].setTextSize(13);
                    textViews[i].setTextColor(mContext.getResources().getColor(R.color.black_color));
                    layout.addView(textViews[i]);

                    TextView textValue = new TextView(mContext);
                    textValue.setGravity(17);
                    LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    l.setMargins(0, 0, mContext.dip(mContext,36), mContext.dip(mContext,12));
                    textValue.setLayoutParams(l);
                    textValues[i] = textValue;
                    textValues[i].setText(values.get(i));
                    textValues[i].setTag(i);
                    textValues[i].setTextSize(13);
                    textValues[i].setTextColor(mContext.getResources().getColor(R.color.black_color));
                    layout.addView(textValues[i]);

                    holder.vgPropContents.addView(layout);
                }
            }

        }
        return convertView;
    }

    /*定义item对象*/
    public class ViewHolder {
        MyViewGroup vgPropContents;
    }
}
