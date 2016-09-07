package com.example.sdk_demo;

import com.jiuan.android.sdk.bp.bluetooth.BPControl;
import com.jiuan.android.sdk.bp.observer_bp.Interface_Observer_BP;
import com.jiuan.android.sdk.device.DeviceManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BP7Activity extends Activity implements Interface_Observer_BP{

	private Button btn2, btn3, btn4, btn5, btn6, btn7;
	private BPControl bpControl;
	private TextView SYS, DIA, Pulse, angle, showisOffLine, showofflinenum;
	private ImageView isOffLine;
	private boolean isOffline = false;
	private String mAddress;
	private DeviceManager deviceManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setContentView(R.layout.activity_bp7);
		mAddress = getIntent().getStringExtra("mac");
		Log.i("mAddress", "mAddress:" + mAddress);
		deviceManager = DeviceManager.getInstance();
		getView();
		initListener();
	}

	private void getView(){
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn4 = (Button) findViewById(R.id.button4);
		btn5 = (Button) findViewById(R.id.button5);
		btn6 = (Button) findViewById(R.id.button6);
		btn7 = (Button) findViewById(R.id.getoffline);
		btn6.setVisibility(View.GONE);
		showisOffLine = (TextView) findViewById(R.id.showisoffline);
		showofflinenum = (TextView) findViewById(R.id.showgetofflinenum);
		isOffLine = (ImageView) findViewById(R.id.isoffline);
		angle = (TextView) findViewById(R.id.angle);
		SYS = (TextView) findViewById(R.id.SYS);
		DIA = (TextView) findViewById(R.id.DIA);
		Pulse = (TextView) findViewById(R.id.Pulse);
	}

	private void initListener(){
		getBpControl bpcontrol = new getBpControl();
		getBatteryLevel batterylevel = new getBatteryLevel();
		getOffLine offline = new getOffLine();
		getAngle angle = new getAngle();
		StartMeansure start = new StartMeansure();
		EndMeansure end = new EndMeansure();
		Checkbox check = new Checkbox();
		btn2.setOnClickListener(bpcontrol);
		btn3.setOnClickListener(batterylevel);
		btn4.setOnClickListener(angle);
		btn5.setOnClickListener(end);
		btn6.setOnClickListener(start);
		btn7.setOnClickListener(offline);
		isOffLine.setOnClickListener(check);
	}

	class getBpControl implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			getbpControl();
			getFunctionInfo();
		}
	}

	class getOffLine implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int num;
			showofflinenum.setVisibility(View.VISIBLE);
			num = bpControl.getOffLineDataNum();
			showofflinenum.setText("offline data num=" + num);
			if(num > 0){
				SystemClock.sleep(500);
				String data = bpControl.getOffLineData();
				Log.i("data",data);
			}
		}
	}

	class Checkbox implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isOffline) {
				isOffline = false;
				isOffLine.setBackgroundResource(R.drawable.isnoteveryday);
				new Thread() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						bpControl.isOffLineData(2);
					}

				}.start();
			}else {
				isOffline = true;
				isOffLine.setBackgroundResource(R.drawable.iseveryday);
				new Thread() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						bpControl.isOffLineData(1);
					}

				}.start();
			}
		}
	}

	class getBatteryLevel implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(bpControl.getBatteryLevel()){
				Toast.makeText(BP7Activity.this, bpControl.getBattery() + "", Toast.LENGTH_SHORT).show();
			}
		}
	}

	class getAngle implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/**
			 * clientID and clientSecret 
			 * the identification of the SDK, will be issued after the iHealth SDK registration
			 * please contact lvjincan@jiuan.com for registration
			 */
			String clientID =  "";
			String clientSecret = "";
			bpControl.start(BP7Activity.this,clientID,clientSecret);
		}
	}

	class StartMeansure implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			bpControl.angleIsOk();
		}
	}

	class EndMeansure implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			bpControl.InterruptMeasure();
		}
	}

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				angle.setText("not left hand");
				btn6.setVisibility(View.GONE);
				break;
			case 1:
				angle.setText("angle ok");
				btn6.setVisibility(View.VISIBLE);
				break;
			case 2:
				angle.setText("angle is small");
				btn6.setVisibility(View.GONE);
				break;
			case 3:
				angle.setText("angle is big");
				btn6.setVisibility(View.GONE);
				break;
			case 4:
				Bundle bundle = (Bundle)msg.obj;
				int[] result = bundle.getIntArray("bp");
				setText(result);
				break;
			case 5:
				Bundle bundle2 = (Bundle)msg.obj;
				int errorNum = bundle2.getInt("error");
				Toast.makeText(BP7Activity.this, "error:"+errorNum, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

	};

	private void getbpControl(){
		bpControl = deviceManager.getBpDevice(mAddress);
		if(bpControl != null){
			showisOffLine.setVisibility(View.VISIBLE);
			isOffLine.setVisibility(View.VISIBLE);
			btn7.setVisibility(View.VISIBLE);
			bpControl.controlSubject.attach(this);
		}else{
			Log.e("", "control is null");
			Toast.makeText(BP7Activity.this, "noDevice", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private void getFunctionInfo(){
		bpControl.FunctionInfo(1);
		if(bpControl.offLinedata){
			showisOffLine.setText("allow offline test");
			isOffline = true;
			isOffLine.setBackgroundResource(R.drawable.iseveryday);
		}else{
			showisOffLine.setText("not allow offline test");
			isOffline = false;
			isOffLine.setBackgroundResource(R.drawable.isnoteveryday);
		}
	}

	@Override
	public void msgInden() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgBattery(int battery) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgUserStatus(int status) {
		// TODO Auto-generated method stub
		//用户状态
		Log.i("", "user status:"+status);
	}

	@Override
	public void msgError(int num) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.what = 5;
		Bundle bundle = new Bundle();
		bundle.putInt("error", num);
		message.obj = bundle;
		handler.sendMessage(message);
	}

	@Override
	public void msgAngle(int angle) {
		// TODO Auto-generated method stub
		try {
			Message msg = new Message();
			msg.what = angle;
			handler.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
		}
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
		// TODO Auto-generated method stub
		Log.e("result", result[0]+" "+result[1]+" "+result[2]+" ");
		//显示:
		try {
			Message message = new Message();
			message.what = 4;
			Bundle bundle = new Bundle();
			bundle.putIntArray("bp", result);
			message.obj = bundle;
			handler.sendMessage(message);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void msgPowerOff() {
		// TODO Auto-generated method stub

	}

	private void setText(int[] result){
		SYS.setText((result[0] + result[1]) + "");
		DIA.setText(result[1] + "");
		Pulse.setText(result[2] + "");
	}
}
