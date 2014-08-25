package com.lenovo.market.activity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.lenovo.market.R;
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;

/**
 * 定位地理信息
 * 
 * @author muqiang
 * 
 */
public class BasicMapActivity extends BaseActivity implements LocationSource, AMapLocationListener, OnClickListener {

    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private int from;
    private String label;
    private double mLatitude;// 纬度
    private double mLongitude;// 经度

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_basicmap);
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(paramBundle);// 此方法必须重写
        from = getIntent().getIntExtra("from", 0);
        init();
    }

    @Override
    protected void findViewById() {
        setTitleBarRightBtnText(R.string.title_send);
        setTitleBarText("位置");
        setTitleBarLeftBtnText();
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_right_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:// 返回
            finish();
            break;
        case R.id.btn_right:
            if (TextUtils.isEmpty(label)) {
                Utils.showToast(context, "还在定位中...请稍后发送您的位置！");
            } else {
                sendMapMsg();
            }
            break;
        }
    }

    private void sendMapMsg() {
        Message message = null;
        switch (from) {
        case 1:
            message = ChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_ELEVEN);
            break;
        case 2:
            message = GroupChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_ELEVEN);
            break;
        case 3:
            message = HomePageFragment.handler.obtainMessage(MarketApp.HANDLERMESS_ELEVEN);
            break;
        case 4:
            message = PublicChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_ELEVEN);
            break;
        }
        if (message == null) {
            log.e("请设置from参数");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("Location_X", mLatitude + "");
        bundle.putString("Location_Y", mLongitude + "");
        bundle.putString("Label", label);
        message.setData(bundle);
        message.sendToTarget();
        finish();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        // myLocationStyle.radiusFillColor(color)//设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationRotateAngle(180);
        aMap.setLocationSource(this);// 设置定位监听
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        uiSettings.setCompassEnabled(true);// 设置指南针可用
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
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
        deactivate();
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
     * 此方法已经废弃
     */
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            float bearing = aMap.getCameraPosition().bearing;
            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
            label = (String) aLocation.getExtras().get("desc");
            mLatitude = aLocation.getLatitude();
            mLongitude = aLocation.getLongitude();
            Toast.makeText(context, label, Toast.LENGTH_LONG).show();
            deactivate();
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            /*
             * mAMapLocManager.setGpsEnable(false); 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location API定位采用GPS和网络混合定位方式 ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
             */
            mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }
}
