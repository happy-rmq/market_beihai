package com.lenovo.market.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.DriveRouteResult;
import com.lenovo.market.R;
import com.lenovo.market.util.AMapUtil;
import com.lenovo.market.util.Utils;

/**
 * 查看地理位置信息
 * 
 * @author muqiang
 * 
 */
public class QueryMapActivity extends BaseActivity implements OnGeocodeSearchListener, OnClickListener, InfoWindowAdapter, OnInfoWindowClickListener, OnMarkerClickListener {

    private ProgressDialog progDialog;
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private AMap aMap;
    private MapView mapView;
    private LatLonPoint latLonPoint;
    private Marker regeoMarker;
    private double location_x;
    private double location_y;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_querymap);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mapView = (MapView) findViewById(R.id.querymap_map);
        mapView.onCreate(bundle);// 此方法必须重写
        location_x = Double.parseDouble(getIntent().getStringExtra("Location_X"));
        location_y = Double.parseDouble(getIntent().getStringExtra("Location_Y"));
        latLonPoint = new LatLonPoint(location_x, location_y);
        init();
        getAddress(latLonPoint);
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void setListener() {

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            setUpMap();
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);
    }

    private void setUpMap() {
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.setCancelable(false);
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress();
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(latLonPoint), 17));
                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
                drawMarkers(addressName);// 添加10个带有系统默认icon的marker
                // Utils.showToast(context, addressName);
            } else {
                Utils.showToast(context, R.string.no_result);
            }
        } else if (rCode == 27) {
            Utils.showToast(context, R.string.error_network);
        } else if (rCode == 32) {
            Utils.showToast(context, R.string.error_key);
        } else {
            Utils.showToast(context, R.string.error_other);
        }
    }

    /**
     * 绘制系统默认的1种marker背景图片
     */
    public void drawMarkers(String addressName) {
        regeoMarker.setTitle(addressName);
        regeoMarker.showInfoWindow();// 设置默认显示一个infowinfow
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.layout_info_window, null);

        TextView titleUi = (TextView) view.findViewById(R.id.title);
        titleUi.setTextSize(15);
        titleUi.setText(addressName);

        Button snippetUi = (Button) view.findViewById(R.id.snippet);
        snippetUi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra("end", addressName);
                intent.putExtra("Location_X", location_x);
                intent.putExtra("Location_Y", location_y);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub
        return false;
    }
}
