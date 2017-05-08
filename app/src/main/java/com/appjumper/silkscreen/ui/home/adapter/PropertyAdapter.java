package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.view.MyViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016-10-11.
 */
public class PropertyAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, Object>> mList;
    private ArrayList<HashMap<String, TextView[]>> mViewList;
    private Handler mHandler;
    private CallbackListener callbackListener;
    //用于保存用户的属性集合
    private HashMap<String, String> selectProMap = new HashMap<String, String>();

    /**
     * 返回选中的属性
     *
     * @return
     */


    public void setSelectProMap(HashMap<String, String> selectProMap) {
        this.selectProMap = selectProMap;
    }


    public void setCallbackListener(CallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    public PropertyAdapter(Handler handler, Context context, ArrayList<HashMap<String, Object>> list) {
        super();
        this.mHandler = handler;
        this.mContext = context;
        this.mList = list;
        mViewList = new ArrayList<>();
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
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.item_lv_property, null, true);
            holder = new ViewHolder();

            // 获取控件对象
            holder.tvPropName = (TextView) convertView
                    .findViewById(R.id.tv_property_name);
            // 设置控件集到convertView
            holder.vgPropContents = (MyViewGroup) convertView.findViewById(R.id.my_view_group);
            holder.view = convertView.findViewById(R.id.view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (this.mList != null) {
            ArrayList<String> labels = (ArrayList<String>) this.mList.get(position).get("label");

            String type = (String) this.mList.get(position).get(
                    "type");
            holder.tvPropName.setText(type);//规格名称
            //动态加载标签
            //判断布局中的子控件是否为0，如果不为0，就不添加了，防止ListView滚动时重复添加
            if (holder.vgPropContents.getChildCount() == 0) {
                TextView[] textViews = new TextView[labels.size()];
                //设置每个标签的文本和布局

                for (int i = 0; i < labels.size(); i++) {
                    TextView textView = new TextView(mContext);
                    textView.setGravity(17);
                    textView.setPadding(25, 15, 25, 25);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 48, 48);
                    textView.setLayoutParams(lp);
                    textViews[i] = textView;
                    textViews[i].setBackgroundResource(R.drawable.specifications_unselect_background);
                    textViews[i].setText(labels.get(i));
                    textViews[i].setTag(i);
                    textViews[i].setTextSize(13);
                    textViews[i].setTextColor(mContext.getResources().getColor(R.color.black_color));

                    holder.vgPropContents.addView(textViews[i]);
                }
                //绑定标签的Click事件
                for (int j = 0; j < textViews.length; j++) {
                    textViews[j].setTag(textViews);
                    textViews[j].setOnClickListener(new LabelClickListener(type, position));
                }
//                if (position == mList.size() - 1) {
//                    holder.view.setVisibility(View.GONE);
//                }
            }
            /**判断之前是否已选中标签*/
            if (selectProMap.get(type) != null) {
                for (int h = 0; h < holder.vgPropContents.getChildCount(); h++) {
                    TextView v = (TextView) holder.vgPropContents.getChildAt(h);
                    if (selectProMap.get(type).equals(v.getText().toString())) {
                        v.setBackgroundResource(R.drawable.theme_background_login_btn);
                        v.setTextColor(mContext.getResources().getColor(R.color.while_color));
                        selectProMap.put(type, v.getText().toString());
                    }
                }
            }

        }
        return convertView;
    }

    /*定义item对象*/
    public class ViewHolder {

        TextView tvPropName;
        MyViewGroup vgPropContents;
        View view;
    }


    class LabelClickListener implements View.OnClickListener {
        private String type;
        private int position;

        public LabelClickListener(String type, int position) {

            this.type = type;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            TextView[] textViews = (TextView[]) v.getTag();
            TextView tv = (TextView) v;
            for (int i = 0; i < textViews.length; i++) {
                //让点击的标签背景变成橙色，字体颜色变为白色
                if (tv.equals(textViews[i])) {
                    textViews[i].setBackgroundResource(R.drawable.theme_background_login_btn);
                    textViews[i].setTextColor(mContext.getResources().getColor(R.color.while_color));
                    selectProMap.put(type, textViews[i].getText().toString());
                    callbackListener.onCallbackListener(type, i, position);
                } else {
                    //其他标签背景变成白色，字体颜色为黑色
                    textViews[i].setBackgroundResource(R.drawable.specifications_unselect_background);
                    textViews[i].setTextColor(mContext.getResources().getColor(R.color.black_color));
                }
            }

        }

    }

    public interface CallbackListener {
        void onCallbackListener(String type, int i, int position);

    }
}
