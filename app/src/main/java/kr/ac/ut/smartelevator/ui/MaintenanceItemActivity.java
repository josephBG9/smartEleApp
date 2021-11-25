package kr.ac.ut.smartelevator.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.ac.ut.smartelevator.app.R;
import kr.ac.ut.smartelevator.common.HandlerCallback;
import kr.ac.ut.smartelevator.sock.SockClient;

public class MaintenanceItemActivity extends AppCompatActivity implements Handler.Callback{

    ExecutorService executorService;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_item);

        Intent intent = getIntent();

        /*
            승강기 WIFI AccessPoint에서 오류코드를 읽어 옴.
         */
        executorService = Executors.newFixedThreadPool(4);
        handler = new Handler(this);
        SockClient sockClient = new SockClient(executorService, handler);
        sockClient.getElevatorErrorCode(getApplicationContext().getResources().getString(R.string.wifiap_ip),
                Integer.valueOf(getApplicationContext().getResources().getString(R.string.wifiap_port)));


        TextView tvna = (TextView) findViewById(R.id.ele_name_id);
        tvna.setText(intent.getStringExtra("lift_name") + "("
                + intent.getIntExtra("lift_id",0) + ")");

        Date at =new Date();
        SimpleDateFormat datestr = new SimpleDateFormat("yyyy-MM-dd");
        TextView tvdate = (TextView) findViewById(R.id.maintenance_date);
        tvdate.setText(datestr.format(at).toString());



    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if(msg.what == HandlerCallback.ELEVATOR_ERR_CODE) {
            /*
                승강기 오류 코드를 읽은 후 호출되는 callback method.
                msg.obj에 JSONObject 형식으로 저장되어 있음.
             */
            Log.i("ELEVATOR", "After Selection : " + ((JSONObject)msg.obj).toString());
        }
        return false;
    }
}