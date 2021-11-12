package kr.ac.ut.smartelevator.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.CarrierConfigManager;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.ac.ut.smartelevator.common.HandlerCallback;
import kr.ac.ut.smartelevator.sock.SockClient;

public class  MainActivity extends AppCompatActivity implements HandlerCallback {
    SockClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        Log.i("ELEVATOR","App is started.");

        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        client = new SockClient(executorService, handler, this);
        //client.getElevatorErrorCode("192.168.5.5", 5000);
        client.getElevatorErrorCode("210.119.145.6", 12345);
/*
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        for(Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.i("ELEVATOR","Mobile Netork Type : binding process to this network");
                connMgr.bindProcessToNetwork(network);
            }
        } */





    }

    @Override
    public void handleMessage(Message msg) {
        Log.i("ELEVATOR","Handler.....");
        if(msg.what == HandlerCallback.ELEVATOR_ERR_CODE) {
            JSONObject obj = (JSONObject)msg.obj;
            try {
                JSONArray erlist = obj.getJSONArray("lift_err");
                Log.i("ELEVATOR","N of Error Code in Array : " + erlist.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("ELEVATOR", ((JSONObject)msg.obj).toString());
        }

    }

    public void onClick(View v) {
        //client.getElevatorErrorCode("192.168.5.5", 5000);
        client.getElevatorErrorCode("210.119.145.6", 12345);
    }
}