
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class Po3Activity extends Activity implements JiuanPO3Observer {
    private PO3Control poControl;
    private DeviceManager deviceManager;
    private String mAddress;
    private Stream mainStream;
    private Stream spo2Stream;
    private Stream pulseStream;
    private Stream perfStream;
    private Connection connection;
    private EventsCallback eventsCallback;
    private StreamsCallback streamsCallback;
    private Credentials credentials;
    private TextView spo2View;
    private TextView pulseView;
    private TextView perfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po3);

        spo2View = (TextView) findViewById(R.id.spo2_po3);
        pulseView = (TextView) findViewById(R.id.pulse_po3);
        perfView = (TextView) findViewById(R.id.perf_po3);

        // get control by device mac
        deviceManager = DeviceManager.getInstance();
        mAddress = getIntent().getStringExtra("mac");
        if (deviceManager.getAmDevice(mAddress) instanceof PO3Control) {
            poControl = (PO3Control) deviceManager.getPoDevice(mAddress);
        }

        if (poControl != null) {
            poControl.addObserver(this);
        }

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


        credentials = new Credentials(this);
        if(credentials.hasCredentials()) {
            // Initiate the connection to Pryv, providing handler which will update UI
            setCallbacks();
            connection = new Connection(Po3Activity.this, credentials.getUsername(), credentials.getToken(), LoginActivity.DOMAIN, true, new DBinitCallback());
            Filter scope = new Filter();
            spo2Stream = new Stream("oximeter_spo2", "Blood oxygen %");
            pulseStream = new Stream("oximeter_pulse", "Pulse Rate");
            perfStream = new Stream("oximeter_perf", "Perfusion Index");
            mainStream = new Stream("oximeter", "Oximeter");
            mainStream.addChildStream(spo2Stream);
            mainStream.addChildStream(perfStream);
            mainStream.addChildStream(pulseStream);
            scope.addStream(mainStream);
            connection.setupCacheScope(scope);
            connection.streams.create(mainStream, streamsCallback);
            connection.streams.create(spo2Stream, streamsCallback);
            connection.streams.create(pulseStream, streamsCallback);
            connection.streams.create(perfStream, streamsCallback);
        }
    }

    // Oximeter possible action
    // poControl.getBattery();
    // poControl.syncHistoryDatas();

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int userStatus = ((Bundle) (msg.obj)).getInt("status");
                    Toast.makeText(Po3Activity.this,"Auth status: " + userStatus, Toast.LENGTH_SHORT);
                    if(userStatus==2) {
                        poControl.startRealTime();
                    }
                    break;
                case 1:
                    String real = ((Bundle) (msg.obj)).getString("real");
                    try {
                        JSONObject data = new JSONObject(real);
                        JSONArray array = data.getJSONArray("Data");
                        JSONObject object = array.getJSONObject(0);
                        spo2View.setText(object.getString("bloodOxygen"));
                        pulseView.setText(object.getString("pulseRate"));
                        perfView.setText(object.getString("PI"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void msgUserStatus(int status) {
        Log.i("act", "user status: " + status);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        msg.what = 0;
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    @Override
    public void msgBattery(int battery) {
    }

    @Override
    public void msgHistroyData(String historyData) {
    }

    @Override
    public void msgRealtimeData(String realData) {
        Log.i("act", "real data: " + realData);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("real", realData);
        msg.what = 1;
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    @Override
    public void msgResultData(String result) {
    }

    public void sendToPryv(View v) {
        if(connection!=null && credentials.hasCredentials()) {
            double time = System.currentTimeMillis()/1000;
            Event o2 = new Event(spo2Stream.getId(),"ratio/percent",spo2View.getText().toString());
            Event pulse = new Event(pulseStream.getId(),"frequency/bpm",pulseView.getText().toString());
            Event perf = new Event(perfStream.getId(),"count/generic",perfView.getText().toString());
            o2.setTime(time);
            pulse.setTime(time);
            perf.setTime(time);
            connection.events.create(o2, eventsCallback);
            connection.events.create(pulse, eventsCallback);
            connection.events.create(perf, eventsCallback);
        }
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
    }
}
