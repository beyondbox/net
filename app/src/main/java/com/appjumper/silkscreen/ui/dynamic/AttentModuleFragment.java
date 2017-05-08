package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.base.BaseRecyclerAdapter;
import com.appjumper.silkscreen.bean.AttentModule;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.ui.common.AddressSelectCityActivity;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.dynamic.adapter.AttentModuleAdapter;
import com.appjumper.silkscreen.ui.home.equipment.SelectActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.ItemSpaceDecorationGrid;
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
 * 关注的版块
 * Created by Botx on 2017/3/28.
 */

public class AttentModuleFragment extends BaseFragment {

    @Bind(R.id.recyclerLogistics)
    RecyclerView recyclerLogistics;
    @Bind(R.id.recyclerProduct)
    RecyclerView recyclerProduct;
    @Bind(R.id.recyclerDevice)
    RecyclerView recyclerDevice;
    @Bind(R.id.recyclerPlant)
    RecyclerView recyclerPlant;
    @Bind(R.id.recyclerPost)
    RecyclerView recyclerPost;

    @Bind(R.id.txtLogisticsManage)
    TextView txtLogisticsManage;
    @Bind(R.id.txtProductManage)
    TextView txtProductManage;
    @Bind(R.id.txtDeviceManage)
    TextView txtDeviceManage;
    @Bind(R.id.txtPlantManage)
    TextView txtPlantManage;
    @Bind(R.id.txtPostManage)
    TextView txtPostManage;

    private List<AttentModule> logisticsList; //物流地点
    private List<AttentModule> productList; //在找车的货物
    private List<AttentModule> deviceList; //出售的设备
    private List<AttentModule> plantList; //出租厂房的地点
    private List<AttentModule> postList; //招聘职位

    private AttentModuleAdapter logisticsAdapter;
    private AttentModuleAdapter productAdapter;
    private AttentModuleAdapter deviceAdapter;
    private AttentModuleAdapter plantAdapter;
    private AttentModuleAdapter postAdapter;

