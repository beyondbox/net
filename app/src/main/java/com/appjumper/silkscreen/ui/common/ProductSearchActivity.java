package com.appjumper.silkscreen.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.ui.common.adapter.ProductSearchAdapter;
import com.appjumper.silkscreen.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 本地产品搜索
 * Created by Botx on 2017/5/19.
 */

public class ProductSearchActivity extends BaseActivity {

    @Bind(R.id.lvData)
    ListView lvData;
    @Bind(R.id.tv_cancel)
    TextView tv_cancel;
    @Bind(R.id.et_search)
    EditText et_search;

    private ProductSearchAdapter productAdapter;
    private List<ServiceProduct> origList; //原始数据
    private List<ServiceProduct> filterList; //过滤后的数据

    private boolean isMultiMode = false; //多选模式
    private String action = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        origList = (List<ServiceProduct>) intent.getSerializableExtra("list");
        isMultiMode = intent.getBooleanExtra(Const.KEY_IS_MULTI_MODE, false);
        action = intent.getStringExtra(Const.KEY_ACTION);

        initListView();
        et_search.addTextChangedListener(new SearchWatcher());
    }


    private void initListView() {
        filterList = new ArrayList<>();
        filterList.addAll(origList);
        productAdapter = new ProductSearchAdapter(context, filterList, action);
        if (isMultiMode) {
            productAdapter.setMultiMode(true);
        }

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isMultiMode) {
                    Intent data = new Intent();
                    data.putExtra(Const.KEY_OBJECT, filterList.get(position));
                    setResult(0, data);
                    finish();
                }
            }
        });

        productAdapter.setOnWhichClickListener(new MyBaseAdapter.OnWhichClickListener() {
            @Override
            public void onWhichClick(View view, int position, int tag) {
                switch (view.getId()) {
                    case R.id.chkSelect:
                        if (action.equals(Const.ACTION_ATTENT_PRODUCT_MANAGE)) {
                            if (((CheckBox)view).isChecked())
                                filterList.get(position).setIs_collection("1");
                            else
                                filterList.get(position).setIs_collection("0");
                        } else if (action.equals(Const.ACTION_ADD_PRODUCT)) {
                            if (((CheckBox)view).isChecked())
                                filterList.get(position).setIs_car("1");
                            else
                                filterList.get(position).setIs_car("0");
                        }

                        productAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        });

        lvData.setAdapter(productAdapter);
    }



    private class SearchWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String content = et_search.getText().toString().trim();
            filterList.clear();

            if (TextUtils.isEmpty(content)) {
                filterList.addAll(origList);
            } else {
                for (ServiceProduct product : origList) {
                    if (product.getName().contains(content) || product.getAlias().contains(content))
                        filterList.add(product);
                }
            }

            productAdapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }



    @Override
    public void finish() {
        if (isMultiMode) {
            if (filterList.size() > 0) {
                Intent data = new Intent();
                data.putExtra("list", (ArrayList)filterList);
                setResult(0, data);
            }
        }

        super.finish();
    }



    @OnClick(R.id.tv_cancel)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            default:
                break;
        }
    }


}
