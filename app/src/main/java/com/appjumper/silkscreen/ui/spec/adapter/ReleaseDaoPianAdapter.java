package com.appjumper.silkscreen.ui.spec.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.DPChild;
import com.appjumper.silkscreen.bean.DPGroup;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/**
 * 刀片刺绳adapter
 * Created by Botx on 2017/6/19.
 */

public class ReleaseDaoPianAdapter extends BaseExpandableListAdapter {

    private final int TYPE_IMAGE = 1;
    private final int TYPE_TEXT = 2;

    public static final int CHOICE_MODE_SINGLE = 41; //单选
    public static final int CHOICE_MODE_MULTIPLE = 42; //多选

    private Context context;
    private Map<DPGroup, List<DPChild>> guigeMap;
    private List<DPGroup> groupList;
    private int choiceMode = CHOICE_MODE_MULTIPLE; //默认为多选模式


    public ReleaseDaoPianAdapter(Context context, Map<DPGroup, List<DPChild>> guigeMap, List<DPGroup> groupList) {
        this.context = context;
        this.guigeMap = guigeMap;
        this.groupList = groupList;
    }

    public void setChoiceMode(int choiceMode) {
        this.choiceMode = choiceMode;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return guigeMap.get(groupList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return guigeMap.get(groupList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public int getItemViewType(int position) {
        int type;
        if (position == 0)
            type = TYPE_IMAGE;
        else
            type = TYPE_TEXT;

        return type;
    }


    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = null;
        if (choiceMode == CHOICE_MODE_SINGLE)
            view = LayoutInflater.from(context).inflate(R.layout.item_daopian_group_single, null);
        else
            view = LayoutInflater.from(context).inflate(R.layout.item_daopian_group_multi, null);

        CheckBox chkGroup = (CheckBox) view.findViewById(R.id.chkGroup);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);

        DPGroup group = groupList.get(groupPosition);
        chkGroup.setChecked(group.isChecked());
        txtName.setText(group.getName());

        chkGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                groupList.get(groupPosition).setChecked(isChecked);

                if (choiceMode == CHOICE_MODE_SINGLE) {
                    if (isChecked) {
                        for (int i = 0; i < groupList.size(); i++) {
                            if (i != groupPosition)
                                groupList.get(i).setChecked(false);
                        }

                        notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = null;
            if (getItemViewType(childPosition) == TYPE_IMAGE)
                view = LayoutInflater.from(context).inflate(R.layout.item_daopian_child_image, null);
            else
                view = LayoutInflater.from(context).inflate(R.layout.item_daopian_child_text, null);

        DPChild child = (DPChild) getChild(groupPosition, childPosition);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        txtName.setText(child.getName());

        if (getItemViewType(childPosition) == TYPE_IMAGE) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            Picasso.with(context)
                    .load(child.getValue())
                    .placeholder(R.mipmap.img_error)
                    .error(R.mipmap.img_error)
                    .into(imageView);
        } else {
            TextView txtValue = (TextView) view.findViewById(R.id.txtValue);
            txtValue.setText(child.getValue());
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
