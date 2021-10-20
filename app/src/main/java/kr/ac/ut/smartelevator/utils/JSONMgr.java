package kr.ac.ut.smartelevator.utils;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONMgr {

    public final static int StringType = 0;
    public final static int IntegerType = 1;
    public final static int ShortType = 2;
    public final static int LongType = 3;
    public final static int FloatType = 4;
    public final static int DoubleType = 5;
    public final static int BooleanType = 6;

    private int getClassTypeIdx(Object obj) {
        int type;
        if(obj.getClass() == String.class)
            type = JSONMgr.StringType;
        else if(obj.getClass() == Integer.class)
            type = JSONMgr.IntegerType;
        else if(obj.getClass() == Float.class)
            type = JSONMgr.FloatType;
        else if(obj.getClass() == Short.class)
            type = JSONMgr.ShortType;
        else if(obj.getClass() == Long.class)
            type = JSONMgr.LongType;
        else if(obj.getClass() == Double.class)
            type = JSONMgr.DoubleType;
        else if(obj.getClass() == Boolean.class)
            type = JSONMgr.BooleanType;
        else
            type = JSONMgr.StringType;
        return type;
    }

    public JSONObject toJSON(HashMap<String,Object> hashMap) {
        JSONObject obj = new JSONObject();
        for(Map.Entry<String, Object> entry : hashMap.entrySet()) {
            try {
                switch (getClassTypeIdx(entry.getValue())) {
                    case JSONMgr.StringType:
                        obj.put(entry.getKey(), (String)entry.getValue());
                        break;
                    case JSONMgr.IntegerType:
                        obj.put(entry.getKey(), ((Integer) entry.getValue()).intValue());
                        break;
                    case JSONMgr.ShortType:
                        obj.put(entry.getKey(), ((Short) entry.getValue()).shortValue());
                        break;
                    case JSONMgr.LongType:
                        obj.put(entry.getKey(), ((Long) entry.getValue()).longValue());
                        break;
                    case JSONMgr.FloatType:
                        obj.put(entry.getKey(), ((Float) entry.getValue()).floatValue());
                        break;
                    case JSONMgr.DoubleType:
                        obj.put(entry.getKey(), ((Double) entry.getValue()).doubleValue());
                        break;
                    case JSONMgr.BooleanType:
                        obj.put(entry.getKey(), ((Boolean) entry.getValue()).booleanValue());
                        break;
                    default:
                        Log.i("JSON :", "No match class type.");
                }
            }catch(JSONException e) {
                Log.i("JSON :", "Unhandled key-vale" + entry.getKey());
            }
        }

        return obj;
    }

    public HashMap<String, Object> fromJSON(JSONObject jsonObject) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        String key;

        Iterator<String> itr = jsonObject.keys();
        while(itr.hasNext()) {
            key = itr.next();
            try {
                map.put(key, jsonObject.get(key));
            } catch(JSONException e) {
                Log.i("JSON", "JSON -> HashMap : error for " + key);
            }
        }
        return map;
    }

}
