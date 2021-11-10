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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);

        JSONArray[] data  = new JSONArray[1];

        try {
            data[0] = new JSONArray("[{\"lift_id\":1,\"lift_name\":\"중앙정보관\",\"lift_status\":\"비정상\",\"address\":\"충북 충주시 대학로 50\",\"created_at\":\"2021-11-03T04:26:58+09:00\",\"updated_at\":\"2021-11-03T13:57:14+09:00\"},{\"lift_id\":2,\"lift_name\":\"건설환경관\",\"lift_status\":\"정상\",\"address\":\"충북 충주시 대학로 50\",\"created_at\":\"2021-11-03T04:26:58+09:00\",\"updated_at\":null},{\"lift_id\":3,\"lift_name\":\"스마트ICT\",\"lift_status\":\"정상\",\"address\":\"충북 충주시 대학로 50\",\"created_at\":\"2021-11-03T04:26:58+09:00\",\"updated_at\":null}]");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.mylist);
        adapt = new CustomListView(this, R.layout.custom_listview, data);
        listView.setAdapter(adapt);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
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