package com.example.sdk_demo;

import com.jiuan.android.sdk.am.bluetooth.lpcbt.AM3SControl;
import com.jiuan.android.sdk.am.bluetooth.lpcbt.JiuanAM3SObserver;
import com.jiuan.android.sdk.device.DeviceManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AM3SActivity extends Activity implements JiuanAM3SObserver {

	private Button btn_connect;
	private Button user_Scan, user_Set, user_Reset, user_Info, user_InfoScan;
	private Button sport, sleep, realdata, queryInfo, planemodel, stagereport;
	private Button queryAlarmNum, setAlarm, queryAlarm, deleteAlarm, queryreminder, setreminder, hour;
	private DeviceManager deviceManager;
	private String mAddress = "";
	private AM3SControl am3sControl;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				int userStatus = ((Bundle) (msg.obj)).getInt("userStatus");
				Toast.makeText(getApplicationContext(), "AM3s UserStatus=" + userStatus, Toast.LENGTH_SHORT).show();
				break;
			case 1://显示用户id信息
				int userid = ((Bundle) (msg.obj)).getInt("userid");
				Toast.makeText(getApplicationContext(), "AM3s Userid =" + userid, Toast.LENGTH_SHORT).show();
				break;
			case 2:
				boolean result_random = ((Bundle) (msg.obj)).getBoolean("result");
				final EditText editText = new EditText(AM3SActivity.this);
				new AlertDialog.Builder(AM3SActivity.this).setTitle("please entry").setView(editText).setPositiveButton("OK", 
				new DialogInterface.OnClickListener() {              
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        // TODO Auto-generated method stub
				        am3sControl.setUser(Integer.parseInt(editText.getText().toString().trim()),am3sControl.getUserId());
				    }
				}).setNegativeButton("Cancel", null).show();
				break;
			case 3:
				boolean result_user = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3s setUser=" + result_user, Toast.LENGTH_SHORT).show();
				break;
			case 4:
				boolean result_info = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3s setUserInfo=" + result_info, Toast.LENGTH_SHORT).show();
				break;
			case 5:
				String userInfo = ((Bundle) (msg.obj)).getString("userInfo");
				Toast.makeText(getApplicationContext(), "AM3s UserInfo=" + userInfo, Toast.LENGTH_SHORT).show();
				break;
			case 6:
				String alarmId = ((Bundle) (msg.obj)).getString("alarmId");
				Toast.makeText(getApplicationContext(), "AM3s alarmId=" + alarmId, Toast.LENGTH_SHORT).show();
				break;
			case 7:
				String alarmInfo = ((Bundle) (msg.obj)).getString("alarmInfo");
				Toast.makeText(getApplicationContext(), "AM3s alarmInfo=" + alarmInfo, Toast.LENGTH_SHORT).show();
				break;
			case 8:
				boolean result_setAlarm = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3s set clock=" + result_setAlarm, Toast.LENGTH_SHORT).show();
				break;
			case 9:
				boolean result_deleteAlarm = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3s delete clock=" + result_deleteAlarm, Toast.LENGTH_SHORT).show();
				break;
			case 10:
				String remindInfo = ((Bundle) (msg.obj)).getString("remindInfo");
				Toast.makeText(getApplicationContext(), "AM3s remindInfo=" + remindInfo, Toast.LENGTH_SHORT).show();
				break;
			case 11:
				boolean result_setRemind = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3s set sportRemind=" + result_setRemind, Toast.LENGTH_SHORT).show();
				break;
			case 12:
				String stateInfo = ((Bundle) (msg.obj)).getString("stateInfo");
				Toast.makeText(getApplicationContext(), "AM3s stateInfo=" + stateInfo, Toast.LENGTH_SHORT).show();
				break;
			case 13:
				boolean result_setFlyMode = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3s set mode=" + result_setFlyMode, Toast.LENGTH_SHORT).show();
				break;
			case 14:
				boolean result_setHour = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3s set hour=" + result_setHour, Toast.LENGTH_SHORT).show();
				break;
			case 15:
				String realData = ((Bundle) (msg.obj)).getString("realData");
				Toast.makeText(getApplicationContext(), "AM3s RealData=" + realData, Toast.LENGTH_SHORT).show();
				break;
			case 16:
				String StageReportDatas = ((Bundle) (msg.obj)).getString("StageReportDatas");
				Toast.makeText(getApplicationContext(), "AM3s StageReportDatas=" + StageReportDatas, Toast.LENGTH_SHORT).show();
				break;
			case 17:
				String activityDatas = ((Bundle) (msg.obj)).getString("activityDatas");
				Toast.makeText(getApplicationContext(), "AM3s StageReportDatas=" + activityDatas, Toast.LENGTH_SHORT).show();
				break;
			case 18:
				String sleepDatas = ((Bundle) (msg.obj)).getString("sleepDatas");
				Toast.makeText(getApplicationContext(), "AM3s StageReportDatas=" + sleepDatas, Toast.LENGTH_SHORT).show();
				break;
			case 19:
				boolean result_setFactory = ((Bundle) (msg.obj)).getBoolean("result");
				Toast.makeText(getApplicationContext(), "AM3s reset=" + result_setFactory, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_am3s);
		deviceManager = DeviceManager.getInstance();
		mAddress = getIntent().getStringExtra("mac");
		if(deviceManager.getAmDevice(mAddress) instanceof AM3SControl) {
			am3sControl = (AM3SControl) (deviceManager.getAmDevice(mAddress));
		}
		
		if(am3sControl != null){
			am3sControl.addObserver(this);
		}
		
		btn_connect = (Button)findViewById(R.id.connect_am3s);
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
				am3sControl.connect(AM3SActivity.this,userId,clientID,clientSecret);
			}
		});
		
		user_Scan = (Button) findViewById(R.id.scanUser_am3s);
		user_Set = (Button) findViewById(R.id.setUser_am3s);
		user_Scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.queryUserID();
			}
		});
		user_Set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.sendRandom();
			}
		});
		
		user_Info = (Button) findViewById(R.id.infoUser_am3s);
		user_InfoScan = (Button) findViewById(R.id.scaninfoUser_am3s);
		user_Info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.setUserInfo(25, 170, 60, 1, 1, 10000, 2);
			}
		});
		user_InfoScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.checkUserInfo();
			}
		});
		
		queryAlarmNum = (Button) findViewById(R.id.queryAlarmNum_am3s);
		queryAlarm = (Button) findViewById(R.id.queryAlarm_am3s);
		setAlarm = (Button) findViewById(R.id.setAlarm_am3s);
		deleteAlarm = (Button) findViewById(R.id.deleteAlarm_am3s);
		queryAlarmNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.getAlarmClockNum();
			}
		});
		queryAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.checkAlarmClock(1);
			}
		});
		setAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.setAlarmClock((1), 11, 45, true, new int[]{1, 1, 1, 1, 1, 1, 1}, true);

			}
		});
		deleteAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.deleteAlarmClock(1);
			}
		});
		
		queryreminder = (Button) findViewById(R.id.queryreminder_am3s);
		setreminder = (Button) findViewById(R.id.setreminder_am3s);
		queryreminder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.getActivityRemind();
			}
		});
		setreminder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.setActivityRemind(1, 0, true);
			}
		});
		
		queryInfo = (Button) findViewById(R.id.queryInfo_am3s);
		planemodel = (Button) findViewById(R.id.planemodel_am3s);
		hour = (Button) findViewById(R.id.hour_am3s);
		queryInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.queryAMState();
			}
		});
		planemodel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.setFlyMode();
			}
		});
		hour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.setHour(1);
			}
		});
		sport = (Button) findViewById(R.id.sport_am3s);
		sleep = (Button) findViewById(R.id.sleep_am3s);
		realdata = (Button) findViewById(R.id.realdata_am3s);
		stagereport = (Button) findViewById(R.id.Stagereport_am3s);
		
		realdata.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.syncRealData();
			}
		});
		stagereport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.syncStageReportData();
