package com.mapchat.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.chat.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by lzw on 14-9-17.发现
 */
public class DiscoverFragment extends BaseFragment {

  String currentUserID = AVUser.getCurrentUser().getObjectId();
  private AVIMConversation conversation;//会话

  //定位相关
  public LocationClient mLocationClient = null;//定位
  private MyLocationListener myListener = new MyLocationListener();//位置监听
  boolean isFirstLoc = true; // 是否首次定位


  //地图层相关
  private MapView mMapView ;//view层
  private BaiduMap mBaiduMap;//地图数据层
  public  BitmapDescriptor mCurrentMarker;//声明定位图标marker
  private MyLocationConfiguration.LocationMode mCurrentMode; //声明地图层定位交互方式，普通，跟随，罗盘

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    SDKInitializer.initialize(getActivity().getApplicationContext());//地图SDK初始化
    View view = inflater.inflate(R.layout.discover_fragment, container, false);
    mMapView = (MapView) view.findViewById(R.id.bmapView);
    ButterKnife.bind(this, view);

    // 地图SDK 相关
    mBaiduMap = mMapView.getMap();//获取地图数据加载到地图view层
    mBaiduMap.setMyLocationEnabled(true);//开启定位图层
    mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;//地图层定位模式，普通，跟随，罗盘式
    mCurrentMarker = BitmapDescriptorFactory .fromResource(R.drawable.earth32pxw);//自定义marker图标
    MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
    mBaiduMap.setMyLocationConfiguration(config);//设置自定义定位模式，定位图标

    //定位SDK相关
    mLocationClient = new LocationClient(getActivity().getApplicationContext());//第一次初始化定位如果不能定位重置为appcontext
    mLocationClient.registerLocationListener(myListener);//注册定位客户端
    LocationClientOption option = new LocationClientOption();//定位客户端可选项
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位模式
    option.setCoorType("bd09ll");//设置百度坐标系
    option.setScanSpan(100000);//每发起一次定位数据的时间间隔1秒
    option.setOpenGps(true);//打开GPS数据
    option.setLocationNotify(true);//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果
    option.setIgnoreKillProcess(true);//可选，定位SDK内部是一个service，并放到了独立进程。
    mLocationClient.setLocOption(option);//mLocationClient为第二步初始化过的LocationClient对象
    mLocationClient.start();//启动定位终端
//
    //在地图上用marker显示所有的好友位置1获取ID 2获取位置 3绘制marker
    AVQuery<AVUser> query = AVUser.followeeQuery(currentUserID, AVUser.class);
    query.include("followee");//follee的所有属性
    query.findInBackground(new FindCallback<AVUser>() {
      @Override
      public void done(List<AVUser> list, AVException e) {
        for(final AVUser user : list){
           final String whoId = user.getObjectId();//获取这一次循环对象的ID
          final AVFile avatar = user.getAVFile("avatar");
          Log.d(TAG, "donenenene: "+avatar);//打印出这次循环对象的头像
          final  AVGeoPoint wholoc = user.getAVGeoPoint("location");
          final double wholat = wholoc.getLatitude();
          final double wholon = wholoc.getLongitude();
          final LatLng point = new LatLng(wholat, wholon);
          OverlayOptions option =  new MarkerOptions()
                  .position(point).icon(mCurrentMarker);
          mBaiduMap.addOverlay(option);//在地图上添加这次ID的marker
          //第三步绘制这次ID的marker
          mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {//设置marker点击监听
            @Override
            public boolean onMarkerClick(Marker marker) {
              LatLng p = marker.getPosition();//得到地图点击点的经纬度
              String userId = null;
              DecimalFormat df = new DecimalFormat("0.00000");//java格式化浮点数工具
              if (df.format(p.latitude).equals(df.format(wholat))&&
                      df.format(p.longitude).equals(df.format(wholon))) {
                userId = user.getObjectId();//如果点击的点和用户的经纬度一样
              }
              if (userId!=null)//如果USERID不为空则打开对话框
                LCChatKit.getInstance().open(currentUserID, new AVIMClientCallback() {
                  @Override
                  public void done(AVIMClient avimClient, AVIMException e) {
                    if (null == e) {
                      Intent intent = new Intent(getContext(), LCIMConversationActivity.class);
                      intent.putExtra(LCIMConstants.PEER_ID, whoId);
                      startActivity(intent);
                    }
                  }
                });
              return false;
            }
          });
          //点击事件结束
        }
      }
    });

    return view;
  }

  //定位SDK监听函数,从baidu的SDK获取定位数据
  public class MyLocationListener extends BDAbstractLocationListener {

    @Override
    public void onReceiveLocation(BDLocation location){//接收baidu定位SDK发来的位置数据
      // 构造定位精度，方向，经纬度构成的对象
      MyLocationData locData = new MyLocationData.Builder()
              .accuracy(location.getRadius())//定位数据精度信息 10米100米精度
              .direction(100)//定位数据的方向信息，例如0-360？？？
              .latitude(location.getLatitude()).longitude(location.getLongitude())//定位的经纬度
              .build();//构建生成定位数据对象
      mBaiduMap.setMyLocationData(locData);// 给地图层绑定定位数据对象
      //是否是第一次定位，第一次定位需要移动地图
      if (isFirstLoc) {
        isFirstLoc = false;
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
      }
      //上传我的位置
      Log.d(TAG, "doneeeee: "+location.getLatitude());
      AVGeoPoint point = new AVGeoPoint(location.getLatitude(),location.getLongitude());//封装位置数据
      final AVUser user = AVUser.getCurrentUser();//获取当前账户ID
      user.put("location",point);//给当前用户ID上传位置数据
      user.saveInBackground();//异步执行上传位置数据
    }
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
    //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    mMapView.onDestroy();
    mLocationClient.stop();
    Log.d(TAG, "donenenennnnnn: ");
  }
  @Override
  public void onResume() {
    super.onResume();
    //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
    mMapView.onResume();
  }
  @Override
  public void onPause() {
    super.onPause();
    //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
    mMapView.onPause();
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    headerLayout.showTitle("发现");
  }


}
































