package com.lenovo.market.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.BusRouteOverlay;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.lenovo.market.R;
import com.lenovo.market.util.AMapUtil;
import com.lenovo.market.util.Utils;

/**
 * AMapV2地图中简单介绍route搜索
 */
public class RouteActivity extends BaseActivity implements OnMarkerClickListener, OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnPoiSearchListener, OnRouteSearchListener, OnClickListener, LocationSource, AMapLocationListener {

    private AMap aMap;
    private MapView mapView;
    private Button drivingButton;
    private Button busButton;
    private Button walkButton;// route planning

    private ImageButton startImageButton;
    private ImageButton endImageButton;
    private ImageButton routeSearchImagebtn;

    private EditText startTextView;
    private EditText endTextView;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private int busMode = RouteSearch.BusDefault;// 公交默认模式
    private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
    private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
    private BusRouteResult busRouteResult;// 公交模式查询结果
    private DriveRouteResult driveRouteResult;// 驾车模式查询结果
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private int routeType = 1;// 1代表公交模式，2代表驾车模式，3代表步行模式
    private String strStart;
    private String strEnd;
    private LatLonPoint startPoint = null;
    private LatLonPoint endPoint = null;
    private PoiSearch.Query startSearchQuery;
    private PoiSearch.Query endSearchQuery;

    private boolean isClickStart = false;
    private boolean isClickTarget = false;
    private Marker startMk, targetMk;
    private RouteSearch routeSearch;
    public ArrayAdapter<String> aAdapter;

