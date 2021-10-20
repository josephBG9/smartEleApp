package kr.ac.ut.smartelevator.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.ac.ut.smartelevator.utils.JSONMgr;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
 * Testing for JSONMgr class.
 * JSONMgr.toJSON() : HashMap Object to JSON Object
 * JSONMgr.fromJSON() : JSON Object to HashMap Object.
 *
 *
        JSONMgr jsonMgr = new JSONMgr();

        HashMap<String,Object> map = new HashMap<String, Object>();
        JSONObject jsonObject;
        HashMap<String,Object> map2;

        map.put("str","GOOD");
        map.put("int", 123);
        map.put("float", 3.14F);
        map.put("boolean", true);

        Log.i("JSON : ", "Starting LOG");
        for(Map.Entry<String, Object>entry : map.entrySet()) {
            Log.i("JSON TEST : ", entry.getKey() + " ---> " + entry.getValue());
        }

        Log.i("JSON : ", "JSON LOG");
        jsonObject = jsonMgr.toJSON(map);
        String key;
        Iterator<String> itr = jsonObject.keys();
        while(itr.hasNext()) {
            key = itr.next();
            try {
                Log.i("JSON TEST", key + "-->" + jsonObject.get(key));
            } catch(JSONException e) {
                Log.i("JSON", "JSON -> HashMap : error for " + key);
            }
        }

        map2 = jsonMgr.fromJSON(jsonObject);
        Log.i("JSON : ", "Ending LOG");
        for(Map.Entry<String, Object>entry : map2.entrySet()) {
            Log.i("JSON TEST : ", entry.getKey() + " ---> " + entry.getValue());
        }
*/


    }
}