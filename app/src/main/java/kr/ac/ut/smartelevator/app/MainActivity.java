package kr.ac.ut.smartelevator.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.ac.ut.smartelevator.restapi.HandlerCallback;
import kr.ac.ut.smartelevator.restapi.RestApiMgr;

public class MainActivity extends AppCompatActivity implements HandlerCallback {

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = HandlerCompat.createAsync(Looper.getMainLooper());
        RestApiMgr api = new RestApiMgr(handler, "http://boas.asuscomm.com:11001/", this);
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
            case RestApiMgr.GET_OK:
                JSONArray array = (JSONArray)msg.obj;
                Log.i("API : ", array.toString());
                break;
            case RestApiMgr.PUT_OK:
                Log.i("API : ", "Successfully Updated.");
                break;
            case RestApiMgr.HTTP_ERROR:
                Log.i("API : ", "Http Server interaction Error.");
                break;
        }
    }
}