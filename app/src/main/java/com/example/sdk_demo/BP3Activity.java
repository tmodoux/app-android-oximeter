package com.example.sdk_demo;

import com.jiuan.android.sdk.bp.bluetooth.manager.BPDeviceManager;
import com.jiuan.android.sdk.bp.observer_bp3.Interface_Observer_BP3;
import com.jiuan.android.sdk.bp.usb.USBControlForBP;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BP3Activity extends Activity implements Interface_Observer_BP3{

	Button btn_getbattery;
	Button btn_getControl;
	TextView tv_battery;
	Button btn_measure;
	TextView tv_measure;
	private BPDeviceManager dm = BPDeviceManager.getInstance();
	private USBControlForBP bpControl;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Bundle bundle0 = (Bundle)msg.obj;
				int status = bundle0.getInt("status");
				Toast.makeText(getApplicationContext(), "user status "+status, Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Bundle bundle1 = (Bundle)msg.obj;
				int battery = bundle1.getInt("battery");
				tv_battery.setText("获得了电量"+battery);
				break;
			case 2:
				Bundle bundle2 = (Bundle)msg.obj;
				int[] result = bundle2.getIntArray("bp");
				tv_measure.setText("压差："+result[0]+"低压："+result[1]+"心跳："+result[2]);
				break;
			default:
				break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp3);
        
        btn_getControl = (Button)findViewById(R.id.btn_getControl);
        btn_getControl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bpControl = dm.getUsbControlForBP();
				if(bpControl != null) {
					bpControl.controlSubject.observersVector.clear();// 监听者模式，加监听
					bpControl.controlSubject.attach(BP3Activity.this);// 监听者模式，加监听
					Toast.makeText(getApplicationContext(), "获取到control", Toast.LENGTH_SHORT).show();
				}
			}
		});
    	btn_getbattery = (Button)findViewById(R.id.btn_getbattery);
    	btn_getbattery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bpControl.getBatteryLevel();
			}
		});
    	tv_battery = (TextView)findViewById(R.id.tv_battery);
    	btn_measure = (Button)findViewById(R.id.btn_measure);
    	btn_measure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(){
					@Override
					public void run() {
						String clientID =  "";
						String clientSecret = "";
						bpControl.start(BP3Activity.this,clientID,clientSecret);
					}
				}.start();
			}
		});
    	tv_measure = (TextView)findViewById(R.id.tv_measure);
    	
    }
	@Override
	public void msgInden() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgBattery(int battery) {
		Message message = new Message();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putInt("battery", battery);
		message.obj = bundle;
		handler.sendMessage(message);
	}
	@Override
	public void msgError(int num) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgAngle(int angle) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgZeroIng() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgZeroOver() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgPressure(int pressure) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgMeasure(int pressure, int[] measure, boolean heart) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgResult(int[] result) {
		Message message = new Message();
		message.what = 2;
		Bundle bundle = new Bundle();
		bundle.putIntArray("bp", result);
		message.obj = bundle;
		handler.sendMessage(message);
	}
	@Override
	public void msgPowerOff(BluetoothDevice device) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgRestart() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgStart() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgUserStatus(int status) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 0;
		Bundle bundle = new Bundle();
		bundle.putInt("status", status);
		message.obj = bundle;
		handler.sendMessage(message);
	}
}
