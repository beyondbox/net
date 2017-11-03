package com.appjumper.silkscreen.ui.common;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-12-14.
 */
public class MapviewActivity extends BaseActivity {
    @Bind(R.id.map)
    MapView mMapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        initBack();
        ButterKnife.bind(this);
        initTitle("公司地址");

        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        String lng = intent.getStringExtra("lng");
        String lat = intent.getStringExtra("lat");
        String name = intent.getStringExtra("name");

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);

        //初始化地图变量
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.getUiSettings().setCompassEnabled(true);
        }

        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        final Marker marker = aMap.addMarker(new MarkerOptions().
                position(latLng).
                title(name).
                snippet(address));
        marker.showInfoWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

}
