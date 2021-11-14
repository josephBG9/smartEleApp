package kr.ac.ut.smartelevator.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.ac.ut.smartelevator.common.HandlerCallback;
import kr.ac.ut.smartelevator.db.ElevatorInfoDB;
import kr.ac.ut.smartelevator.restapi.RestApiMgr;
import kr.ac.ut.smartelevator.ui.ListMainActivity;

/*
  2021-11-14:
    앱이 실행될 때 API 서버로 부터 승강기 정보를 읽어야 하므로 스플래시를 넣은 것을
    고려할 필요가 있음.
 */
public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private Handler handler;
    ElevatorInfoDB db;
    SharedPreferences preferences;
    JSONArray eleinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eleinfo = null;

        /*  이 앱이 마지막으로 실행 된 날짜를 기준으로 그 날짜 이후에 API 서버에 등록된
            승강기 정보를 읽기 위해 프리퍼런스에서 날짜를 읽어 옴.
            만약, 이 앱이 '최초'로 실행된 경우 날짜 정보가 프리퍼런스에 없으므로 2021년 9월 1일을
            그 날짜로 설정함.

            API 서버에서 승가기 정보를 읽은 뒤 이 프리퍼런스에 오늘 날짜를 기록해 둠.
         */
        preferences = getPreferences(Context.MODE_PRIVATE);
        String date = preferences.getString("update_date","2021-09-01");

        Log.i("ELEVATOR", "DATE : " + date);

        handler = new Handler(this);
        db = new ElevatorInfoDB(this);

        /* API 서버에서 가장 마지막에 이 앱이 실행된 이후 추가된 승강기 정보를 getFromApiServer()를
           이용하여 읽어 옴.
           서버로 부터 데이터를 수신하면 handler를 이용하여 handleMessage()가 콜백 됨.
         */
        //RestApiMgr api = new RestApiMgr(handler, "http://boas.asuscomm.com:10002/" );
        RestApiMgr api = new RestApiMgr(handler, getString(R.string.urlBase) );
        api.getFromApiServer("afterdate/?date=" + date);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(eleinfo != null) {
            /*  승강기 리스트를 표시한 Activity에서 back button을
                누른 경우 앱을 완전히 종료하도록 함.
                이것은 앱을 새로 실행을 한 경우 SQLite에서 승강기 정보를
                읽어 승강기 리스트를 표시하는 Activity가 출력되도록 하기 위함임.
                그냥 finish()만 호출하면 Activity가 stack에서 제거되지만
                Task는 계속 background에서 실행되고 있는 상태가 됨.
             */
            finishAndRemoveTask();
            System.exit(0);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case HandlerCallback.GET_OK:
                /* API 서버로 부터 정상적으로 승강기 정보를 수신한 경우 실행되는 것으로
                   [{"content":[....JSONArray...]}] 형태로 옮.
                   여기에서 [....JSONArray...]를 getJSONArray()를 이용하여 추출하여
                   JSONArray 타입을 변경.
                 */
                JSONArray array = null;
                Intent intent;
                try {
                    array = (((JSONArray)msg.obj).getJSONObject(0)).getJSONArray("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("ELEVATOR : ", "Length : " + String.valueOf(array.length()) +
                        "\tContents : " + array.toString());

                /*  만약, API 서버에서 읽어 온 승강기 정보가 없다면 JSONArray는 비어 있음.
                    비어 있지 않다면 자체 SQLite DB에 승강기 정보를 저장함.
                 */
                if(array.length() > 0)
                    db.insertElevatorInfo(array);

                /*  앱이 API 서버로 부터 승강기 정보를 읽어 온 최종 날짜(오늘 날짜)를 프리퍼런스에
                    저장함. 앱이 다시 실행될 때 이 프리퍼런스에 저장된 날짜 이후에 DB에 추가된
                    승강기 정보만 읽어 올 수 있음.
                 */
                SharedPreferences.Editor editor = preferences.edit();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                editor.putString("update_date",currentDate);
                editor.commit();

                /*  SQLite DB에 저장되어 있는 승강기 정보를 읽은 후(JSONArray 형식) 이를
                    이용하여 승강기 정보를 화면에 리스트하는 Activity를 시작함.
                    현재 버전에서는 JSONArray를 String으로 하여 Intent에 "ele_info" 키로
                    넣어 승강기 정보를 전송함.
                 */
                eleinfo = db.getElevatorInfo();
                Log.i("ELEVATOR : ", "From DB Contents : " + eleinfo.toString());

                intent = new Intent(this, ListMainActivity.class);
                intent.putExtra("ele_info", eleinfo.toString());
                startActivity(intent);

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