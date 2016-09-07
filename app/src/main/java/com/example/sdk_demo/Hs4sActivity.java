
package com.example.sdk_demo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jiuan.android.sdk.hs.bluetooth.HSCommManager;
import com.jiuan.android.sdk.hs.bluetooth.Hs4sControl;

public class Hs4sActivity extends Activity {

    private Button connect, measure, history;
    private Hs4sControl mHs4sControl = null;
    private String deviceMac = "";
    private TextView tv_msg;
    private int mUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs4);
        initReceiver();
        Intent intent = getIntent();
        deviceMac = intent.getStringExtra("mac");

        // mHs4sControl = HSCommManager.mapHS4SDeviceConnected.get(deviceMac);

        Set<HashMap.Entry<String, Hs4sControl>> set4s = HSCommManager.mapHS4SDeviceConnected.entrySet();
        for (Iterator<Map.Entry<String, Hs4sControl>> it = set4s.iterator(); it.hasNext();) {
            Map.Entry<String, Hs4sControl> entry = (Map.Entry<String, Hs4sControl>) it.next();
            if (entry.getKey().equals(deviceMac)) {
                mHs4sControl = entry.getValue();
            }
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
                // btCommManager.stopBluetoothScan();
                mUnit = 1;// unit：01-Kg，02-Lb，03-ST；
                mHs4sControl.connect(Hs4sActivity.this, userId, clientID, clientSecret, mUnit); // unit：01-Kg，02-Lb，03-ST；
            }
        });

        history.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mHs4sControl != null)
                    mHs4sControl.getOfflineData();
            }
        });

        measure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mHs4sControl != null)
                    mHs4sControl.startMeasure();
            }
        });

        tv_msg = (TextView) findViewById(R.id.content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unReceiver();
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Hs4sControl.MSG_HS4S_USER_DATA);
        intentFilter.addAction(Hs4sControl.MSG_HS4S_OFFLINE_DATA);
        intentFilter.addAction(Hs4sControl.MSG_HS4S_REALTIME_DATA);
        intentFilter.addAction(Hs4sControl.MSG_HS4S_RESULT_DATA);
        registerReceiver(mReceiver, intentFilter);
    }

    private void unReceiver() {
        unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Hs4sControl.MSG_HS4S_USER_DATA.equals(action)) {
                int status = intent.getIntExtra(Hs4sControl.MSG_HS4S_USER_DATA_EXTRA, 0);
                tv_msg.setText("user status:" + status);
                Log.i("", "user status:" + status);
            } else if (Hs4sControl.MSG_HS4S_OFFLINE_DATA.equals(action)) {
                String offline = intent.getStringExtra(Hs4sControl.MSG_HS4S_OFFLINE_DATA_EXTRA);
                tv_msg.setText("offline data：" + offline);
                Log.i("", "offline data：" + offline);
            } else if (Hs4sControl.MSG_HS4S_REALTIME_DATA.equals(action)) {
                float real = intent.getFloatExtra(Hs4sControl.MSG_HS4S_REALTIME_DATA_EXTRA, 0.0f);
                String realString = String.valueOf(real); // kg
                if (mUnit == 2) { // lb
                    realString = String.valueOf(getWeight_formKgtoLb(real));
                } else if (mUnit == 3) { // st
                    realString = getWeight_fromKgtoSt(real);
                }
                tv_msg.setText("real weight：" + realString);
                Log.i("", "real weight：" + real);
            } else if (Hs4sControl.MSG_HS4S_RESULT_DATA.equals(action)) {
                float result = intent.getFloatExtra(Hs4sControl.MSG_HS4S_RESULT_DATA_EXTRA, 0.0f);
                String resualtString = String.valueOf(result); // kg
                if (mUnit == 2) { // lb
                    resualtString = String.valueOf(getWeight_formKgtoLb(result));
                } else if (mUnit == 3) { // st
                    resualtString = getWeight_fromKgtoSt(result);
                }
                tv_msg.setText("result：" + resualtString);
                Log.i("", "result：" + resualtString);
            }
        }
    };

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
