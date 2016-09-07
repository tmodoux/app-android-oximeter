package com.example.sdk_demo;

import java.util.Iterator;
import java.util.List;
import com.jiuan.android.sdk.device.DeviceManager;
import com.jiuan.android.sdk.hs.bluetooth.HS5Control;
import com.jiuan.android.sdk.hs.observer_hs5.Interface_Observer_HS5BT;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Hs5Activity_bt extends Activity implements Interface_Observer_HS5BT{
	
	private String TAG = "Hs5Activity";
	private HS5Control myControl = null;
	
	private Button btn_set;
	private TextView tv_set;
	
	LayoutInflater wifiPwdInput; 
    private WifiManager mWifiManager;
    private ProgressDialog settingProgressDialog = null;
    private String mAddress = "";
    private DeviceManager deviceManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hs5_bt);
		
		initView();
		mAddress = getIntent().getStringExtra("mac");
		myControl = null;
		deviceManager = DeviceManager.getInstance();
		getControl();
		if(myControl == null){
			btn_set.setVisibility(View.INVISIBLE);
		}else{
			btn_set.setVisibility(View.VISIBLE);
		}
	}

	private void initView(){
		btn_set = (Button)findViewById(R.id.button_complet);
		btn_set.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setWiFiDialog();
			}
		});
		tv_set = (TextView)findViewById(R.id.textview_msg);
		settingProgressDialog = new ProgressDialog(Hs5Activity_bt.this);
	}
	
	private void getControl(){
		
		myControl = deviceManager.getHs5Device(mAddress);
		if(myControl != null){
			myControl.hs5Subject.attach(this);
		}
		
	}
	String ssid;
	int type = 4;
	String wifiPwd;
	private void setWiFiDialog() {
		wifiPwdInput = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);  
    	View viewPwd = wifiPwdInput.inflate(R.layout.setwifi, null); 
    	mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	ssid = mWifiManager.getConnectionInfo().getSSID();
    	Log.i(TAG, "ssid:"+ssid);
		//
    	List<ScanResult> mWifiList; 

    	mWifiManager.startScan();   
        mWifiList = mWifiManager.getScanResults();   
		
		Iterator<ScanResult> it = mWifiList.iterator();
		while(it.hasNext()){
			ScanResult temp = it.next();
			if(mWifiManager.getConnectionInfo().getSSID().equals(temp.SSID)){
				Log.i("wifi", temp.SSID+"|"+temp.capabilities);
				if(temp.capabilities.contains("WPA")|temp.capabilities.contains("WPA2")){
					Log.i("wifi", 4+"");
					type = 4;
				}else if(temp.capabilities.contains("WEP")){
					Log.i("wifi", 1+"");
					type = 1;
				}else{
					Log.i("wifi", 0+"");
					type = 0;
				}
				break;
			}
		}
		
		final EditText etPwd;
		final TextView tvSSID;
		tvSSID = (TextView)viewPwd.findViewById(R.id.btSetWiFi_tv_SSID);
		tvSSID.setText(ssid);
		etPwd = (EditText)viewPwd.findViewById(R.id.btSetWiFi_et_Pwd);
		new AlertDialog.Builder(this)   
		 .setView(viewPwd)
		 .setCancelable(false)
		 .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
	            @Override
				public void onClick(DialogInterface dialog, int which) {  
	                wifiPwd = etPwd.getText().toString();
					new Thread(){
						public void run(){
							if(myControl != null){
								myControl.set(ssid,type,wifiPwd);
							}
						}
					}.start();
                    dialog.dismiss();
            		Message message = new Message();
            		message.what = 1;
            		handler.sendMessage(message);
	            }  
	        }).create().show();
		
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
                if(!settingProgressDialog.isShowing()){
                	settingProgressDialog.setCancelable(false);
                	settingProgressDialog.show();
                }
				break;
			case 2:
                if(settingProgressDialog.isShowing()){  
                	settingProgressDialog.dismiss();
                }
				Bundle bundle = (Bundle)msg.obj;
				boolean result = bundle.getBoolean("result");
				tv_set.setText("set result："+result);
				break;
			default:
				break;
			}
		}
	};
	@Override
	public void msgSetWiFIResult(boolean result) {
		// TODO Auto-generated method stub
		Log.i(TAG, "set result："+result);
		Message message = new Message();
		message.what = 2;
		Bundle bundle = new Bundle();
		bundle.putBoolean("result",result);
		message.obj = bundle;
		handler.sendMessage(message);
	}
}
