package com.appjumper.silkscreen.ui.spec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 产品规格选择adapter
 * Created by Botx on 2017/4/19.
 */

public class SpecSelectAdapter extends MyBaseAdapter<String> {

    public static final int CHOICE_MODE_SINGLE = 31; //单选
    public static final int CHOICE_MODE_MULTIPLE = 32; //多选

    private int choiceMode = CHOICE_MODE_MULTIPLE; //默认为多选模式

    private int currSelected = -1;

    public SpecSelectAdapter(Context context, List<String> list) {
        super(context, list);
    }

    public void setChoiceMode(int choiceMode) {
        this.choiceMode = choiceMode;
    }

    public void changeSelected(int position){
        currSelected = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_spec_choice2, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.chkSpec.setText(list.get(position));

        if (choiceMode == CHOICE_MODE_SINGLE) { //单选模式下，布局中已屏蔽checkbox自身的点击事件
            if (currSelected == position) {
                vh.chkSpec.setChecked(true);
            } else {
                vh.chkSpec.setChecked(false);
            }
        } else { //多选模式下，启用checkbox自身的点击事件
            vh.chkSpec.setClickable(true);
        }

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.chkSpec)
        CheckBox chkSpec;
    }
}