    private String end;// 终点
    private double location_x;
    private double location_y;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void setContentView() {
        setContentView(R.layout.route_activity);
        end = getIntent().getStringExtra("end");
        location_x = getIntent().getDoubleExtra("Location_X", 0);
        location_y = getIntent().getDoubleExtra("Location_Y", 0);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(bundle);// 此方法必须重写
        init();
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
            registerListener();
        }
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        startTextView = (EditText) findViewById(R.id.autotextview_roadsearch_start);
        startTextView.setText("当前位置");
        endTextView = (EditText) findViewById(R.id.autotextview_roadsearch_goals);
        endTextView.setText(end);
        busButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_transit);
        busButton.setOnClickListener(this);
        drivingButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_driving);
        drivingButton.setOnClickListener(this);
        walkButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_walk);
        walkButton.setOnClickListener(this);
        startImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_startoption);
        startImageButton.setOnClickListener(this);
        endImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_endoption);
        endImageButton.setOnClickListener(this);
        routeSearchImagebtn = (ImageButton) findViewById(R.id.imagebtn_roadsearch_search);
        routeSearchImagebtn.setOnClickListener(this);
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
     * 选择公交模式
     */
    private void busRoute() {
        routeType = 1;// 标识为公交模式
        busMode = RouteSearch.BusDefault;
        drivingButton.setBackgroundResource(R.drawable.mode_driving_off);
        busButton.setBackgroundResource(R.drawable.mode_transit_on);
        walkButton.setBackgroundResource(R.drawable.mode_walk_off);
    }

    /**
     * 选择驾车模式
     */
    private void drivingRoute() {
        routeType = 2;// 标识为驾车模式
        drivingMode = RouteSearch.DrivingSaveMoney;
        drivingButton.setBackgroundResource(R.drawable.mode_driving_on);
        busButton.setBackgroundResource(R.drawable.mode_transit_off);
        walkButton.setBackgroundResource(R.drawable.mode_walk_off);
    }

    /**
     * 选择步行模式
     */
    private void walkRoute() {
        routeType = 3;// 标识为步行模式
        walkMode = RouteSearch.WalkMultipath;
        drivingButton.setBackgroundResource(R.drawable.mode_driving_off);
        busButton.setBackgroundResource(R.drawable.mode_transit_off);
        walkButton.setBackgroundResource(R.drawable.mode_walk_on);
    }

    /**
     * 在地图上选取起点
     */
    private void startImagePoint() {
        Utils.showToast(this, "在地图上点击您的起点");
        isClickStart = true;
        isClickTarget = false;
        registerListener();
    }

    /**
     * 在地图上选取终点
     */
    private void endImagePoint() {
        Utils.showToast(this, "在地图上点击您的终点");
        isClickTarget = true;
        isClickStart = false;
        registerListener();
    }

    /**
     * 点击搜索按钮开始Route搜索
     */
    public void searchRoute() {
        // strStart = startTextView.getText().toString().trim();
        // strEnd = endTextView.getText().toString().trim();
        // if (strStart == null || strStart.length() == 0) {
        // Utils.showToast(this, "请选择起点");
        // return;
        // }
        // if (strEnd == null || strEnd.length() == 0) {
        // Utils.showToast(this, "请选择终点");
        // return;
        // }
        // if (strStart.equals(strEnd)) {
        // Utils.showToast(this, "起点与终点距离很近，您可以步行前往");
        // return;
        // }
        startSearchResult();// 开始搜终点
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        isClickStart = false;
        isClickTarget = false;
        if (startMk.equals(marker)) {
            startTextView.setText("地图上的起点");
            startPoint = AMapUtil.convertToLatLonPoint(startMk.getPosition());
            startMk.hideInfoWindow();
            startMk.remove();
        } else if (targetMk.equals(marker)) {
            endTextView.setText("地图上的终点");
            endPoint = AMapUtil.convertToLatLonPoint(targetMk.getPosition());
            targetMk.hideInfoWindow();
            targetMk.remove();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latng) {
        if (isClickStart) {
            startMk = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromResource(R.drawable.point)).position(latng).title("点击选择为起点"));
            startMk.showInfoWindow();
        } else if (isClickTarget) {
            targetMk = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromResource(R.drawable.point)).position(latng).title("点击选择为目的地"));
            targetMk.showInfoWindow();
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(RouteActivity.this);
        aMap.setOnMarkerClickListener(RouteActivity.this);
        aMap.setOnInfoWindowClickListener(RouteActivity.this);
        aMap.setInfoWindowAdapter(RouteActivity.this);
        // aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
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
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 查询路径规划起点
     */
    public void startSearchResult() {
        if (startPoint != null && strStart.equals("地图上的起点")) {
            endSearchResult();
        } else {
            showProgressDialog();
            startSearchQuery = new PoiSearch.Query(strStart, "", "010"); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
            startSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
            startSearchQuery.setPageSize(20);// 设置每页返回多少条数据
            PoiSearch poiSearch = new PoiSearch(RouteActivity.this, startSearchQuery);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();// 异步poi查询
        }
    }

    /**
     * 查询路径规划终点
     */
    public void endSearchResult() {
        strEnd = endTextView.getText().toString().trim();
        if (endPoint != null && strEnd.equals("地图上的终点")) {
            searchRouteResult(startPoint, endPoint);
        } else {
            showProgressDialog();
            endSearchQuery = new PoiSearch.Query(strEnd, "", "010"); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
            endSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
            endSearchQuery.setPageSize(20);// 设置每页返回多少条数据

            PoiSearch poiSearch = new PoiSearch(RouteActivity.this, endSearchQuery);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn(); // 异步poi查询
        }
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        if (routeType == 1) {// 公交路径规划
            BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, "北京", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == 2) {// 驾车路径规划
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == 3) {// 步行路径规划
            WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
            routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {

    }

    /**
     * POI搜索结果回调
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {// 返回成功
            if (result != null && result.getQuery() != null && result.getPois() != null && result.getPois().size() > 0) {// 搜索poi的结果
                if (result.getQuery().equals(startSearchQuery)) {
                    startPoint = new LatLonPoint(mLatitude, mLongitude);
                    endSearchResult();// 开始搜终点
                    // List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
                    // RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(RouteActivity.this, poiItems);
                    // dialog.setTitle("您要找的起点是:");
                    // dialog.show();
                    // dialog.setOnListClickListener(new OnListItemClick() {
                    // @Override
                    // public void onListItemClick(RouteSearchPoiDialog dialog, PoiItem startpoiItem) {
                    // startPoint = startpoiItem.getLatLonPoint();
                    // strStart = startpoiItem.getTitle();
                    // startTextView.setText(strStart);
                    // endSearchResult();// 开始搜终点
                    // }
                    //
                    // });
                } else if (result.getQuery().equals(endSearchQuery)) {
                    endPoint = new LatLonPoint(location_x, location_y);
                    searchRouteResult(startPoint, endPoint);// 进行路径规划搜索
                    // List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
                    // RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(RouteActivity.this, poiItems);
                    // dialog.setTitle("您要找的终点是:");
                    // dialog.show();
                    // dialog.setOnListClickListener(new OnListItemClick() {
                    // @Override
                    // public void onListItemClick(RouteSearchPoiDialog dialog, PoiItem endpoiItem) {
                    // // endPoint = endpoiItem.getLatLonPoint();
                    // endPoint = new LatLonPoint(location_x, location_y);
                    // // strEnd = endpoiItem.getTitle();
                    // // endTextView.setText(strEnd);
                    // // endPoint = new LatLonPoint(location_x, location_y);
                    // // startPoint = new LatLonPoint(location_x + 30, location_y + 50);
                    // searchRouteResult(startPoint, endPoint);// 进行路径规划搜索
                    // }
                    //
                    // });
                }
            } else {
                Utils.showToast(this, R.string.no_result);
            }
        } else if (rCode == 27) {
            Utils.showToast(this, R.string.error_network);
        } else if (rCode == 32) {
            Utils.showToast(this, R.string.error_key);
        } else {
            Utils.showToast(this, getString(R.string.error_other) + rCode);
        }
    }

    /**
     * 公交路线查询回调
     */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                busRouteResult = result;
                BusPath busPath = busRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap, busPath, busRouteResult.getStartPos(), busRouteResult.getTargetPos());
                routeOverlay.removeFromMap();
                routeOverlay.addToMap();
                routeOverlay.zoomToSpan();
            } else {
                Utils.showToast(this, R.string.no_result);
            }
        } else if (rCode == 27) {
            Utils.showToast(this, R.string.error_network);
        } else if (rCode == 32) {
            Utils.showToast(this, R.string.error_key);
        } else {
            Utils.showToast(this, getString(R.string.error_other) + rCode);
        }
    }

    /**
     * 驾车结果回调
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                driveRouteResult = result;
                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, aMap, drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            } else {
                Utils.showToast(this, R.string.no_result);
            }
        } else if (rCode == 27) {
            Utils.showToast(this, R.string.error_network);
        } else if (rCode == 32) {
            Utils.showToast(this, R.string.error_key);
        } else {
            Utils.showToast(this, getString(R.string.error_other) + rCode);
        }
    }

    /**
     * 步行路线结果回调
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                walkRouteResult = result;
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
            } else {
                Utils.showToast(this, R.string.no_result);
            }
        } else if (rCode == 27) {
            Utils.showToast(this, R.string.error_network);
        } else if (rCode == 32) {
            Utils.showToast(this, R.string.error_key);
        } else {
            Utils.showToast(this, getString(R.string.error_other) + rCode);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.imagebtn_roadsearch_startoption:
            startImagePoint();
            break;
        case R.id.imagebtn_roadsearch_endoption:
            endImagePoint();
            break;
        case R.id.imagebtn_roadsearch_tab_transit:
            busRoute();
            break;
        case R.id.imagebtn_roadsearch_tab_driving:
            drivingRoute();
            break;
        case R.id.imagebtn_roadsearch_tab_walk:
            walkRoute();
            break;
        case R.id.imagebtn_roadsearch_search:
            searchRoute();
            break;
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

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            strStart = (String) aLocation.getExtras().get("desc");
            mLatitude = aLocation.getLatitude();
            mLongitude = aLocation.getLongitude();
            deactivate();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}
