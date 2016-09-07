package com.example.sdk_demo;

import com.jiuan.android.sdk.abi.bluetooth.ABICommManager;
import com.jiuan.android.sdk.abi.bluetooth.ABIControl;
import com.jiuan.android.sdk.abi.bluetooth.DeviceFunction;
import com.jiuan.android.sdk.abi.observer.bluetooth.Interface_Observer_BluetoothAbi;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ABI_Activity extends Activity implements Interface_Observer_BluetoothAbi{
    private Button measure;
    private Button upperlimb_Measure;
    private Button battery;
    private Button function;
    private Button stopMeasure;
    private Button deviceInfo;
    private Button interrupt;
    private TextView Tv_UpperResult;
    private TextView Tv_LowerResult;
    private TextView Tv_UpperInfo;
    private TextView Tv_LowerInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.i("TAG", "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_abi);
        
        Tv_UpperResult = (TextView)findViewById(R.id.upper_device_result);
        Tv_LowerResult = (TextView)findViewById(R.id.lower_device_result);
        Tv_UpperInfo = (TextView)findViewById(R.id.upper_device_info);
        Tv_LowerInfo = (TextView)findViewById(R.id.lower_device_info);
        
        measure = (Button)findViewById(R.id.measure);
        measure.setOnClickListener(measureOnClick);
        upperlimb_Measure = (Button)findViewById(R.id.upperlimb_measure);
        upperlimb_Measure.setOnClickListener(upperlimbMeasureOnClick);
        battery = (Button)findViewById(R.id.battery);
        battery.setOnClickListener(batteryOnClick);
        function = (Button)findViewById(R.id.function);
        function.setOnClickListener(functionOnClick);
        stopMeasure = (Button)findViewById(R.id.stop);
        stopMeasure.setOnClickListener(stopOnClick);
        deviceInfo = (Button)findViewById(R.id.device_info);
        deviceInfo.setOnClickListener(deviceInfoOnClick);
        interrupt = (Button)findViewById(R.id.interrupt);
        interrupt.setOnClickListener(interruptOnClick);
        ABICommManager.abiControl.attach(this);
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(ABICommManager.abiControl.getDeviceState() == 0){
            ShowResultAsyncTask asyncTask1 = new ShowResultAsyncTask(Tv_UpperInfo, false);
            asyncTask1.execute("未连接");
            ShowResultAsyncTask asyncTask2 = new ShowResultAsyncTask(Tv_LowerInfo, false);
            asyncTask2.execute("已连接");
        }else if(ABICommManager.abiControl.getDeviceState() == 1){
            ShowResultAsyncTask asyncTask1 = new ShowResultAsyncTask(Tv_UpperInfo, false);
            asyncTask1.execute("已连接");
            ShowResultAsyncTask asyncTask2 = new ShowResultAsyncTask(Tv_LowerInfo, false);
            asyncTask2.execute("未连接");
        } else if(ABICommManager.abiControl.getDeviceState() > 1){
            ShowResultAsyncTask asyncTask1 = new ShowResultAsyncTask(Tv_UpperInfo, false);
            asyncTask1.execute("已连接");
            ShowResultAsyncTask asyncTask2 = new ShowResultAsyncTask(Tv_LowerInfo, false);
            asyncTask2.execute("已连接");
        }
    }
    
    
    private View.OnClickListener measureOnClick = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Tv_UpperResult.setText("");
            Tv_LowerResult.setText("");
            
            String clientID =  "";
            String clientSecret = "";
            ABICommManager.abiControl.startMeasure(clientID, clientSecret);
        }
    };
    
    private View.OnClickListener upperlimbMeasureOnClick = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Tv_UpperResult.setText("");
            Tv_LowerResult.setText("");
            
            String clientID =  "dbbe1fa8b5634ffaa52448c3b70b2abe";
            String clientSecret = "81ad7936c17849168e23586ef9be8f67";
            ABICommManager.abiControl.startMeasure_UpperLimb(clientID, clientSecret, ABIControl.UPPERLIMB_DEVICE);
        }
    };
    
    private View.OnClickListener stopOnClick = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            ABICommManager.abiControl.measureFinish(ABIControl.LOWERLIMB_DEVICE);
            ABICommManager.abiControl.measureFinish(ABIControl.UPPERLIMB_DEVICE);
        }
    };
    
    private View.OnClickListener batteryOnClick = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Tv_UpperInfo.setText("");
            Tv_LowerInfo.setText("");
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String msg1 = "电量:" + ABICommManager.abiControl.getBattery(ABIControl.UPPERLIMB_DEVICE);
                    String msg2 = "电量:" + ABICommManager.abiControl.getBattery(ABIControl.LOWERLIMB_DEVICE);
                    ShowResultAsyncTask asyncTask1 = new ShowResultAsyncTask(Tv_UpperInfo, false);
                    ShowResultAsyncTask asyncTask2 = new ShowResultAsyncTask(Tv_LowerInfo, false);
                    asyncTask1.execute(msg1);
                    asyncTask2.execute(msg2);
                }
            }).start();
        }
    };
    
    private View.OnClickListener functionOnClick = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Tv_UpperResult.setText("");
            Tv_LowerResult.setText("");
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    StringBuffer msg1 = new StringBuffer();
                    StringBuffer msg2 = new StringBuffer();
                    DeviceFunction upperDeviceFunction = ABICommManager.abiControl.getDeviceFunction(ABIControl.UPPERLIMB_DEVICE);
                    DeviceFunction lowerDeviceFunction = ABICommManager.abiControl.getDeviceFunction(ABIControl.LOWERLIMB_DEVICE);
                    msg1.append("有无离线测量设置:" + (upperDeviceFunction.isoffLinedata?"有":"无") + "\n");
                    msg1.append("离线测量开关状态:" + (upperDeviceFunction.offLinedata?"开":"关") + "\n");
                    msg1.append("有无自动回连设置:" + (upperDeviceFunction.isbacktoConnect?"有":"无") + "\n");
                    msg1.append("自动回连开关状态:" + (upperDeviceFunction.backtoConnect?"开":"关") + "\n");
                    msg2.append("有无离线测量设置:" + (lowerDeviceFunction.isoffLinedata?"有":"无") + "\n");
                    msg2.append("离线测量开关状态:" + (lowerDeviceFunction.offLinedata?"开":"关") + "\n");
                    msg2.append("有无自动回连设置:" + (lowerDeviceFunction.isbacktoConnect?"有":"无") + "\n");
                    msg2.append("自动回连开关状态:" + (lowerDeviceFunction.backtoConnect?"开":"关") + "\n");
                    ShowResultAsyncTask asyncTask1 = new ShowResultAsyncTask(Tv_UpperInfo, false);
                    ShowResultAsyncTask asyncTask2 = new ShowResultAsyncTask(Tv_LowerInfo, false);
                    asyncTask1.execute(msg1.toString());
                    asyncTask2.execute(msg2.toString());
                }
            }).start();
        }
    };
    
    private View.OnClickListener deviceInfoOnClick = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//            bpControl.FunctionInfo(3);