//				byte[] data = new byte[]{
//						(byte)0x01,(byte)0x0E,(byte)0x09,(byte)0x01,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x14,(byte)0x06,(byte)0xDE,(byte)0x01,(byte)0x21,(byte)0x00,(byte)0x29,(byte)0x00,(byte)0x00,(byte)0x00,
//						(byte)0x01,(byte)0x0E,(byte)0x09,(byte)0x02,(byte)0x0B,(byte)0x1C,(byte)0x00,(byte)0x16,(byte)0x08,(byte)0x0E,(byte)0x01,(byte)0x38,(byte)0x00,(byte)0x2E,(byte)0x00,(byte)0x00,(byte)0x00,
//						(byte)0x02,(byte)0x0E,(byte)0x09,(byte)0x02,(byte)0x10,(byte)0x36,(byte)0x00,(byte)0x37,(byte)0x03,(byte)0x8D,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
//						(byte)0x01,(byte)0x0E,(byte)0x09,(byte)0x02,(byte)0x13,(byte)0x0A,(byte)0x00,(byte)0x12,(byte)0x04,(byte)0x34,(byte)0x00,(byte)0x51,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x00,
//						(byte)0x01,(byte)0x0E,(byte)0x09,(byte)0x02,(byte)0x13,(byte)0x31,(byte)0x00,(byte)0x1F,(byte)0x07,(byte)0xD5,(byte)0x01,(byte)0x34,(byte)0x00,(byte)0x2E,(byte)0x00,(byte)0x00,(byte)0x00,
//						(byte)0x01,(byte)0x0E,(byte)0x09,(byte)0x02,(byte)0x14,(byte)0x32,(byte)0x00,(byte)0x0A,(byte)0x04,(byte)0x09,(byte)0x00,(byte)0x4E,(byte)0x00,(byte)0x17,(byte)0x00,(byte)0x00,(byte)0x00,
//						(byte)0x01,(byte)0x0E,(byte)0x09,(byte)0x03,(byte)0x08,(byte)0x23,(byte)0x00,(byte)0x11,(byte)0x07,(byte)0x5C,(byte)0x01,(byte)0x2B,(byte)0x00,(byte)0x2A,(byte)0x00,(byte)0x00,(byte)0x00
//						};
//				am3sControl.getAM3SStageReportDatas(data);
			}
		});
		sport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.syncActivityData();
			}
		});
		sleep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.syncSleepData();
			}
		});
		
		user_Reset = (Button) findViewById(R.id.resetUser_am3s);
		user_Reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				am3sControl.reset(am3sControl.getUserId());
			}
		});
	}
