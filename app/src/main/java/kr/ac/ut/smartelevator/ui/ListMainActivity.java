package kr.ac.ut.smartelevator.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.ut.smartelevator.app.R;

public class ListMainActivity extends AppCompatActivity {

    CustomListView adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);

        /*  CustomListView가 ArrayAdapter를 상속하므로 객체의 Array가 필요함.
            따라서, JSONArray 하나를 저장하는 JSONArray []가 필요함.
            즉, JSONArray 객체 하나를 저장하는 배열을 생성하여 CustomListView에게 넘겨 줌.
         */
        JSONArray[] data  = new JSONArray[1];

        /*  MainActivity에서 "ele_info" 키 값으로 intent에 넣어 준 승강기 정보를 읽어
            JSONArray 객체를 생성한 후 이를 JSONArray 객체 배열 [0]에 넣음.
            앞서 기술한 것과 같이 이 배열에는 JSONArray 객체 1개만 저장함.
         */
        intent = getIntent();
        try {
            data[0] = new JSONArray(intent.getStringExtra("ele_info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("ELEVATOR", "New Activity : " + data[0].toString());

        ListView listView = (ListView) findViewById(R.id.mylist);
        adapt = new CustomListView(this, R.layout.custom_listview, data);
        listView.setAdapter(adapt);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    /*  선택한 승강기 정보를 이용하여 승강기 점검 내용을 입력할 수 있는 Activity를
                        시작시킴.
                        이때 전달할 승강기 정보는 승강기 식별자(id)와 이름(name)이며, 이것은 각각
                        intent의 'lift_id'와 'lift_name' 키를 이용하여 저장한 후
                        MaintenanceItemActivity에 전달함.
                     */
                    JSONObject target = data[0].getJSONObject(position);
                    Intent intent = new Intent(ListMainActivity.this, MaintenanceItemActivity.class);
                    intent.putExtra("lift_id", target.getInt("lift_id") );
                    intent.putExtra("lift_name", target.getString("lift_name"));

                    Log.i("ELEVATOR", "ITEM CLICKED : " + target.getInt("lift_id") );
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}