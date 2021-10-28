package kr.ac.ut.smartelevator.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.ac.ut.smartelevator.common.HandlerCallback;
import kr.ac.ut.smartelevator.sock.SockClient;

public class MainActivity extends AppCompatActivity implements HandlerCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        SockClient client = new SockClient(executorService, handler, this);
        client.getElevatorErrorCode("210.119.145.6", 12345);


    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.what == HandlerCallback.ELEVATOR_ERR_CODE) {
            Log.i("ELEVATOR ERROR ", ((JSONObject)msg.obj).toString());
        }

    }
}