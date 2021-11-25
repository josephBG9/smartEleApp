package kr.ac.ut.smartelevator.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import kr.ac.ut.smartelevator.app.R;
import kr.ac.ut.smartelevator.sock.SockClient;

public class MaintenanceItemActivity extends AppCompatActivity implements Handler.Callback{

    ExecutorService executorService;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_item);

        Intent intent = getIntent();

        executorService = ExecutorService.
        SockClient sockClient = new SockClient();



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
        return false;
    }
}