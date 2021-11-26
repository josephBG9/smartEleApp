package kr.ac.ut.smartelevator.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.ac.ut.smartelevator.app.R;
import kr.ac.ut.smartelevator.common.HandlerCallback;
import kr.ac.ut.smartelevator.restapi.RestApiMgr;
import kr.ac.ut.smartelevator.sock.SockClient;

public class MaintenanceItemActivity extends AppCompatActivity implements Handler.Callback{

    ExecutorService executorService;
    Handler handler;
    int elevator_id;
    String maintenance_date;

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


        elevator_id = intent.getIntExtra("lift_id",0);

        TextView tvna = (TextView) findViewById(R.id.ele_name_id);
        tvna.setText(intent.getStringExtra("lift_name") + "(" + elevator_id + ")");

        Date at =new Date();
        SimpleDateFormat datestr = new SimpleDateFormat("yyyy-MM-dd");

        maintenance_date = datestr.format(at).toString();

        TextView tvdate = (TextView) findViewById(R.id.maintenance_date);
        tvdate.setText(maintenance_date);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if(msg.what == HandlerCallback.ELEVATOR_ERR_CODE) {
            /*
                승강기 오류 코드를 읽은 후 호출되는 callback method.
                msg.obj에 JSONObject 형식으로 저장되어 있음.
             */
            JSONObject jsonObject = (JSONObject)msg.obj;
            //JSONArray jsonArray = new JSONArray();

            //jsonArray.put(jsonObject);

            try {
                jsonObject.put("lift_status","비정상");
                //jsonObject.put("err_id", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Log.i("ELEVATOR", "ID : " + jsonObject.getInt("lift_id"));
                Log.i("ELEVATOR", "Errors : " + jsonObject.getJSONArray("lift_errors").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RestApiMgr api = new RestApiMgr(handler, getString(R.string.urlBase) );
            //api.getFromApiServer("afterdate/?date=" + date);
            api.putToApiServer("POST","posterr/", jsonObject);

            Log.i("ELEVATOR", "After Selection : " + jsonObject.toString());

        }
        else if(msg.what == HandlerCallback.PUT_OK) {
            Log.i("ELEVATOR", "> > > > > Error Code is successfully Stored.");
        }
        else {
            Log.i("ELEVATOR", "> > > > > Error Code is unsuccessfully Stored.(ERROR)");
        }
        return false;
    }

    public void btnDone(View view) {
        JSONObject jsonObject = new JSONObject();
        EditText editText = (EditText) findViewById(R.id.editTextTextMultiLine2);
        EditText editName = (EditText) findViewById(R.id.editName);
        String name = editName.getText().toString();
        String maintenance_statement = editText.getText().toString();

        if(name.length() == 0)
            name = "Anonymous";

        if(maintenance_statement.length() == 0)
            maintenance_statement = "Done";

        maintenance_statement = name + "\n" + maintenance_statement;

        try {
            jsonObject.put("lift_id", elevator_id);
            jsonObject.put("lift_status","정상");
            jsonObject.put("report_date", maintenance_date);
            jsonObject.put("content",maintenance_statement);

            Log.i("ELEVATOR", maintenance_date);

            RestApiMgr api = new RestApiMgr(new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    if(msg.what == HandlerCallback.PUT_OK) {
                        Log.i("ELEVATOR","> > > > > Maintenance Statement is successfully transferred.");
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.statement_good),Toast.LENGTH_LONG).show();
                    }
                    else if(msg.what == HandlerCallback.HTTP_ERROR) {
                        Log.i("ELEVATOR","> > > > > Maintenance Statement is lost.");
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.statement_bad),Toast.LENGTH_LONG).show();
                    }
                    finish();
                    return false;
                }
            }), getString(R.string.urlBase) );
            //api.getFromApiServer("afterdate/?date=" + date);
            api.putToApiServer("POST","postrepo/", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void btnCancle(View view) {
        finish();
    }
}