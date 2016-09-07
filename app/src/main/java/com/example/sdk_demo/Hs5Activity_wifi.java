
package com.example.sdk_demo;

import com.jiuan.android.sdk.hs.observer_hs5.Interface_Observer_HS5WiFi;
import com.jiuan.android.sdk.hs.wifi.HS5WiFiControl;
import com.jiuan.android.sdk.hs.wifi.WiFiCommManager;

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

public class Hs5Activity_wifi extends Activity implements Interface_Observer_HS5WiFi {

    private String TAG = "Hs5Activity_wifi";
    private HS5WiFiControl myControl = null;

    private Button btn_connect;
    private Button btn_addUser, btn_updateUser, btn_deleteUser;
    private Button btn_offline;
    private Button btn_online;
    private Button btn_cloud;
    private Button button_disconnect;
    private TextView tv_msg;
    // -------
    private String userName = "";
    String clientID = "";
    String clientSecret = "";

    // -------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs5_wifi);

        initView();

        myControl = null;
        getControl();
        if (myControl == null) {
            btn_connect.setVisibility(View.INVISIBLE);
        } else {
            btn_connect.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        btn_connect = (Button) findViewById(R.id.button_connect);
        btn_connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (myControl != null) {
                    new Thread() {
                        public void run() {
                            myControl.connect(Hs5Activity_wifi.this, userName, clientID, clientSecret);
                        }
                    }.start();
                }
            }
        });
        btn_addUser = (Button) findViewById(R.id.button_add);
        btn_addUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread() {
                    public void run() {
                        boolean add = myControl.addUserToScale(28, 174, 0, 1);
                        Message message = new Message();
                        message.what = 6;
                        Bundle bundle = new Bundle();
                        bundle.putString("result", "add user " + add);
                        message.obj = bundle;
                        handler.sendMessage(message);
                    }
                }.start();
            }
        });
        btn_updateUser = (Button) findViewById(R.id.button_update);
        btn_updateUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread() {
                    public void run() {
                        boolean update = myControl.updateUserInScale(28, 174, 0, 1);
                        Message message = new Message();
                        message.what = 6;
                        Bundle bundle = new Bundle();
                        bundle.putString("result", "update user " + update);
                        message.obj = bundle;
                        handler.sendMessage(message);
                    }
                }.start();
            }
        });
        btn_deleteUser = (Button) findViewById(R.id.button_delete);
        btn_deleteUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread() {
                    public void run() {
                        boolean delete = myControl.deleteUserInScale();
                        Message message = new Message();
                        message.what = 6;
                        Bundle bundle = new Bundle();
                        bundle.putString("result", "delete user " + delete);
                        message.obj = bundle;
                        handler.sendMessage(message);
                    }
                }.start();
            }
        });
        btn_offline = (Button) findViewById(R.id.button_offline);
        btn_offline.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (myControl != null) {
                    new Thread() {
                        public void run() {
                            myControl.uploadOfflineData();
                        }
                    }.start();
                }
            }
        });
        btn_online = (Button) findViewById(R.id.button_online);
        btn_online.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (myControl != null) {
                    new Thread() {
                        public void run() {
                            myControl.startMeasure();
                        }
                    }.start();
                }
            }
        });
        btn_cloud = (Button) findViewById(R.id.button_cloud);
        btn_cloud.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread() {
                    public void run() {
                        String cloudData = myControl.getCloudData();
                        Message message = new Message();
                        message.what = 7;
                        Bundle bundle = new Bundle();
                        bundle.putString("cloud", cloudData);
                        message.obj = bundle;
                        handler.sendMessage(message);
                    }
                }.start();
            }
        });
        button_disconnect = (Button) findViewById(R.id.button_disconnect);
        button_disconnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Thread() {
                    public void run() {
                        myControl.CloseConnection();
                    }
                }.start();
            }
        });
        tv_msg = (TextView) findViewById(R.id.textview_msg_hs5);
    }

    private void getControl() {
        Intent intent = getIntent();
        String mac = intent.getStringExtra("mac");
        Log.i(TAG, mac);
        // HS5-－Wi-Fi
        myControl = WiFiCommManager.getHs5Device(mac);
        myControl.hs5Subject.attach(this);
    }

    @Override
    public void msgUserStatus(int status) {
        // TODO Auto-generated method stub
        Log.i(TAG, "user status " + status);
        Message message = new Message();
        message.what = 0;
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgConnectStatus(int connection) {
        // TODO Auto-generated method stub
        Log.i(TAG, "connect status " + connection);
        if (connection == 1) {
            Log.i(TAG, "position " + myControl.getUserInList());
        }
        Message message = new Message();
        message.what = 1;
        Bundle bundle = new Bundle();
        bundle.putInt("connect", connection);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgRealWeight(float realWeight) {
        // TODO Auto-generated method stub
        Log.i(TAG, "realWeight " + realWeight);
        Message message = new Message();
        message.what = 2;
        Bundle bundle = new Bundle();
        bundle.putFloat("realweight", realWeight);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgStabWeight(float stabWeight) {
        // TODO Auto-generated method stub
        Log.i(TAG, "stabWeight " + stabWeight);
        Message message = new Message();
        message.what = 3;
        Bundle bundle = new Bundle();
        bundle.putFloat("stabweight", stabWeight);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgResult(String result) {
        // TODO Auto-generated method stub
        Log.i(TAG, "result " + result);
        Message message = new Message();
        message.what = 4;
        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    public void msgOfflineData(String offlineData) {
        // TODO Auto-generated method stub
        Log.i(TAG, "offline data " + offlineData);
        Message message = new Message();
        message.what = 5;
        Bundle bundle = new Bundle();
        bundle.putString("offlineData", offlineData);
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
                case 0:
                    Bundle bundle0 = (Bundle) msg.obj;
                    int status = bundle0.getInt("status");
                    String usercheckResualt = userCheckResult(status);

                    tv_msg.setText("user status：" + usercheckResualt);
                    break;
                case 1:
                    Bundle bundle1 = (Bundle) msg.obj;
                    int connection = bundle1.getInt("connect");
                    String resualtString = checkUserInScaleResualt(connection);
                    tv_msg.setText("connection：" + resualtString);
                    break;
                case 2:
                    Bundle bundle2 = (Bundle) msg.obj;
                    float realWeight = bundle2.getFloat("realweight");
                    tv_msg.setText("realWeight：" + realWeight);
                    break;
                case 3:
                    Bundle bundle3 = (Bundle) msg.obj;
                    float stabWeight = bundle3.getFloat("stabweight");
                    tv_msg.setText("stabweight：" + stabWeight);
                    break;
                case 4:
                    Bundle bundle4 = (Bundle) msg.obj;
                    String result = bundle4.getString("result");
                    tv_msg.setText("result：" + result);
                    break;
                case 5:
                    Bundle bundle5 = (Bundle) msg.obj;
                    String data = bundle5.getString("offlineData");
                    tv_msg.setText("offlineData：" + data);
                    break;
                case 6:
                    Bundle bundle6 = (Bundle) msg.obj;
                    String res = bundle6.getString("result");
                    tv_msg.setText(res);
                    break;
                case 7:
                    Bundle bundle7 = (Bundle) msg.obj;
                    String cloud = bundle7.getString("cloud");
                    tv_msg.setText("cloud data " + cloud);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @Title: userCheckResult
     * @author:gaonana
     * @Description: TODO
     * @param @param status : 1 register success; 2 login success; 3 iHealth user add SDK measure
     *        fuction and measure data by SDK also belongs to this user; 4 Network abnormal just for
     *        test; 5 userid or clientID or clientSecret Validation fails ;6 Application has no such
     *        permission;7 user has no such permission;8 Authentication failed for network anomaly
     *        ;33 not iHealth user
     * @param @return
     * @return String
     * @throws
     */
    private String userCheckResult(int status) {
        String userCheckResult = "";

        switch (status) {
            case 1:
                userCheckResult = "register success";
                break;
            case 2:
                userCheckResult = "login success";

                break;
            case 3:
                userCheckResult = "iHealth user";

                break;
            case 4:
                userCheckResult = "Network abnormal just for test";

                break;
            case 5:
                userCheckResult = "userid or clientID or clientSecret Validation fails";

                break;
            case 6:
                userCheckResult = "Application has no such permission";

                break;
            case 7:
                userCheckResult = "user has no such permission";

                break;
            case 8:
                userCheckResult = "Authentication failed for network anomaly";

                break;
            case 33:
                userCheckResult = "not iHealth user";

                break;
            default:
                break;
        }
        return userCheckResult;
    }

    /*
     * status:1 user in scale, you can update user infomation to scale; 2 user isn't in scale and
     * amount of users in scale isn't full ,you need add this user to scale ; 3 user isn't in scale
     * but amount of users in scale is full ,you need delete some users and add this user to scale
     */
    private String checkUserInScaleResualt(int status) {
        String resualtString = "" + status;
        switch (status) {
            case 1:
                resualtString = "this user in scale";
                break;
            case 2:
                resualtString = "this user not in scale need add";

                break;
            case 3:
                resualtString = "this user not in scale need delete and then add";

                break;
            default:
                break;
        }
        return resualtString;
    }

}
