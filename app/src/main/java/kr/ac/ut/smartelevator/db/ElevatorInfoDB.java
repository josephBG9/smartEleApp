package kr.ac.ut.smartelevator.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.ut.smartelevator.app.MainActivity;
import kr.ac.ut.smartelevator.app.R;

public class ElevatorInfoDB {
    Context context;
    DbHelper sqlite;
    public ElevatorInfoDB(Context context) {
        this.context = context;
        sqlite = new DbHelper(context, context.getResources().getString(R.string.db_name), null, 1);
    }

    public void insertElevatorInfo(JSONArray array)  {
        SQLiteDatabase db = sqlite.getWritableDatabase();
        JSONObject obj;

        Log.i("ELEVATOR", "LENGTH of array : " + String.valueOf(array.length()));
        for(int i=0; i<array.length(); i++) {
            try {
                obj = array.getJSONObject(i);

                Log.i("ELEVATOR", "QRY : " + "insert into eleinfo values (" + obj.getInt("lift_id") +
                        ", '" + obj.getString("lift_name") + "', '" +
                        obj.getString("address") + "', '" +
                        obj.getString("lift_status") + "')");

                db.execSQL("insert into eleinfo values (" + obj.getInt("lift_id") +
                        ", '" + obj.getString("lift_name") + "', '" +
                        obj.getString("address") + "', '" +
                        obj.getString("lift_status") + "')");

            } catch (JSONException e) {
                Log.i("ELEVATOR", "Getting JSONObject Error in insertElevatorInfo.");
            }
        }

    }

    public JSONArray getElevatorInfo() {
        JSONArray array = new JSONArray();
        SQLiteDatabase db = sqlite.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from eleinfo", null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("lift_id", cursor.getInt(cursor.getColumnIndex("lift_id")));
                        obj.put("lift_name", cursor.getString(cursor.getColumnIndex("lift_name")));
                        obj.put("address", cursor.getString(cursor.getColumnIndex("address")));
                        obj.put("lift_status", cursor.getString(cursor.getColumnIndex("lift_status")));

                        array.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } while(cursor.moveToNext());
            }
        }
        return array;
    }

    private class DbHelper extends SQLiteOpenHelper {

        public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table eleinfo (lift_id int, lift_name varchar(30), address varchar(50), lift_status varchar(20))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
