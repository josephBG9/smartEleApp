package kr.ac.ut.smartelevator.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.ac.ut.smartelevator.common.HandlerCallback;
import kr.ac.ut.smartelevator.db.ElevatorInfoDB;
import kr.ac.ut.smartelevator.restapi.RestApiMgr;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private Handler handler;
    ElevatorInfoDB db;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getPreferences(Context.MODE_PRIVATE);
        String date = preferences.getString("update_date","2021-09-01");

        Log.i("ELEVATOR", "DATE : " + date);

        handler = new Handler(this);
        db = new ElevatorInfoDB(this);

        //RestApiMgr api = new RestApiMgr(handler, "http://boas.asuscomm.com:10002/" );
        RestApiMgr api = new RestApiMgr(handler, getString(R.string.urlBase) );
        api.getFromApiServer("afterdate/?date=" + date);

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case HandlerCallback.GET_OK:
                JSONArray array = null;
                try {
                    array = (((JSONArray)msg.obj).getJSONObject(0)).getJSONArray("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("ELEVATOR : ", "Length : " + String.valueOf(array.length()) +
                        "\tContents : " + array.toString());

                if(array.length() > 0)
                    db.insertElevatorInfo(array);

                SharedPreferences.Editor editor = preferences.edit();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                editor.putString("update_date",currentDate);
                editor.commit();

                JSONArray dbdata = db.getElevatorInfo();
                Log.i("ELEVATOR : ", "From DB Contents : " + dbdata.toString());

                break;
            case HandlerCallback.PUT_OK:
                Log.i("ELEVATOR : ", "Successfully Updated.");
                break;
            case HandlerCallback.HTTP_ERROR:
                Log.i("ELEVATOR : ", "Http Server interaction Error.");
                break;
        }

        return true;
    }
}