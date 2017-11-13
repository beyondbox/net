package com.appjumper.silkscreen.ui.home.logistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Province;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.ProvinceAdapter;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 物流-货站
 * Created by Botx on 2017/9/29.
 */

public class LineFragment extends BaseFragment {

    @Bind(R.id.lvProvince)
    ListView lvProvince;
    @Bind(R.id.frameLine)
    FrameLayout frameLine;

    private List<Province> provinceList;
    private ProvinceAdapter provinceAdapter;
    private List<Fragment> fragList;
    private FragAdapter fragAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        initListView();
        getProvince();
    }


    private void initListView() {
        provinceList = new ArrayList<>();
        provinceAdapter = new ProvinceAdapter(context, provinceList);
        lvProvince.setAdapter(provinceAdapter);
        lvProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                provinceAdapter.changeSelected(position);
                switchFragment(position);
            }
        });

        fragList = new ArrayList<>();
        fragAdapter = new FragAdapter(context.getSupportFragmentManager());
    }



    /**
     * 获取省份数据
     */
    private void getProvince() {
        RequestParams params = MyHttpClient.getApiParam("line", "line_province");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<Province> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), Province.class);
                        provinceList.clear();
                        provinceList.addAll(list);
                        provinceAdapter.notifyDataSetChanged();

                        fragList.clear();
                        for (Province province : provinceList) {
                            Fragment fragment = new LineListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("id", province.getProvince_id());
                            fragment.setArguments(bundle);
                            fragList.add(fragment);
                        }
                        if (fragList.size() > 0)
                            switchFragment(0);
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (!isViewCreated) return;
                showFailTips(getResources().getString(R.string.requst_fail));
            }
        });
    }


    /**
     * 切换Fragment
     * @param position
     */
    private void switchFragment(int position) {
        Fragment fragment = (Fragment) fragAdapter.instantiateItem(frameLine, position);
        fragAdapter.setPrimaryItem(frameLine, position, fragment);
        fragAdapter.finishUpdate(frameLine);
    }


    /**
     * Fragment管理适配器
     */
    private class FragAdapter extends FragmentPagerAdapter {

        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }
    }


    @OnClick({R.id.llSearch})
    public void onClick() {
        switch (R.id.llSearch) {
            case R.id.llSearch:
                start_Activity(context, LineSearchActivity.class);
                break;
            default:
                break;
        }
    }



    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