//            StringBuffer msg = new StringBuffer();
//            msg.append("ABI功能机型:" + (bpControl.isabiDevice?("是"+ "\n" + (bpControl.isUpperLimb?"上肢设备":"下肢设备")):"否") + "\n");
//            Toast.makeText(ABI_Activity.this, msg, Toast.LENGTH_LONG).show();
        }
    };
    
    private View.OnClickListener interruptOnClick = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            ABICommManager.abiControl.interruptMeasure();
        }
    };
    
    @Override
    public void msgInden() {
        // TODO Auto-generated method stub
        Log.i("TAG", "msgInden");
    }

    @Override
    public void msgUserStatus(int status) {
        // TODO Auto-generated method stub
        Log.i("TAG", "msgUserStatus " + status);
    }

    @Override
    public void msgBattery(int arg, int battery) {
        // TODO Auto-generated method stub
        if(arg == ABIControl.UPPERLIMB_DEVICE){
            ShowResultAsyncTask asyncTask = new ShowResultAsyncTask(Tv_UpperInfo, false);
            asyncTask.execute("电量:" + battery);
        } else {
            ShowResultAsyncTask asyncTask = new ShowResultAsyncTask(Tv_LowerInfo, false);
            asyncTask.execute("电量:" + battery);
        }
    }

    @Override
    public void msgError(int arg, int num) {
        // TODO Auto-generated method stub
        Log.i("TAG", arg + " error " + num);
        if(arg == ABIControl.UPPERLIMB_DEVICE){
            ShowResultAsyncTask asyncTask = new ShowResultAsyncTask(Tv_UpperInfo, false);
            asyncTask.execute("Error:" + num);
        } else {
            ShowResultAsyncTask asyncTask = new ShowResultAsyncTask(Tv_LowerInfo, false);
            asyncTask.execute("Error:" + num);
        }
    }

    @Override
    public void msgAngle(int arg, int angle) {
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
    public void msgPressure(int arg, int pressure) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void msgMeasure(int arg, int pressure, int[] measure, boolean heart) {
        // TODO Auto-generated method stub
//        Log.i("TAG", "device:" + arg + "," +pressure+ "," + measure +"," + heart);
    }

    @Override
    public void msgResult(int arg, int[] result) {
        // TODO Auto-generated method stub
        Log.i("TAG", arg + " result " + result[0] + "," + result[1] + "," + result[2]);
        StringBuffer msg = new StringBuffer();
        if(arg == ABIControl.UPPERLIMB_DEVICE){
            msg.append("SYS:" + (result[0] + result[1]) + "\n");
            msg.append("DIA:" + result[1] + "\n");
            msg.append("Pulse:" + result[2] + "\n");
            ShowResultAsyncTask asyncTask = new ShowResultAsyncTask(Tv_UpperResult, false);
            asyncTask.execute(msg.toString());
        } else {
            msg.append("SYS:" + (result[0] + result[1]) + "\n");
            msg.append("DIA:" + result[1] + "\n");
            msg.append("Pulse:" + result[2] + "\n");
            ShowResultAsyncTask asyncTask = new ShowResultAsyncTask(Tv_LowerResult, false);
            asyncTask.execute(msg.toString());
        }
    }

    @Override
    public void msgPowerOff(int arg) {
        // TODO Auto-generated method stub
        
    }
    
    public static class ShowResultAsyncTask extends AsyncTask<String, Integer, String>{

        private TextView textView;
        private boolean appendText;
        
        public ShowResultAsyncTask(TextView textView,boolean bool) {
            // TODO Auto-generated constructor stub
            this.textView = textView;
            this.appendText = bool;
        }
        
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if(appendText){
                textView.append(result);
            } else {
                textView.setText(result);
            }
        }
        
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
        }


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return params[0];
        }
    }
}
