package kr.ac.ut.smartelevator.buffer;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.stream.Stream;

public class BufferMgr {
    public static final int TYPE_ELEVATOR_ERROR = 1;
    public static final int TYPE_ELEVATOR_MAINTENANCE = 2;

    private Context context;

    public BufferMgr(Context context) {
        this.context = context;
    }

    public void saveData(int type, JSONObject obj) {
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    context.openFileOutput("data." + type,
                            Context.MODE_PRIVATE | Context.MODE_APPEND)));

            writer.write(obj.toString() +"\n");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("EVEVATOR", "BufferMgr - File open Error in saveData");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("EVEVATOR", "BufferMgr - File IO Error in saveData");
        }
    }

    public JSONArray loadData(int type) {
        JSONObject jsonObject;
        JSONArray jsonArray;
        BufferedReader reader;
        String line;

        jsonArray = new JSONArray();
        try {
            reader = new BufferedReader(new InputStreamReader(
                    context.openFileInput("data."+type)));
            while((line=reader.readLine()) != null) {
                jsonArray.put(new JSONObject(line));
            }
            reader.close();
            context.deleteFile("data."+type);

        } catch (FileNotFoundException e) {
            // No operation
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }
}
