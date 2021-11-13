package kr.ac.ut.smartelevator.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.ac.ut.smartelevator.restapi.RestApiMgr;
import kr.ac.ut.smartelevator.sock.SockClient;
import kr.ac.ut.smartelevator.ui.ListMainActivity;

import kr.ac.ut.smartelevator.common.HandlerCallback;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private Handler handler;
    private RestApiMgr restApiMgr;
    private SockClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler(this);

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        // client = new SockClient(executorService, handler, this);
        client = new SockClient(executorService, handler);
        //client.getElevatorErrorCode("192.168.5.5", 5000);
        client.getElevatorErrorCode("210.119.145.6", 12345);

        //Intent intent = new Intent(this, ListMainActivity.class);

        //startActivity(intent);

    }
/*
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
*/

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case HandlerCallback.ELEVATOR_ERR_CODE:
                JSONObject obj = (JSONObject)msg.obj;
                Log.i("ELEVATOR : ", obj.toString());
                break;
            case HandlerCallback.GET_OK:
                JSONArray array = (JSONArray)msg.obj;
                Log.i("ELEVATOR : ", array.toString());
                break;
            case HandlerCallback.PUT_OK:
                Log.i("ELEVATOR : ", "Successfully Updated.");
                break;
            case HandlerCallback.HTTP_ERROR:
                Log.i("ELEVATOR : ", "Http Server interaction Error.");
                break;
        }

        return false;
    }
}