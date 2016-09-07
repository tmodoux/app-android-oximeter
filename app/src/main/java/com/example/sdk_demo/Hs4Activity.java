
package com.example.sdk_demo;

import java.math.BigDecimal;

import com.jiuan.android.sdk.device.DeviceManager;
import com.jiuan.android.sdk.hs.bluetooth.lpcbt.HS4Control;
import com.jiuan.android.sdk.hs.bluetooth.lpcbt.JiuanHS4Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Hs4Activity extends Activity implements JiuanHS4Observer {

    private String TAG = "Hs4Activity";
    private Button connect, measure, history;
    private HS4Control hs4Control = null;
    private String mAddress = "";
    private TextView tv_msg;
    private DeviceManager deviceManager;
    private int mUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs4);

        deviceManager = DeviceManager.getInstance();
        Intent intent = getIntent();
        mAddress = intent.getStringExtra("mac");
        if (deviceManager.getAmDevice(mAddress) instanceof HS4Control) {
            hs4Control = (HS4Control) (deviceManager.getAmDevice(mAddress));
            hs4Control.addObserver(this);
        }
        history = (Button) findViewById(R.id.history);
        measure = (Button) findViewById(R.id.online);
        connect = (Button) findViewById(R.id.connect);

        connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String userId = "";
                final String clientID = "";
                final String clientSecret = "";
                Log.i("gnn", "connect click");
                mUnit = 1;// unit：01-Kg，02-Lb，03-ST；
                hs4Control.connect(Hs4Activity.this, userId, clientID, clientSecret, mUnit); // unit：01-Kg，02-Lb，03-ST；
            }
        });

        history.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (hs4Control != null) {
                    new Thread() {
                        @Override
                        public void run() {
                            hs4Control.getOfflineData();
                        }

                    }.start();
                }
            }
        });

        measure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (hs4Control != null) {
                    new Thread() {
                        @Override
                        public void run() {
                            hs4Control.startMeasure();
                        }

                    }.start();
                }
            }
        });

        tv_msg = (TextView) findViewById(R.id.content);

    }

    @Override
    public void msgUserStatus(int status) {
        // TODO Auto-generated method stub
        Log.e(TAG, "user status:" + status);
        Message message = new Message();
        message.what = 1;
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgOffLineData_HS4(String offlineData) {
        // TODO Auto-generated method stub
        Log.e(TAG, "offline data:" + offlineData);
        Message message = new Message();
        message.what = 2;
        Bundle bundle = new Bundle();
        bundle.putString("data", offlineData);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgCurrentData_HS4(float weight) {
        // TODO Auto-generated method stub
        Log.e(TAG, "realWeight" + weight);
        Message message = new Message();
        message.what = 3;
        Bundle bundle = new Bundle();
        bundle.putFloat("weight", weight);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgOnLineData_HS4(float weight) {
        // TODO Auto-generated method stub
        Log.e(TAG, "stabWeight" + weight);
        Message message = new Message();
        message.what = 4;
        Bundle bundle = new Bundle();
        bundle.putFloat("weight", weight);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    /**
     * UI handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bundle bundle1 = (Bundle) msg.obj;
                    int status = bundle1.getInt("status");
                    tv_msg.setText("user status:" + status);
                    break;
                case 2:
                    Bundle bundle2 = (Bundle) msg.obj;
                    String data = bundle2.getString("data");
                    tv_msg.setText("offline data：" + data);
                    break;
                case 3:
                    Bundle bundle3 = (Bundle) msg.obj;
                    Float result0 = bundle3.getFloat("weight");
                    String realString = String.valueOf(result0); // kg
                    if (mUnit == 2) { // lb
                        realString = String.valueOf(getWeight_formKgtoLb(result0));
                    } else if (mUnit == 3) { // st
                        realString = getWeight_fromKgtoSt(result0);
                    }
                    tv_msg.setText("real weight：" + realString);
                    break;
                case 4:
                    Bundle bundle4 = (Bundle) msg.obj;
                    Float result1 = bundle4.getFloat("weight");
                    String resualtString = String.valueOf(result1); // kg
                    if (mUnit == 2) { // lb
                        resualtString = String.valueOf(getWeight_formKgtoLb(result1));
                    } else if (mUnit == 3) { // st
                        resualtString = getWeight_fromKgtoSt(result1);
                    }
                    tv_msg.setText("result：" + resualtString);
                    break;
                default:
                    break;
            }
        }
    };

    public String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * kg --> lb
     */
    public static float getWeight_formKgtoLb(float val) {

        BigDecimal multiplier = new BigDecimal("2.2046154");
        BigDecimal multiplicand = new BigDecimal(val + "");
        BigDecimal product = multiplier.multiply(multiplicand);

        Float result = product.setScale(1, BigDecimal.ROUND_DOWN).floatValue();
        return result;
    }

    /**
     * kg --> st
     */
    public static String getWeight_fromKgtoSt(float val) {

        float temp_f = (float) ((val * 2.2046226218488) / 14);
        String temp_s = String.valueOf(temp_f);
        int i = temp_s.indexOf(".");
        String st = temp_s.substring(0, i);
        String temp_bl = "0" + temp_s.substring(i, temp_s.length());
        BigDecimal b = new BigDecimal(Float.parseFloat(temp_bl) * 14);
        float lb_val = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        if ((int) lb_val == 14) {
            return String.valueOf(Integer.parseInt(st) + 1) + ":" + "0.0";
        }
        return st + ":" + String.valueOf(lb_val);
    }

}