    private DynamicManageActivity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attent_module, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        activity = (DynamicManageActivity) getActivity();
        initRecyclerView();
        initProgressDialog();
        getAttentData();
    }


    private void initRecyclerView() {
        logisticsList = new ArrayList<>();
        productList = new ArrayList<>();
        deviceList = new ArrayList<>();
        plantList = new ArrayList<>();
        postList = new ArrayList<>();

        /*AttentModule logistics1 = new AttentModule();
        logistics1.setName("安平");
        logisticsList.add(logistics1);
        AttentModule logistics2 = new AttentModule();
        logistics2.setName("成都");
        logisticsList.add(logistics2);
        AttentModule logistics3 = new AttentModule();
        logistics3.setName("乌鲁木齐");
        logisticsList.add(logistics3);

        AttentModule product1 = new AttentModule();
        product1.setName("金刚网");
        productList.add(product1);
        AttentModule product2 = new AttentModule();
        product2.setName("石笼网");
        productList.add(product2);

        AttentModule device1 = new AttentModule();
        device1.setName("金刚网织机");
        deviceList.add(device1);
        AttentModule device2 = new AttentModule();
        device2.setName("直丝机");
        deviceList.add(device2);

        AttentModule post1 = new AttentModule();
        post1.setName("会计");
        postList.add(post1);

        AttentModule plant1 = new AttentModule();
        plant1.setName("安平");
        plantList.add(plant1);*/

        recyclerLogistics.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerProduct.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerDevice.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerPost.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerPlant.setLayoutManager(new GridLayoutManager(context, 3));

        ItemSpaceDecorationGrid itemDecoration = new ItemSpaceDecorationGrid(3, 7, 8);
        recyclerLogistics.addItemDecoration(itemDecoration);
        recyclerProduct.addItemDecoration(itemDecoration);
        recyclerDevice.addItemDecoration(itemDecoration);
        recyclerPost.addItemDecoration(itemDecoration);
        recyclerPlant.addItemDecoration(itemDecoration);

        logisticsAdapter = new AttentModuleAdapter(context, logisticsList);
        logisticsAdapter.setOnWhichClickListener(new OnWhichClickLogistic());
        recyclerLogistics.setAdapter(logisticsAdapter);

        productAdapter = new AttentModuleAdapter(context, productList);
        productAdapter.setOnWhichClickListener(new OnWhichClickProduct());
        recyclerProduct.setAdapter(productAdapter);

        deviceAdapter = new AttentModuleAdapter(context, deviceList);
        deviceAdapter.setOnWhichClickListener(new OnWhichClickDevice());
        recyclerDevice.setAdapter(deviceAdapter);

        postAdapter = new AttentModuleAdapter(context, postList);
        postAdapter.setOnWhichClickListener(new OnWhichClickPost());
        recyclerPost.setAdapter(postAdapter);

        plantAdapter = new AttentModuleAdapter(context, plantList);
        plantAdapter.setOnWhichClickListener(new OnWhichClickPlant());
        recyclerPlant.setAdapter(plantAdapter);
    }

    /**
     * 物流地点条目中的点击事件
     */
    private class OnWhichClickLogistic implements BaseRecyclerAdapter.OnWhichClickListener {

        @Override
        public void onWhichClick(View view, int position, int tag) {
            switch (tag) {
                case AttentModuleAdapter.TAG_DELETE:
                    deleteLogistics(position);
                    break;
                case AttentModuleAdapter.TAG_ADD:
                    Intent intent = new Intent(context, AddressSelectActivity.class);
                    intent.putExtra("code", Const.REQUEST_CODE_SELECT_LOGISTICS + "");
                    intent.putExtra("type", "1");
                    startActivityForResult(intent, Const.REQUEST_CODE_SELECT_LOGISTICS);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 找车货物条目中的点击事件
     */
    private class OnWhichClickProduct implements BaseRecyclerAdapter.OnWhichClickListener {

        @Override
        public void onWhichClick(View view, int position, int tag) {
            switch (tag) {
                case AttentModuleAdapter.TAG_DELETE:
                    deleteProduct(position);
                    break;
                case AttentModuleAdapter.TAG_ADD:
                    Intent intent = new Intent(context, ProductSelectActivity.class);
                    intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_ORDER);
                    intent.putExtra(Const.KEY_IS_MULTI_MODE, true);
                    intent.putExtra(Const.KEY_ACTION, Const.ACTION_ATTENT_PRODUCT_MANAGE);
                    startActivityForResult(intent, Const.REQUEST_CODE_SELECT_PRODUCT);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 出售的设备条目中的点击事件
     */
    private class OnWhichClickDevice implements BaseRecyclerAdapter.OnWhichClickListener {

        @Override
        public void onWhichClick(View view, int position, int tag) {
            switch (tag) {
                case AttentModuleAdapter.TAG_DELETE:
                    deleteDevice(position);
                    break;
                case AttentModuleAdapter.TAG_ADD:
                    Intent intent = new Intent(context, SelectActivity.class);
                    intent.putExtra("title", "设备名称");
                    intent.putExtra("type", "1");
                    startActivityForResult(intent, Const.REQUEST_CODE_SELECT_DEVICE);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 出租厂房条目中的点击事件
     */
    private class OnWhichClickPlant implements BaseRecyclerAdapter.OnWhichClickListener {

        @Override
        public void onWhichClick(View view, int position, int tag) {
            switch (tag) {
                case AttentModuleAdapter.TAG_DELETE:
                    deletePlant(position);
                    break;
                case AttentModuleAdapter.TAG_ADD:
                    Intent intent = new Intent(context, AddressSelectCityActivity.class);
                    intent.putExtra("id", "208");
                    intent.putExtra("type", "2");
                    intent.putExtra("code", Const.REQUEST_CODE_SELECT_PLANT + "");
                    startActivityForResult(intent, Const.REQUEST_CODE_SELECT_PLANT);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 招聘职位条目中的点击事件
     */
    private class OnWhichClickPost implements BaseRecyclerAdapter.OnWhichClickListener {

        @Override
        public void onWhichClick(View view, int position, int tag) {
            switch (tag) {
                case AttentModuleAdapter.TAG_DELETE:
                    deletePost(position);
                    break;
                case AttentModuleAdapter.TAG_ADD:
                    Intent intent = new Intent(context, SelectActivity.class);
                    intent.putExtra("title", "招聘职位");
                    intent.putExtra("type", "2");
                    startActivityForResult(intent, Const.REQUEST_CODE_SELECT_POST);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 获取关注的版块数据
     */
    private void getAttentData() {
        RequestParams params = MyHttpClient.getApiParam("service", "collectionSection_list");
        params.put("uid", getUserID());
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        logisticsList.addAll(GsonUtil.getEntityList(dataObj.getJSONArray("area").toString(), AttentModule.class));
                        productList.addAll(GsonUtil.getEntityList(dataObj.getJSONArray("car").toString(), AttentModule.class));
                        deviceList.addAll(GsonUtil.getEntityList(dataObj.getJSONArray("equipment").toString(), AttentModule.class));
                        plantList.addAll(GsonUtil.getEntityList(dataObj.getJSONArray("workshop").toString(), AttentModule.class));
                        postList.addAll(GsonUtil.getEntityList(dataObj.getJSONArray("job").toString(), AttentModule.class));

                        logisticsAdapter.notifyDataSetChanged();
                        productAdapter.notifyDataSetChanged();
                        deviceAdapter.notifyDataSetChanged();
                        plantAdapter.notifyDataSetChanged();
                        postAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 删除物流地点
     */
    private void deleteLogistics(final int position) {
        RequestParams params = MyHttpClient.getApiParam("collection", "deleteArea");
        params.put("uid", getUserID());
        params.put("area_id", logisticsList.get(position).getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        logisticsList.remove(position);
                        logisticsAdapter.notifyItemRemoved(position);
                        logisticsAdapter.notifyItemRangeChanged(position, logisticsList.size() - position);
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 删除找车的货物
     */
    private void deleteProduct(final int position) {
        RequestParams params = MyHttpClient.getApiParam("collection", "deleteCar");
        params.put("uid", getUserID());
        params.put("car_id", productList.get(position).getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        productList.remove(position);
                        productAdapter.notifyItemRemoved(position);
                        productAdapter.notifyItemRangeChanged(position, productList.size() - position);
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 删除出售的设备
     */
    private void deleteDevice(final int position) {
        RequestParams params = MyHttpClient.getApiParam("collection", "deleteEquipment");
        params.put("uid", getUserID());
        params.put("equipment_id", deviceList.get(position).getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        deviceList.remove(position);
                        deviceAdapter.notifyItemRemoved(position);
                        deviceAdapter.notifyItemRangeChanged(position, deviceList.size() - position);
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 删除出租厂房的地点
     */
    private void deletePlant(final int position) {
        RequestParams params = MyHttpClient.getApiParam("collection", "deleteWorkshop");
        params.put("uid", getUserID());
        params.put("workshop_id", plantList.get(position).getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        plantList.remove(position);
                        plantAdapter.notifyItemRemoved(position);
                        plantAdapter.notifyItemRangeChanged(position, plantList.size() - position);
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 删除招聘职位
     */
    private void deletePost(final int position) {
        RequestParams params = MyHttpClient.getApiParam("collection", "deleteJob");
        params.put("uid", getUserID());
        params.put("job_id", postList.get(position).getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        postList.remove(position);
                        postAdapter.notifyItemRemoved(position);
                        postAdapter.notifyItemRangeChanged(position, postList.size() - position);
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 添加物流地点
     */
    private void addLogistics(final AttentModule module) {
        RequestParams params = MyHttpClient.getApiParam("collection", "addArea");
        params.put("uid", getUserID());
        params.put("area_id", module.getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        logisticsList.add(module);
                        logisticsAdapter.notifyDataSetChanged();
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 添加找车的货物
     */
    private void addProduct(final List<ServiceProduct> list) {
        final List<AttentModule> moduleList = new ArrayList<>();
        for (ServiceProduct product : list) {
            AttentModule module = new AttentModule();
            module.setId(product.getId());
            module.setName(product.getName());
            moduleList.add(module);
        }

        String productIds = "";
        for (AttentModule module : moduleList) {
            productIds += module.getId() + ",";
        }

        RequestParams params = MyHttpClient.getApiParam("collection", "addCar");
        params.put("uid", getUserID());
        params.put("product_id", productIds);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        productList.clear();
                        productList.addAll(moduleList);
                        productAdapter.notifyDataSetChanged();
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 添加出售的设备
     */
    private void addDevice(final AttentModule module) {
        RequestParams params = MyHttpClient.getApiParam("collection", "addEquipment");
        params.put("uid", getUserID());
        params.put("equipment_id", module.getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        deviceList.add(module);
                        deviceAdapter.notifyDataSetChanged();
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }

    /**
     * 添加出租厂房的地点
     */
    private void addPlant(final AttentModule module) {
        RequestParams params = MyHttpClient.getApiParam("collection", "addWorkshop");
        params.put("uid", getUserID());
        params.put("workshop_id", module.getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        plantList.add(module);
                        plantAdapter.notifyDataSetChanged();
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 添加招聘职位
     */
    private void addPost(final AttentModule module) {
        RequestParams params = MyHttpClient.getApiParam("collection", "addJob");
        params.put("uid", getUserID());
        params.put("job_id", module.getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        postList.add(module);
                        postAdapter.notifyDataSetChanged();
                        activity.getAttentNum();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (true) {

            AttentModule module1 = new AttentModule();
            module1.setId("3774");
            module1.setName("深州");
            addPlant(module1);
            return;
        }*/

        if (data == null)
            return;

        AttentModule module = new AttentModule();
        switch (requestCode) {
            case Const.REQUEST_CODE_SELECT_LOGISTICS:
                module.setId(data.getStringExtra("id"));
                module.setName(data.getStringExtra("name"));
                addLogistics(module);
                break;
            case Const.REQUEST_CODE_SELECT_PRODUCT:
                List<ServiceProduct> list = (List<ServiceProduct>) data.getSerializableExtra("list");
                addProduct(list);
                break;
            case Const.REQUEST_CODE_SELECT_DEVICE:
                module.setId(data.getStringExtra("id"));
                module.setName(data.getStringExtra("name"));
                addDevice(module);
                break;
            case Const.REQUEST_CODE_SELECT_PLANT:
                module.setId(data.getStringExtra("id"));
                module.setName(data.getStringExtra("name"));
                addPlant(module);
                break;
            case Const.REQUEST_CODE_SELECT_POST:
                module.setId(data.getStringExtra("id"));
                module.setName(data.getStringExtra("name"));
                addPost(module);
                break;
            default:
                break;
        }
    }


    @OnClick({R.id.txtLogisticsManage, R.id.txtProductManage, R.id.txtDeviceManage, R.id.txtPostManage, R.id.txtPlantManage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtLogisticsManage: //物流地点管理
                onManageClick(logisticsAdapter, txtLogisticsManage);
                break;
            case R.id.txtProductManage: //找车的货物管理
                onManageClick(productAdapter, txtProductManage);
                break;
            case R.id.txtDeviceManage: //出售设备管理
                onManageClick(deviceAdapter, txtDeviceManage);
                break;
            case R.id.txtPlantManage: //出租厂房地点管理
                onManageClick(plantAdapter, txtPlantManage);
                break;
            case R.id.txtPostManage: //招聘职位管理
                onManageClick(postAdapter, txtPostManage);
                break;
            default:
                break;
        }
    }


    private void onManageClick(AttentModuleAdapter adapter, TextView txtView) {
        if (adapter.isEditMode()) {
            adapter.setEditMode(false);
            txtView.setText("管理");
            txtView.setTextColor(0xff72bb38);
        } else {
            adapter.setEditMode(true);
            txtView.setText("完成");
            txtView.setTextColor(0xffff6000);
        }
    }

}
