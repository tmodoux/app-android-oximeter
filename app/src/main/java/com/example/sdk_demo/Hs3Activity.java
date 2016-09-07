package com.example.sdk_demo;

import com.jiuan.android.sdk.device.DeviceManager;
import com.jiuan.android.sdk.hs.bluetooth.HS3Control;
import com.jiuan.android.sdk.hs.observer_hs3.Interface_Observer_HS3;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Hs3Activity extends Activity implements Interface_Observer_HS3{

	private String TAG = "Hs3Activity";
	private HS3Control myControl = null;
	
	private Button btn_connect;
	private Button btn_offline;
	private String mAddress;
	private TextView tv_msg;
	private DeviceManager deviceManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hs3);
		
		initView();
		mAddress = getIntent().getStringExtra("mac");
		myControl = null;
		deviceManager = DeviceManager.getInstance();
		getControl();
		if(myControl == null){
			btn_connect.setVisibility(View.INVISIBLE);
		}else{
			btn_connect.setVisibility(View.VISIBLE);
		}
	}

	private void initView(){
		btn_connect = (Button)findViewById(R.id.button_connect);
		btn_connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(){
					public void run(){
						//
						if(myControl != null){
							String userId = "";
							final String clientID = "";
							final String clientSecret = "";
							myControl.connect(Hs3Activity.this,userId,clientID,clientSecret);
						}
					}
				}.start();
			}
		});
		
		btn_offline = (Button)findViewById(R.id.button_hs3_offline);
		btn_offline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(){
					public void run(){
						if(myControl != null){
							myControl.getOfflineData();
						}
					}
				}.start();
			}
		});
		
		tv_msg = (TextView)findViewById(R.id.textView_hs3);
		
	}
	
	
	private void getControl(){
		myControl = deviceManager.getHs3Device(mAddress);
		if(myControl != null){
			myControl.hs3Subject.attach(this);
		}else{
			Log.e("", "control is null");
			Toast.makeText(Hs3Activity.this, "noDevice", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void msgUserStatus(int status) {
		// TODO Auto-generated method stub
		Log.i(TAG, "user status "+status);
		Message message = new Message();
		message.what = 0;
		Bundle bundle = new Bundle();
		bundle.putInt("status",status);
		message.obj = bundle;
		handler.sendMessage(message);
	}

	@Override
	public void msgConnectResult(boolean result) {
		// TODO Auto-generated method stub
		Log.i(TAG, "connect result "+result);
		Message message = new Message();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putBoolean("result",result);
		message.obj = bundle;
		handler.sendMessage(message);
	}
	@Override
	public void msgoffLineData(String offLineData) {
		// TODO Auto-generated method stub
		Log.i(TAG, "offline data "+offLineData);
		Message message = new Message();
		message.what = 2;
		Bundle bundle = new Bundle();
		bundle.putString("data",offLineData);
		message.obj = bundle;
		handler.sendMessage(message);
	}

	@Override
	public void msgResult(String result) {
		// TODO Auto-generated method stub
		Log.i(TAG, "result "+result);
		Message message = new Message();
		message.what = 3;
		Bundle bundle = new Bundle();
		bundle.putString("data",result);
		message.obj = bundle;
		handler.sendMessage(message);
	}
	@Override
	public void msgError(int num) {
		// TODO Auto-generated method stub
		Log.i(TAG, "err num "+num);
		Message message = new Message();
		message.what = 4;
		Bundle bundle = new Bundle();
		bundle.putInt("err",num);
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
			case 0:
				Bundle bundle0 = (Bundle)msg.obj;
				int status = bundle0.getInt("status");
				tv_msg.setText("user status:"+status);
				break;
			case 1:
				Bundle bundle1 = (Bundle)msg.obj;
				boolean res = bundle1.getBoolean("result");
				tv_msg.setText("connect ："+res);
				break;
			case 2:
				Bundle bundle2 = (Bundle)msg.obj;
				String data = bundle2.getString("data");
				tv_msg.setText("offline data："+data);
				break;
			case 3:
				Bundle bundle3 = (Bundle)msg.obj;
				String result = bundle3.getString("data");
				tv_msg.setText("result："+result);
				break;
			case 4:
				Bundle bundle4 = (Bundle)msg.obj;
				int errNum = bundle4.getInt("err");
				tv_msg.setText("err："+errNum);
				break;
			default:
				break;
			}
		}
	};
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		myControl.hs3Subject.detach(this);
	}
	
}
