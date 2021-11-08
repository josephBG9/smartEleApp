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

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.ac.ut.smartelevator.buffer.BufferMgr;


public class  MainActivity extends AppCompatActivity {

    BufferMgr buffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buffer = new BufferMgr(this);

    }

    public void onClick(View v) {
        Random rand = new Random();
        if(v == findViewById(R.id.button)) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("data", rand.nextInt(100));
                obj.put("type", rand.nextInt(2));
                buffer.saveData(obj.getInt("type"), obj);
                Log.i("ELEVATOR", "json obj : " + obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if(v == findViewById(R.id.button2)) {
            JSONArray array;
            array = buffer.loadData(0);
            Log.i("ELEVATOR","length type 0 : " + array.length());
            for(int i=0; i<array.length(); i++) {
                try {
                    Log.i("ELEVATOR", "data(type 0) : " + ((JSONObject)array.get(i)).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            array = buffer.loadData(1);
            Log.i("ELEVATOR","length type 1 : " + array.length());
            for(int i=0; i<array.length(); i++) {
                try {
                    Log.i("ELEVATOR", "data(type 1) : " + ((JSONObject)array.get(i)).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

    }
}