//
//	@Override
//	public void msgConnected_AM3s(String mac) {
//		// TODO Auto-generated method stub
//
//	}


	@Override
	public void msgUserStatus_AM3s(int status) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("userStatus", status);
		msg.what = 0;
		msg.obj = bundle;
		handler.sendMessage(msg);
	} 

	@Override
	public void msgUserId_AM3s(int userid) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("userid", userid);
		msg.what = 1;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgRandom_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 2;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSetUserId_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 3;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSetUserInfo_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 4;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgUserinfo_AM3s(String userInfo) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("userInfo", userInfo);
		msg.what = 5;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgAlarmId_AM3s(int[] id) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		String alarmId = "";
		for(int i=0;i<id.length;i++){
			alarmId += "＃" + id[i] + " ";
		}
		bundle.putString("alarmId", alarmId);
		msg.what = 6;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgAlarmInfo_AM3s(String alarmInfo) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("alarmInfo", alarmInfo);
		msg.what = 7;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSetAlarmClock_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 8;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgDeleteAlarmClock_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 9;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	

	@Override
	public void msgRemindInfo_AM3s(String remindInfo) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("remindInfo", remindInfo);
		msg.what = 10;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSetRemind_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 11;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgStateInfo_AM3s(String stateInfo) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("stateInfo", stateInfo);
		msg.what = 12;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgMode_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 13;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgSetHour_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 14;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgRealData_AM3s(String realData) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("realData", realData);
		msg.what = 15;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgStageReportData_AM3s(String StageReportData) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("StageReportDatas", StageReportData);
		msg.what = 16;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
	
	@Override
	public void msgActivityData_AM3s(String activityDatas) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("activityDatas", activityDatas);
		msg.what = 17;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgSleepData_AM3s(String sleepDatas) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("sleepDatas", sleepDatas);
		msg.what = 18;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}

	@Override
	public void msgReset_AM3s(boolean result) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", result);
		msg.what = 19;
		msg.obj = bundle;
		handler.sendMessage(msg);
	}
//	@Override
//	public void msgdisconnect_AM3s(String mac) {
//		// TODO Auto-generated method stub
//
//	}

}
