
package com.example.sdk_demo;

import com.jiuan.android.sdk.device.DeviceManager;
import com.jiuan.android.sdk.po.bluetooth.lpcbt.JiuanPO3Observer;
import com.jiuan.android.sdk.po.bluetooth.lpcbt.PO3Control;
import com.pryv.Connection;
import com.pryv.Filter;
import com.pryv.database.DBinitCallback;
import com.pryv.interfaces.EventsCallback;
import com.pryv.interfaces.GetEventsCallback;
import com.pryv.interfaces.GetStreamsCallback;
import com.pryv.interfaces.StreamsCallback;
import com.pryv.model.Event;
import com.pryv.model.Stream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class Po3Activity extends Activity implements JiuanPO3Observer {
    private PO3Control poControl;

    private Button btn_connect;
    private Button btn_bettary;
    private Button btn_history, btn_realdata;

    private DeviceManager deviceManager;
    private String mAddress;

    private Stream userStream;
    private Stream batteryStream;
    private Stream historyStream;
    private Stream resultStream;
    private Connection connection;
    private EventsCallback eventsCallback;
    private GetEventsCallback getEventsCallback;
    private StreamsCallback streamsCallback;
    private GetStreamsCallback getStreamsCallback;
    private Credentials credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po3);

        // get control by device mac
        deviceManager = DeviceManager.getInstance();
        mAddress = getIntent().getStringExtra("mac");
        if (deviceManager.getAmDevice(mAddress) instanceof PO3Control) {
            poControl = (PO3Control) deviceManager.getPoDevice(mAddress);
        }

        if (poControl != null) {
            poControl.addObserver(this);
        }

        initView();

        credentials = new Credentials(this);
        if(credentials.hasCredentials()) {
            // Initiate the connection to Pryv, providing handler which will update UI
            setCallbacks();
            connection = new Connection(Po3Activity.this, credentials.getUsername(), credentials.getToken(), LoginActivity.DOMAIN, true, new DBinitCallback());
            Filter scope = new Filter();
            userStream = new Stream("oximeter_user", "Oximeter User");
            batteryStream = new Stream("oximeter_battery", "Oximeter Battery");
            historyStream = new Stream("oximeter_history", "Oximeter History");
            resultStream = new Stream("oximeter_result", "Oximeter Result");
            scope.addStream(userStream);
            scope.addStream(batteryStream);
            scope.addStream(historyStream);
            scope.addStream(resultStream);
            connection.setupCacheScope(scope);
            connection.streams.create(userStream, streamsCallback);
            connection.streams.create(batteryStream, streamsCallback);
            connection.streams.create(historyStream, streamsCallback);
            connection.streams.create(resultStream, streamsCallback);
        }
    }

    private void initView() {
        btn_connect = (Button) findViewById(R.id.connect);
        btn_connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /**
                 * userId, the identification of the user, could be the form of email address or
                 * mobile phone number (mobile phone number is not supported temporarily). clientID
                 * and clientSecret, as the identification of the SDK, will be issued after the
                 * iHealth SDK registration. please contact lvjincan@jiuan.com for registration.
                 */
                String userId = "liu01234345555@jiuan.com";
                final String clientID = "2a8387e3f4e94407a3a767a72dfd52ea";
                final String clientSecret = "fd5e845c47944a818bc511fb7edb0a77";
                poControl.connect(Po3Activity.this, userId, clientID, clientSecret);
            }
        });

        btn_bettary = (Button) findViewById(R.id.bettary);
        btn_bettary.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                poControl.getBattery();
            }
        });
        btn_history = (Button) findViewById(R.id.history);
        btn_history.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                poControl.syncHistoryDatas();
            }
        });
        btn_realdata = (Button) findViewById(R.id.realdata);
        btn_realdata.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                poControl.startRealTime();
            }
        });

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            boolean connectedToPryv = (connection!=null && credentials.hasCredentials());
            switch (msg.what) {
                case 0:
                    int userStatus = ((Bundle) (msg.obj)).getInt("status");
                    Toast.makeText(getApplicationContext(), "PO3 UserStatus=" + userStatus, Toast.LENGTH_SHORT).show();
                    if(connectedToPryv) {
                        connection.events.create(new Event(userStream.getId(), "note/txt", ""+userStatus), eventsCallback);
                    }
                    break;
                case 1:
                    int battery = ((Bundle) (msg.obj)).getInt("battery");
                    Toast.makeText(getApplicationContext(), "PO3 battery=" + battery, Toast.LENGTH_SHORT).show();
                    if(connectedToPryv) {
                        connection.events.create(new Event(batteryStream.getId(), "note/txt", ""+battery), eventsCallback);
                    }
                    break;
                case 2:
                    String historyData = ((Bundle) (msg.obj)).getString("historyData");
                    Toast.makeText(getApplicationContext(), "PO3 historyData=" + historyData, Toast.LENGTH_SHORT)
                            .show();
                    if(connectedToPryv) {
                        connection.events.create(new Event(historyStream.getId(), "note/txt", historyData), eventsCallback);
                    }                    break;
                case 3:
                    String result = ((Bundle) (msg.obj)).getString("result");
                    Toast.makeText(getApplicationContext(), "PO3 result=" + result, Toast.LENGTH_SHORT).show();
                    if(connectedToPryv) {
                        connection.events.create(new Event(resultStream.getId(), "note/txt", result), eventsCallback);
                    }                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), "no historyData", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void msgUserStatus(int status) {
        // TODO Auto-generated method stub
        Log.i("act", "user status" + status);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        msg.what = 0;
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    @Override
    public void msgBattery(int battery) {
        // TODO Auto-generated method stub
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("battery", battery);
        msg.what = 1;
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    @Override
    public void msgHistroyData(String historyData) {
        // TODO Auto-generated method stub
        if (historyData == null) {
            Log.d("history", "no historyData");
            handler.sendEmptyMessage(4);
            /**
             * There is no historical data
             */
        } else {
            Log.i("history", historyData);
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("historyData", historyData);
            msg.what = 2;
            msg.obj = bundle;
            handler.sendMessage(msg);
        }

    }

    @Override
    public void msgRealtimeData(String realData) {
        // TODO Auto-generated method stub
        Log.i("real", realData);
    }

    @Override
    public void msgResultData(String result) {
        // TODO Auto-generated method stub
        Log.i("end", result);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        msg.what = 3;
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    /**
     * Initiate custom callbacks
     */
    private void setCallbacks() {

        //Called when actions related to events creation/modification complete
        eventsCallback = new EventsCallback() {

            @Override
            public void onApiSuccess(String s, Event event, String s1, Double aDouble) {
                Log.i("Pryv", s);
            }

            @Override
            public void onApiError(String s, Double aDouble) {
                Log.e("Pryv", s);
            }

            @Override
            public void onCacheSuccess(String s, Event event) {
                Log.i("Pryv", s);
            }

            @Override
            public void onCacheError(String s) {
                Log.e("Pryv", s);
            }
        };

        //Called when actions related to streams creation/modification complete
        streamsCallback = new StreamsCallback() {

            @Override
            public void onApiSuccess(String s, Stream stream, Double aDouble) {
                Log.i("Pryv", s);
            }

            @Override
            public void onApiError(String s, Double aDouble) {
                Log.e("Pryv", s);
            }

            @Override
            public void onCacheSuccess(String s, Stream stream) {
                Log.i("Pryv", s);
            }

            @Override
            public void onCacheError(String s) {
                Log.e("Pryv", s);
            }

        };

        //Called when actions related to events retrieval complete
        getEventsCallback = new GetEventsCallback() {
            @Override
            public void cacheCallback(List<Event> list, Map<String, Double> map) {
                Log.i("Pryv", list.size() + " events retrieved from cache.");
            }

            @Override
            public void onCacheError(String s) {
                Log.e("Pryv", s);
            }

            @Override
            public void apiCallback(List<Event> list, Map<String, Double> map, Double aDouble) {
                Log.i("Pryv", list.size() + " events retrieved from API.");
            }

            @Override
            public void onApiError(String s, Double aDouble) {
                Log.e("Pryv", s);
            }
        };

        //Called when actions related to streams retrieval complete
        getStreamsCallback = new GetStreamsCallback() {

            @Override
            public void cacheCallback(Map<String, Stream> map, Map<String, Double> map1) {
                Log.i("Pryv", map.size() + " streams retrieved from cache.");
            }

            @Override
            public void onCacheError(String s) {
                Log.e("Pryv", s);
            }

            @Override
            public void apiCallback(Map<String, Stream> map, Map<String, Double> map1, Double aDouble) {
                Log.i("Pryv", map.size() + " streams retrieved from API.");
            }

            @Override
            public void onApiError(String s, Double aDouble) {
                Log.e("Pryv", s);
            }
        };

    }
}
