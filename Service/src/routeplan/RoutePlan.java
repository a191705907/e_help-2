package routeplan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import client.ui.R;
import client.ui.SendHelpMsgActivity;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.CommonParams.NL_Net_Mode;
import com.baidu.navisdk.CommonParams.Const.ModelName;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.mapcontrol.MapParams.Const.LayerMode;
import com.baidu.navisdk.comapi.routeguide.RouteGuideParams.RGLocationMode;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.routeplan.IRouteResultObserver;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.comapi.setting.SettingParams;
import com.baidu.navisdk.model.NaviDataEngine;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.navisdk.model.datastruct.RoutePlanNode;
import com.baidu.navisdk.ui.routeguide.BNavConfig;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.util.common.PreferenceHelper;
import com.baidu.navisdk.util.common.ScreenUtil;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;

public class RoutePlan extends Activity {
	private RoutePlanModel mRoutePlanModel = null;
	private MapGLSurfaceView mMapView = null;
	private Location lo;// ��λ
	private Bundle bundle;
	
	double sla;
	double slo;
	double ela;
	double elo;

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_routeplan);

		// ��ȡ��ǰ��λ
		lo = new Location(RoutePlan.this);

		bundle = this.getIntent().getExtras();

		ela = bundle.getDouble("latitude");
		elo = bundle.getDouble("longitude");
		
		findViewById(R.id.online_calc_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startCalcRoute(NL_Net_Mode.NL_Net_Mode_OnLine);
					}
				});

		findViewById(R.id.simulate_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startNavi(false);
					}
				});

		findViewById(R.id.real_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PreferenceHelper.getInstance(getApplicationContext())
						.putBoolean(SettingParams.Key.SP_TRACK_LOCATE_GUIDE,
								false);
				startNavi(true);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onPause() {
		super.onPause();
		BNRoutePlaner.getInstance().setRouteResultObserver(null);
		((ViewGroup) (findViewById(R.id.mapview_layout))).removeAllViews();
		BNMapController.getInstance().onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		initMapView();
		((ViewGroup) (findViewById(R.id.mapview_layout))).addView(mMapView);
		BNMapController.getInstance().onResume();
	}

	private void initMapView() {
		if (Build.VERSION.SDK_INT < 14) {
			BaiduNaviManager.getInstance().destroyNMapView();
		}

		mMapView = BaiduNaviManager.getInstance().createNMapView(this);
		BNMapController.getInstance().setLevel(14);
		BNMapController.getInstance().setLayerMode(
				LayerMode.MAP_LAYER_MODE_INVALID);
		updateCompassPosition();

		BNMapController.getInstance().locateWithAnimation(
				(int) (elo * 1e5),
				(int) (ela * 1e5));
	}

	/**
	 * ����ָ����λ��
	 */
	private void updateCompassPosition() {
		int screenW = this.getResources().getDisplayMetrics().widthPixels;
		BNMapController.getInstance().resetCompassPosition(
				screenW - ScreenUtil.dip2px(this, 30),
				ScreenUtil.dip2px(this, 126), -1);
	}

	private void startCalcRoute(int netmode) {

//		sla = bd09_To_Gcj02_la(lo.GetLatitude(),lo.Getlongtitude());
//		slo = bd09_To_Gcj02_lo(lo.GetLatitude(),lo.Getlongtitude());
		sla = lo.GetLatitude();
		slo = lo.Getlongtitude();
		
		// ���
		 RoutePlanNode startNode = new RoutePlanNode((int) (sla*1e5),
		 (int) (slo*1e5), RoutePlanNode.FROM_MY_POSITION, "���",
		 "���");
		// �յ�
		 RoutePlanNode endNode = new RoutePlanNode(
		 (int) (ela*1e5),
		 (int) (elo*1e5),
		 RoutePlanNode.FROM_MAP_POINT, "�յ�", "�յ�");
//		RoutePlanNode startNode = new RoutePlanNode((int) 2313737,
//				(int) 11335412, RoutePlanNode.FROM_MY_POSITION, "���", "���");
//		RoutePlanNode endNode = new RoutePlanNode((int) 2310319,
//				(int) 11330435, RoutePlanNode.FROM_MAP_POINT, "�յ�", "�յ�");

		// �����յ���ӵ�nodeList

		Log.i("location",
				(int) (lo.GetLatitude() * 1e5) + "////"
						+ (int) (lo.Getlongtitude() * 1e5) + "////"
						+ (int) (bundle.getDouble("latitude") * 1e5) + "////"
						+ (int) (bundle.getDouble("longitude") * 1e5));

		ArrayList<RoutePlanNode> nodeList = new ArrayList<RoutePlanNode>(2);
		nodeList.add(startNode);
		nodeList.add(endNode);
		BNRoutePlaner.getInstance().setObserver(
				new RoutePlanObserver(this, null));
		// ������·��ʽ
		BNRoutePlaner.getInstance().setCalcMode(
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME);
		// ������·����ص�
		BNRoutePlaner.getInstance()
				.setRouteResultObserver(mRouteResultObserver);
		// �������յ㲢��·
		boolean ret = BNRoutePlaner.getInstance().setPointsToCalcRoute(
				nodeList, NL_Net_Mode.NL_Net_Mode_OnLine);
		if (!ret) {
			Toast.makeText(this, "�滮ʧ��", Toast.LENGTH_SHORT).show();
		}
	}
    
    private LatLng gtob(LatLng sourceLatLng){
    	// ��google��ͼ��soso��ͼ��aliyun��ͼ��mapabc��ͼ��amap��ͼ// ��������ת���ɰٶ�����  
    	CoordinateConverter converter  = new CoordinateConverter();  
    	converter.from(CoordType.COMMON);  
    	// sourceLatLng��ת������  
    	converter.coord(sourceLatLng);  
    	LatLng desLatLng = converter.convert(); 
    	return desLatLng;
    }
    

	private void startNavi(boolean isReal) {
		if (mRoutePlanModel == null) {
			Toast.makeText(this, "������·��", Toast.LENGTH_LONG).show();
			return;
		}
		// ��ȡ·�߹滮������
		RoutePlanNode startNode = mRoutePlanModel.getStartNode();
		// ��ȡ·�߹滮����յ�
		RoutePlanNode endNode = mRoutePlanModel.getEndNode();
		if (null == startNode || null == endNode) {
			return;
		}
		// ��ȡ·�߹滮��·ģʽ
		int calcMode = BNRoutePlaner.getInstance().getCalcMode();
		Bundle bundle = new Bundle();
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_VIEW_MODE,
				BNavigator.CONFIG_VIEW_MODE_INFLATE_MAP);
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_DONE,
				BNavigator.CONFIG_CLACROUTE_DONE);
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_X,
				startNode.getLongitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_Y,
				startNode.getLatitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_X, endNode.getLongitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_Y, endNode.getLatitudeE6());
		bundle.putString(BNavConfig.KEY_ROUTEGUIDE_START_NAME,
				mRoutePlanModel.getStartName(this, false));
		bundle.putString(BNavConfig.KEY_ROUTEGUIDE_END_NAME,
				mRoutePlanModel.getEndName(this, false));
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_MODE, calcMode);
		if (!isReal) {
			// ģ�⵼��
			bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,
					RGLocationMode.NE_Locate_Mode_RouteDemoGPS);
		} else {
			// GPS ����
			bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,
					RGLocationMode.NE_Locate_Mode_GPS);
		}

		Intent intent = new Intent(RoutePlan.this, BNavigatorActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private IRouteResultObserver mRouteResultObserver = new IRouteResultObserver() {

		@Override
		public void onRoutePlanYawingSuccess() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRoutePlanYawingFail() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRoutePlanSuccess() {
			// TODO Auto-generated method stub
			BNMapController.getInstance().setLayerMode(
					LayerMode.MAP_LAYER_MODE_ROUTE_DETAIL);
			mRoutePlanModel = (RoutePlanModel) NaviDataEngine.getInstance()
					.getModel(ModelName.ROUTE_PLAN);
		}

		@Override
		public void onRoutePlanFail() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRoutePlanCanceled() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRoutePlanStart() {
			// TODO Auto-generated method stub

		}

	};
}
