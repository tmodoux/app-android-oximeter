
package com.example.sdk_demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.jiuan.android.sdk.abi.bluetooth.ABICommManager;
import com.jiuan.android.sdk.abi.observer_comm.Interface_Observer_CommMsg_ABI;
import com.jiuan.android.sdk.am.observer_comm.Interface_Observer_CommMsg_AM;
import com.jiuan.android.sdk.bg.audio.BG1Control;
import com.jiuan.android.sdk.bg.observer.Interface_Observer_BG;
import com.jiuan.android.sdk.bg.observer.Interface_Observer_BGCoomMsg;
import com.jiuan.android.sdk.bp.observer_comm.Interface_Observer_CommMsg_BP;
import com.jiuan.android.sdk.device.DeviceManager;
import com.jiuan.android.sdk.hs.bluetooth.Hs4sControl;
import com.jiuan.android.sdk.hs.observer_comm.Interface_Observer_CommMsg_HS;
import com.jiuan.android.sdk.po.observer_comm.Interface_Observer_CommMsg_PO;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements Interface_Observer_BGCoomMsg,
        Interface_Observer_CommMsg_BP,
        Interface_Observer_CommMsg_AM,
        Interface_Observer_CommMsg_HS,
        Interface_Observer_CommMsg_PO,
        Interface_Observer_BG,
        Interface_Observer_CommMsg_ABI {

    private String TAG = "MainActivity";
    private ListView mDeviceListview;
    private List<HashMap<String, String>> mDeviceList;
    private SimpleAdapter deviceSimpleAdapter;

    private String userId = "";
    private Button btn_bg1;
    private Button btn_scan;
    private DeviceManager deviceManager = DeviceManager.getInstance();

    BG1Control bg1Control;

    private TextView tv_msg_1;
    private TextView tv_msg_2;
    private TextView tv_msg_3;
    private Timer mtimer;
    private TimerTask mTimerTask;

    String qr = "02323C64323C14322D1200A03E36BCF9D91446B4DC6E19011EDA01201D39";

    String BottleId = "18882266";

    public static String testDevice;

    boolean userFlag = false;
    boolean bg1Flag = false;

    private static final int LOGIN_REQUEST = 1;

    private TextView progressView;
    private Button login;

    private Credentials credentials;

    public class connectRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /**
             * userId the identification of the user, could be the form of email address or mobile
             * phone number (mobile phone number is not supported temporarily). clientID and
             * clientSecret, as the identification of the SDK, will be issued after the iHealth SDK
             * registration. please contact lvjincan@jiuan.com for registration.
             */
            String userId = "liu01234345555@jiuan.com";
            final String clientID = "2a8387e3f4e94407a3a767a72dfd52ea";
            final String clientSecret = "fd5e845c47944a818bc511fb7edb0a77";
            bg1Control.connect(MainActivity.this, userId, clientID, clientSecret);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        progressView = (TextView) findViewById(R.id.progress);

        login = (Button) findViewById(R.id.login);

        credentials = new Credentials(this);
        if(credentials.hasCredentials()) {
            setLogoutView();
        } else {
            setLoginView();
        }

        tv_msg_1 = (TextView) findViewById(R.id.TV1);
        tv_msg_2 = (TextView) findViewById(R.id.TV2);
        tv_msg_3 = (TextView) findViewById(R.id.TV3);

        mDeviceList = new ArrayList<HashMap<String, String>>();
        deviceManager.initDeviceManager(this, userId);
        deviceManager.initReceiver();
        deviceManager.initAmStateCallback(this);
        deviceManager.initBgStateCallback(this);
        deviceManager.initBpStateCallback(this);
        deviceManager.initHsStateCallback(this);
        deviceManager.initPoStateCallaback(this);
        deviceManager.initABIStateCallback(this);
        mDeviceListview = (ListView) findViewById(R.id.bg5list);

        bg1Control = new BG1Control();
        bg1Control.bg1Subject.attach(this);

        btn_bg1 = (Button) findViewById(R.id.buttonbg1);
        btn_bg1.setVisibility(View.INVISIBLE);
        // btn_bg1.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // Intent intent = new Intent(MainActivity.this, BG1Activity.class);
        // startActivity(intent);
        // }
        // });
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        deviceManager.scanDevice();
                    }
                }).start();
            }
        });
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Hs4sControl.MSG_HS4S_CONNECTED);
        intentFilter.addAction(Hs4sControl.MSG_HS4S_DISCONNECT);
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mReceiver, intentFilter);
    }

    private void unReceiver() {
        unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Hs4sControl.MSG_HS4S_CONNECTED.equals(action)) {
                String mac = intent.getStringExtra(Hs4sControl.MSG_HS4S_MAC);
                deviceMap.put(mac, "HS4S");
                refresh();
            } else if (Hs4sControl.MSG_HS4S_DISCONNECT.equals(action)) {
                String mac = intent.getStringExtra(Hs4sControl.MSG_HS4S_MAC);
                deviceMap.remove(mac);
                refresh();
            } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    Log.e(TAG, "isHomeKey");
                    if (deviceManager != null) {
                        deviceManager.cancelScanDevice();
                        // close udpsoket
                        deviceManager.closeUdp();
                        Set<HashMap.Entry<String, String>> set = deviceMap.entrySet();
                        for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
                            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                            if (deviceMap.get(entry.getKey()) != null
                                    && deviceMap.get(entry.getKey()).equals("HS5WIFI")) {
                                deviceMap.remove(entry.getKey());
                                refresh();
                            }
                        }

                    }
                }
            }
        }
    };

    private void initListView() {
        if (mDeviceList != null) {
            deviceSimpleAdapter = new SimpleAdapter(this, mDeviceList, R.layout.listview_baseview,
                    new String[] {
                            "type", "mac"
                    },
                    new int[] {
                            R.id.bgname, R.id.bgaddress
                    });
            mDeviceListview.setAdapter(deviceSimpleAdapter);
            mDeviceListview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                        int position, long id) {
                    if (mDeviceList.get(position).get("type").equals("BG1")) {
                        // deviceManager.cancelScanDevice();
                        // Intent intent = new Intent(MainActivity.this, BG1Activity.class);
                        // intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        // startActivity(intent);
                    } else if (mDeviceList.get(position).get("type").equals("BG5")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, BG5Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("BP3")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, BP3Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("BP5")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, BP5Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("BP7")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, BP7Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("HS3")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, Hs3Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("HS4")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, Hs4Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("HS4S")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, Hs4sActivity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("HS5")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, Hs5Activity_bt.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("HS5WIFI")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, Hs5Activity_wifi.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("AM3")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, AM3Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("AM3S")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, AM3SActivity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").equals("PO3")) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, Po3Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    } else if (mDeviceList.get(position).get("type").contains(ABICommManager.DEVICETYPE)) {
                        deviceManager.cancelScanDevice();
                        Intent intent = new Intent(MainActivity.this, ABI_Activity.class);
                        intent.putExtra("mac", mDeviceList.get(position).get("mac"));
                        startActivity(intent);
                        return;
                    }
                }
            });
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "bg1 identify success", Toast.LENGTH_SHORT).show();
                    tv_msg_2.setText("bg1 identify success");
                    bg1Flag = true;
                    tv_msg_1.setVisibility(View.VISIBLE);
                    handler1.sendEmptyMessage(1);
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "bg1 identify fail", Toast.LENGTH_SHORT).show();
                    tv_msg_2.setText("bg1 identify fail");
                    bg1Flag = false;
                    break;
                case 3:
                    tv_msg_3.setText("user identify success");
                    userFlag = true;
                    tv_msg_2.setVisibility(View.VISIBLE);
                    handler1.sendEmptyMessage(1);
                    break;
                case 4:
                    tv_msg_3.setText("user status :" + ((Bundle) (msg.obj)).getInt("Status"));
                    userFlag = false;
                    break;
                case 5:
                    map2List();
                    initListView();
                    break;

                case 7:
                    Bundle bundle = (Bundle) msg.obj;
                    String result = bundle.getString("result");
                    tv_msg_1.setText(result);
                    break;

                case 8:
                    btn_scan.setVisibility(View.INVISIBLE);
                    tv_msg_3.setVisibility(View.VISIBLE);
                    tv_msg_3.setText("user identifying...");
                    Thread my = new Thread(new connectRunnable());
                    my.start();

                    break;
                case 9:
                    btn_scan.setVisibility(View.VISIBLE);

                    tv_msg_1.setVisibility(View.INVISIBLE);
                    tv_msg_2.setVisibility(View.INVISIBLE);
                    tv_msg_3.setVisibility(View.INVISIBLE);
                    tv_msg_1.setText("");
                    tv_msg_2.setText("");
                    tv_msg_3.setText("");

                    userFlag = false;
                    bg1Flag = false;

                    break;
                default:
                    break;
            }
        }

    };

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (userFlag && bg1Flag) {
                        tv_msg_1.setText("please plug in the strip");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e(TAG, "onResume");
        if (deviceManager != null) {
            deviceManager.reStartUdp();
        }
    }

    @Override
    public void msgHeadsetPluIn() {
        // TODO Auto-generated method stub
        Message message = new Message();
        message.what = 8;
        handler.sendMessage(message);
    }

    @Override
    public void msgHeadsetPullOut() {
        // TODO Auto-generated method stub
        Message message = new Message();
        message.what = 9;
        handler.sendMessage(message);

        if (mtimer != null) {
            mtimer.cancel();
            mtimer = null;
        }

        if (bg1Control != null) {
            bg1Control.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop----调用了 deviceManager.unReceiver()");
//        if (deviceManager != null) {
//            deviceManager.unReceiver();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy----调用了 deviceManager.unReceiver()");
        if (deviceManager != null) {
            deviceManager.unReceiver();
        }
        unReceiver();
    }

    private List<HashMap<String, String>> map2List() {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        mDeviceList.clear();
        for (Iterator<String> it = deviceMap.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("type", deviceMap.get(key));
            map.put("mac", (String) key);
            mDeviceList.add(map);
        }
        return list;
    }

    private void refresh() {
        Message message = new Message();
        message.what = 5;
        handler.sendMessage(message);
    }

    private Map<String, String> deviceMap = new ConcurrentHashMap<String, String>();

    @Override
    public void msgDeviceConnect_Hs(String deviceMac, String deviceType) {
        deviceMap.put(deviceMac, deviceType);
        refresh();
    }

    @Override
    public void msgDeviceDisconnect_Hs(String deviceMac, String deviceType) {
        deviceMap.remove(deviceMac);
        refresh();
    }

    @Override
    public void msgDeviceConnect_Am(String deviceMac, String deviceType) {
        deviceMap.put(deviceMac, deviceType);
        refresh();
    }

    @Override
    public void msgDeviceDisconnect_Am(String deviceMac, String deviceType) {
        deviceMap.remove(deviceMac);
        refresh();
    }

    @Override
    public void msgDeviceConnect_Bp(String deviceMac, String deviceType) {
        deviceMap.put(deviceMac, deviceType);
        refresh();
    }

    @Override
    public void msgDeviceDisconnect_Bp(String deviceMac, String deviceType) {
        deviceMap.remove(deviceMac);
        refresh();
    }

    @Override
    public void msgDeviceConnect_Bg(String deviceMac, String deviceType) {
        deviceMap.put(deviceMac, deviceType);
        refresh();
    }

    @Override
    public void msgDeviceDisconnect_Bg(String deviceMac, String deviceType) {
        deviceMap.remove(deviceMac);
        refresh();
    }

    @Override
    public void msgDeviceConnect_Po(String deviceMac, String deviceType) {
        deviceMap.put(deviceMac, deviceType);
        refresh();
    }

    @Override
    public void msgDeviceDisconnect_Po(String deviceMac, String deviceType) {
        deviceMap.remove(deviceMac);
        refresh();
    }

    @Override
    public void msgUserStatus(int status) {
        // TODO Auto-generated method stub
        if (status == 1 || status == 2 || status == 3 || status == 4) {
            Message message = new Message();
            message.what = 3;
            handler.sendMessage(message);

            if (mtimer != null) {
                mtimer.cancel();
                mtimer = null;
            }
            bg1Control.start_new();// 开始接收音頻

            mtimer = new Timer();
            mTimerTask = new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    new Thread() {
                        public void run() {
                            connectDevice();
                        }
                    }.start();
                }
            };
            mtimer.schedule(mTimerTask, 3500);

        } else {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("Status", status);
            message.what = 4;
            message.obj = bundle;
            handler.sendMessage(message);
        }
    }

    @Override
    public void msgBGError(int num) {
        // TODO Auto-generated method stub
        Message message = new Message();
        message.what = 7;
        Bundle bundle = new Bundle();
        bundle.putString("result", "error:" + num);
        message.obj = bundle;
        handler.sendMessage(message);
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
    public void msgBGResult(int result) {
        // TODO Auto-generated method stub
        Message message = new Message();
        message.what = 7;
        Bundle bundle = new Bundle();
        bundle.putString("result", "result : " + result);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgBGPowerOff() {
        // TODO Auto-generated method stub
        Message message = new Message();
        message.what = 7;
        Bundle bundle = new Bundle();
        bundle.putString("result", "bg1 poweroff");
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgDeviceReady_new() {
        // TODO Auto-generated method stub
        if (mtimer != null) {
            mtimer.cancel();
            mtimer = null;
        }
        new Thread() {
            public void run() {
                connectDevice_new();
            }
        }.start();
    }

    private void connectDevice() {
        Log.e("", bg1Control.getBottleInfoByErWeiMa(qr));

        bg1Control.cancel();
        bg1Control.start_old();
        SystemClock.sleep(100);
        if (bg1Control.identify(qr)) {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        } else {
            Message message = new Message();
            message.what = 2;
            handler.sendMessage(message);
        }
    }

    private void connectDevice_new() {

        Log.e("", bg1Control.getBottleInfoByErWeiMa(qr));

        if (bg1Control.identify_new(qr, BottleId)) {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        } else {
            Message message = new Message();
            message.what = 2;
            handler.sendMessage(message);
        }

    }

    @Override
    public void msgDeviceConnect_ABI(String deviceMac, String deviceType, int arg) {
        // TODO Auto-generated method stub
        if (arg == ABICommManager.UPPERLIMB_DEVICE) {
            deviceMap.put(deviceMac, deviceType + " UPPERLIMB");
        } else if (arg == ABICommManager.LOWERLIMB_DEVICE) {
            deviceMap.put(deviceMac, deviceType + " LOWERLIMB");
        }
        refresh();
    }

    @Override
    public void msgDeviceDisconnect_ABI(String deviceMac, String deviceType, int arg) {
        // TODO Auto-generated method stub
        refresh();
    }

    private void setLoginView() {
        progressView.setText("Hello guest!");
        login.setText("Login");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
    }

    private void setLogoutView() {
        progressView.setText("Hello " + credentials.getUsername() + "!");
        login.setText("Logout");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                credentials.resetCredentials();
                setLoginView();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            setLogoutView();
        }
    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
    }
}
