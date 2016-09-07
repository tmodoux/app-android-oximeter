package com.example.sdk_demo;

import com.jiuan.android.sdk.am.bluetooth.lpcbt.AM3Control;
import com.jiuan.android.sdk.am.bluetooth.lpcbt.JiuanAM3Observer;
import com.jiuan.android.sdk.device.DeviceManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AM3Activity extends Activity implements JiuanAM3Observer{

	private DeviceManager deviceManager;
	private AM3Control am3Control;

	private Button btn_connect;
	private Button user_getUser, user_Reset, user_setInfo, user_getInfo, user_setUser;
	private Button sport, sleep, realdata, queryState, planemodel;
	private Button queryAlarmNum, setAlarm, queryAlarm, deleteAlarm, queryreminder, setreminder;
	private String mAddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_am3);
		
		// get control by device mac
		deviceManager = DeviceManager.getInstance();
		mAddress = getIntent().getStringExtra("mac");
		if(deviceManager.getAmDevice(mAddress) instanceof AM3Control) {
			am3Control = (AM3Control) deviceManager.getAmDevice(mAddress);
		}
		
		if(am3Control != null){
			am3Control.addObserver(this);
		}
		initView();
	}

	private void initView(){
		btn_connect = (Button)findViewById(R.id.connect_am3);
		btn_connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/**
				 * userId the identification of the user, could be the form of email address or mobile phone number
				 * (mobile phone number is not supported temporarily).
				 * clientID and clientSecret, as the identification of the SDK, 
				 * will be issued after the iHealth SDK registration. 
				 * please contact lvjincan@jiuan.com for registration.
				 */
				String userId = "";
				final String clientID = "";
				final String clientSecret = "";
				am3Control.connect(AM3Activity.this,userId,clientID,clientSecret);
			}
		});
		
		user_getUser = (Button) findViewById(R.id.scanUser_am3);
		user_setUser = (Button) findViewById(R.id.setUser_am3);
		
		user_getInfo = (Button) findViewById(R.id.scanInfoUser_am3);
		user_setInfo = (Button) findViewById(R.id.setInfoUser_am3);
		
		user_getUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.getUserID();
			}
		});
		user_setUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.setUser(am3Control.getUserId());
			}
		});
		user_getInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.getUserInfo();
			}
		});
		user_setInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.setUserInfo(25, 170, 60, 1, 1, 10000, 2);
			}
		});
		
		queryAlarmNum = (Button) findViewById(R.id.queryAlarmNum_am3);
		queryAlarm = (Button) findViewById(R.id.queryAlarm_am3);
		setAlarm = (Button) findViewById(R.id.setAlarm_am3);
		deleteAlarm = (Button) findViewById(R.id.deleteAlarm_am3);
		
		queryAlarmNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.getAlarmClockNum();
			}
		});
		queryAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.checkAlarmClock(1);
			}
		});
		setAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.setAlarmClock(1, 16, 31, true, new int[]{1, 1, 1, 1, 1, 1, 1}, true);

			}
		});
		deleteAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.deleteAlarmClock(1);
			}
		});
		
		queryreminder = (Button) findViewById(R.id.queryreminder_am3);
		setreminder = (Button) findViewById(R.id.setreminder_am3);
		
		queryreminder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.getActivityRemind();
			}
		});
		setreminder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.setActivityRemind(0,2,true);
			}
		});
		
		queryState = (Button) findViewById(R.id.queryInfo_am3);
		planemodel = (Button) findViewById(R.id.planemodel_am3);
		
		queryState.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.queryAMState();
			}
		});
		planemodel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.setFlyMode();
			}
		});
		
		realdata = (Button) findViewById(R.id.realdata_am3);
		sport = (Button) findViewById(R.id.sport_am3);
		sleep = (Button) findViewById(R.id.sleep_am3);

		realdata.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.syncRealData();
			}
		});
		sport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.syncActivityData();
//				byte[] data = new byte[]{
//						(byte)0x0E,(byte)0x08,(byte)0x1E,(byte)0x4C,(byte)0x00,(byte)0x02,
//						(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x05,
//						(byte)0x00,(byte)0x0C,(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x10		
////						(byte)0x00,(byte)0x17,(byte)0x00,(byte)0x25,(byte)0x00,(byte)0x02,
////						(byte)0x00,(byte)0x1C,(byte)0x00,(byte)0x29,(byte)0x00,(byte)0x03
////						(byte)0x17,(byte)0x38,(byte)0x00,(byte)0x35,(byte)0x00,(byte)0x06,
////						(byte)0x17,(byte)0x39,(byte)0x00,(byte)0x40,(byte)0x00,(byte)0x08
//				};
//				Method.changeActivityData2Json(AM3_Test.this,"liu01234345555@jiuan.com",123456,"9059AF276C37",data);
			}
		});
		sleep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.syncSleepData();
