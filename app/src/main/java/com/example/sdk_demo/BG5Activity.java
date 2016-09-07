package com.example.sdk_demo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jiuan.android.sdk.bg.bluetooth.BG5Control;
import com.jiuan.android.sdk.bg.observer.Interface_Observer_BG;
import com.jiuan.android.sdk.device.DeviceManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BG5Activity extends Activity implements Interface_Observer_BG {
	
	private boolean isDebug = true;
	private String TAG = "BG5Activity";
	String qr = "02323C50435714322D1200A0404B6AACFE144D7A97E619011E250003158D";
	private DeviceManager deviceManager;
	private BG5Control bg5Control;
	
	private Button btn_control;
	
	private TextView tv_unit;
	private TextView tv_bottleId;
	private TextView tv_offline;
	private TextView tv_delete;
	private TextView tv_code;
	private TextView tv_result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bg5);
		deviceManager = DeviceManager.getInstance();
		mAddress = getIntent().getStringExtra("mac");
		btn_control = (Button)findViewById(R.id.control);
		btn_control.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(){
					public void run(){
						connectDevice();
					}
				}.start();
			}
		});
		
		tv_unit = (TextView)findViewById(R.id.textView1);
		tv_bottleId = (TextView)findViewById(R.id.textView2);
		tv_offline = (TextView)findViewById(R.id.textView3);
		tv_delete = (TextView)findViewById(R.id.textView4);
		tv_code = (TextView)findViewById(R.id.textView5);
		tv_result = (TextView)findViewById(R.id.textView6);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("", "onStop");
	}
	private String mAddress;
	private void connectDevice(){
		
		bg5Control = deviceManager.getBg5Device(mAddress);
		if(bg5Control != null){
			if(isDebug)
				Log.i(TAG, "get control");		
			bg5Control.bg5subject.attach(BG5Activity.this);
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
			bg5Control.connect(BG5Activity.this,userId, clientID, clientSecret);
			
		}else{
			if(isDebug)
				Log.i(TAG, "control is null");		
		}
	}
	@Override
	public void msgUserStatus(int status) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "user status "+status, Toast.LENGTH_SHORT).show();

		if(status == 1||status == 2||status == 3||status == 4){
			new Thread(){
				public void run(){
						if (bg5Control.setParams(1, 1)) {
							Message message1 = new Message();
							message1.what = 1;
							handler.sendMessage(message1);

							int bottleId = bg5Control.readBottleId();
							Log.i(TAG, "bottleId:" + bottleId);
							Message message2 = new Message();
							message2.what = 2;
							Bundle bundle2 = new Bundle();
							bundle2.putInt("bottleId", bottleId);
							message2.obj = bundle2;
							handler.sendMessage(message2);

							String offline = bg5Control.readOfflineData();
							Log.i(TAG, "offline:" + offline);
							Message message3 = new Message();
							message3.what = 3;
							Bundle bundle3 = new Bundle();
							bundle3.putString("offline", offline);
							message3.obj = bundle3;
							handler.sendMessage(message3);

							if (bg5Control.deleteOfflineData()) {
								Message message4 = new Message();
								message4.what = 4;
								handler.sendMessage(message4);
							}

							String code = bg5Control.getCode();
							Log.i(TAG, "code:" + code);
							Message message5 = new Message();
							message5.what = 5;
							Bundle bundle5 = new Bundle();
							bundle5.putString("code", code);
							message5.obj = bundle5;
							handler.sendMessage(message5);

							try {
								String BottleInfo = bg5Control.getBottleInfoByErWeiMa(qr);
								JSONTokener jsonTParser = new JSONTokener(BottleInfo);
								JSONObject bottleInfoObj = (JSONObject) jsonTParser.nextValue();
								String bottleIdFromQR = bottleInfoObj.optJSONArray("bottleInfo").optJSONObject(0).getString("bottleId");
								int stripNumFromQR = Integer.parseInt(bottleInfoObj.optJSONArray("bottleInfo").optJSONObject(0).getString("stripNum"));
								String overDateFromQR = bottleInfoObj.optJSONArray("bottleInfo").optJSONObject(0).getString("overDate");
	
								Log.i(TAG, "bottleIdFromQR = " + bottleIdFromQR);
								Log.i(TAG, "stripNumFromQR = " + stripNumFromQR);
								Log.i(TAG, "overDateFromQR = " + overDateFromQR);
								
								bg5Control.sendCode(qr,overDateFromQR,stripNumFromQR);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
			}.start();
		}	
	}
	@Override
	public void msgBGStripIn() {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 7;
		Bundle bundle = new Bundle();
		bundle.putString("result", "strip in");
		message.obj = bundle;
		handler.sendMessage(message);
	}
	@Override
	public void msgBGGetBlood() {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 7;
		Bundle bundle = new Bundle();
		bundle.putString("result", "got blood");
		message.obj = bundle;
		handler.sendMessage(message);
	}
	@Override
	public void msgBGResult(int result) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 7;
		Bundle bundle = new Bundle();
		bundle.putString("result", "result:"+result);
		message.obj = bundle;
		handler.sendMessage(message);
	}
	@Override
	public void msgBGStripOut() {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 7;
		Bundle bundle = new Bundle();
		bundle.putString("result", "strip out");
		message.obj = bundle;
		handler.sendMessage(message);
	}
	@Override
	public void msgBGError(int errNum) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 7;
		Bundle bundle = new Bundle();
		bundle.putString("result", "errNum:"+errNum);
		Log.e(TAG, "BG5  errNum:"+errNum);
		message.obj = bundle;
		handler.sendMessage(message);
	}
	/**
	 * UI handler
	 */
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				tv_unit.setText("set unit ok");
				break;
			case 2:
				Bundle bundle2 = (Bundle)msg.obj;
				int bottleId = bundle2.getInt("bottleId");
				tv_bottleId.setText("bottleId:"+bottleId);
				break;
			case 3:
				Bundle bundle3 = (Bundle)msg.obj;
				String offline = bundle3.getString("offline");
				tv_offline.setText("offline:"+offline);
				break;
			case 4:
				tv_delete.setText("delete done");
				break;
			case 5:
				Bundle bundle5 = (Bundle)msg.obj;
				String code = bundle5.getString("code");
				tv_code.setText("code:"+code);
				break;
			case 7:
				Bundle bundle7 = (Bundle)msg.obj;
				String result = bundle7.getString("result");
				tv_result.setText(result);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void msgBGPowerOff() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgDeviceReady_new() {
		// TODO Auto-generated method stub
		
	}

}
