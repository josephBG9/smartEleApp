package kr.ac.ut.smartelevator.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.ut.smartelevator.app.R;

public class MaintenanceItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_item);

        Intent intent = getIntent();

        TextView tvna = (TextView) findViewById(R.id.ele_name_id);
        tvna.setText(intent.getStringExtra("lift_name") + "("
                + intent.getIntExtra("lift_id",0) + ")");

        Date at =new Date();
        SimpleDateFormat datestr = new SimpleDateFormat("yyyy-MM-dd");
        TextView tvdate = (TextView) findViewById(R.id.maintenance_date);
        tvdate.setText(datestr.format(at).toString());



    }
}