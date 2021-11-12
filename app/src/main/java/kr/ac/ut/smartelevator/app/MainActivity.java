package kr.ac.ut.smartelevator.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.ac.ut.smartelevator.utils.JSONMgr;
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


import kr.ac.ut.smartelevator.common.HandlerCallback;
import kr.ac.ut.smartelevator.restapi.RestApiMgr;

    BufferMgr buffer;
public class MainActivity extends AppCompatActivity implements HandlerCallback {

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
 * Testing for JSONMgr class.
 * JSONMgr.toJSON() : HashMap Object to JSON Object
 * JSONMgr.fromJSON() : JSON Object to HashMap Object.
 *
 *
        JSONMgr jsonMgr = new JSONMgr();

        HashMap<String,Object> map = new HashMap<String, Object>();
        JSONObject jsonObject;
        HashMap<String,Object> map2;

        map.put("str","GOOD");
        map.put("int", 123);
        map.put("float", 3.14F);
        map.put("boolean", true);

        Log.i("JSON : ", "Starting LOG");
        for(Map.Entry<String, Object>entry : map.entrySet()) {
            Log.i("JSON TEST : ", entry.getKey() + " ---> " + entry.getValue());
        }

        Log.i("JSON : ", "JSON LOG");
        jsonObject = jsonMgr.toJSON(map);
        String key;
        Iterator<String> itr = jsonObject.keys();
        while(itr.hasNext()) {
            key = itr.next();
            try {
                Log.i("JSON TEST", key + "-->" + jsonObject.get(key));
            } catch(JSONException e) {
                Log.i("JSON", "JSON -> HashMap : error for " + key);
            }
        }

        map2 = jsonMgr.fromJSON(jsonObject);
        Log.i("JSON : ", "Ending LOG");
        for(Map.Entry<String, Object>entry : map2.entrySet()) {
            Log.i("JSON TEST : ", entry.getKey() + " ---> " + entry.getValue());
        }
*/



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


        handler = HandlerCompat.createAsync(Looper.getMainLooper());
        RestApiMgr api = new RestApiMgr(handler, "http://boas.asuscomm.com:10002/", this);
        api.getFromApiServer("liftdetail/2/");

        JSONObject obj = new JSONObject();
        try {
            obj.put("lift_name", "중앙정보관w18 Floor 3");
            obj.put("lift_id", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        api.putToApiServer("liftdetail/2/", obj);
        api.getFromApiServer("liftdetail/2/");

    }

    @Override
    public void handleMessage(Message msg) {
        switch(msg.what) {
            case HandlerCallback.GET_OK:
                JSONArray array = (JSONArray)msg.obj;
                Log.i("API : ", array.toString());
                break;
            case HandlerCallback.PUT_OK:
                Log.i("API : ", "Successfully Updated.");
                break;
            case HandlerCallback.HTTP_ERROR:
                Log.i("API : ", "Http Server interaction Error.");
                break;
        }
    }
}