//				byte[] data = new byte[]{
//						(byte)0x0E,(byte)0x08,(byte)0x1D,(byte)0x10,(byte)0x15,(byte)0x2A,(byte)0x00,(byte)0x10,
//						(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x01,(byte)0x02,
//						(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x02
//				};
//				Method.changeSleepData2Json(AM3_Test.this,"liu01234345555@jiuan.com",123456,"9059AF276C37",data);
			}
		});

		user_Reset = (Button) findViewById(R.id.reset_am3);
		user_Reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3Control.reset(am3Control.getUserId());
			}
		});
	}
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				int userStatus = ((Bundle) (msg.obj)).getInt("userStatus");
				Toast.makeText(getApplicationContext(), "AM3 UserStatus=" + userStatus, Toast.LENGTH_SHORT).show();
				break;
			case 1://用户ID
				int userid = ((Bundle) (msg.obj)).getInt("userid");
				Toast.makeText(getApplicationContext(), "AM3 Userid=" + userid, Toast.LENGTH_SHORT).show();
				break;
			case 2:
				boolean result_user = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3 set User=" + result_user, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				String userInfo = ((Bundle) (msg.obj)).getString("userInfo");
				Toast.makeText(getApplicationContext(), "AM3 UserInfo=" + userInfo, Toast.LENGTH_SHORT).show();
				break;
			case 4:
				boolean result_info = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3 set userInfo=" + result_info, Toast.LENGTH_SHORT).show();
				break;
			case 5:
				String alarmId = ((Bundle) (msg.obj)).getString("alarmId");
				Toast.makeText(getApplicationContext(), "AM3 alarmId=" + alarmId, Toast.LENGTH_SHORT).show();
				break;
			case 6:
				String alarmInfo = ((Bundle) (msg.obj)).getString("alarmInfo");
				Toast.makeText(getApplicationContext(), "AM3 alarmInfo=" + alarmInfo, Toast.LENGTH_SHORT).show();
				break;
			case 7:
				boolean result_setAlarm = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3 set clock=" + result_setAlarm, Toast.LENGTH_SHORT).show();
				break;
			case 8:
				boolean result_deleteAlarm = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3 delete clock=" + result_deleteAlarm, Toast.LENGTH_SHORT).show();
				break;
			case 9:
				String remindInfo = ((Bundle) (msg.obj)).getString("remindInfo");
				Toast.makeText(getApplicationContext(), "AM3 remindInfo=" + remindInfo, Toast.LENGTH_SHORT).show();
				break;
			case 10:
				boolean result_setRemind = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3 set sportRemind=" + result_setRemind, Toast.LENGTH_SHORT).show();
				break;
			case 11:
				String stateInfo = ((Bundle) (msg.obj)).getString("stateInfo");
				Toast.makeText(getApplicationContext(), "AM3 stateInfo=" + stateInfo, Toast.LENGTH_SHORT).show();
				break;
			case 12:
				boolean result_setFlyMode = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3 set mode=" + result_setFlyMode, Toast.LENGTH_SHORT).show();
				break;
			case 13:
				String realData = ((Bundle) (msg.obj)).getString("realData");
				Toast.makeText(getApplicationContext(), "AM3 RealData=" + realData, Toast.LENGTH_SHORT).show();
				break;
			case 14:
				String activityData = ((Bundle) (msg.obj)).getString("activityData");
				Toast.makeText(getApplicationContext(), "AM3 activityData=" + activityData, Toast.LENGTH_SHORT).show();
				break;
			case 15:
				String sleepData = ((Bundle) (msg.obj)).getString("sleepData");
				Toast.makeText(getApplicationContext(), "AM3 sleepData=" + sleepData, Toast.LENGTH_SHORT).show();
				break;
			case 16:
				boolean result_setFactory = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3 reset=" + result_setFactory, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void msgUserStatus_AM3(int status) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("userStatus", status);
		msg.what = 0;
		msg.obj = bundle;
		handler.sendMessage(msg);		
	}
	@Override
	public void msgUserId_AM3(int userid) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("userid", userid);
		msg.what = 1;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSetUserId_AM3(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 2;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgUserinfo_AM3(String userInfo) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("userInfo", userInfo);
		msg.what = 3;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSetUserInfo_AM3(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 4;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgAlarmId_AM3(int[] id) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		String alarmId = "";
		for(int i=0;i<id.length;i++){
			alarmId += "＃" + id[i] + " ";
		}
		bundle.putString("alarmId", alarmId);
		msg.what = 5;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgAlarmInfo_AM3(String alarmInfo) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("alarmInfo", alarmInfo);
		msg.what = 6;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSetAlarmClock_AM3(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 7;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgDeleteAlarmClock_AM3(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 8;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgRemindInfo_AM3(String remindInfo) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("remindInfo", remindInfo);
		msg.what = 9;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSetRemind_AM3(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 10;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	

	@Override
	public void msgStateInfo_AM3(String stateInfo) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("stateInfo", stateInfo);
		msg.what = 11;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgSetMode_AM3(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 12;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgRealData_AM3(String realData) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("realData", realData);
		msg.what = 13;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgActivityData_AM3(String activityData) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("activityData", activityData);
		msg.what = 14;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSleepData_AM3(String sleepData) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("sleepData", sleepData);
		msg.what = 15;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgReset_AM3(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 16;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
}
