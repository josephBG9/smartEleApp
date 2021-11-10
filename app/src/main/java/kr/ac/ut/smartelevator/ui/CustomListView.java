package kr.ac.ut.smartelevator.ui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.ut.smartelevator.app.R;

public class CustomListView extends ArrayAdapter<JSONArray> {
    private Activity context;
    private int resource;
    private JSONArray jsonArray;

    public CustomListView(@NonNull Context context, int resource, @NonNull JSONArray[] objects) {
        super(context, resource, objects);

        this.context = (Activity)  context;
        this.resource = resource;
        jsonArray = objects[0];
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(resource, null, true);
        JSONObject obj = null;
        try {
            obj = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            Log.i("ELEVATOR","JSONObject error : CustonListView");

        }

        TextView eleid = (TextView) view.findViewById(R.id.eleid);
        TextView  elename = (TextView) view.findViewById(R.id.elename);
        TextView elelocation = (TextView) view.findViewById(R.id.elelocation);
        TextView elestate = (TextView) view.findViewById(R.id.elestate);

        if(obj != null) {
            try {
                eleid.setText(String.valueOf(obj.getInt("lift_id")));
                eleid.setText(obj.getString("lift_name"));
                eleid.setText(obj.getString("address"));
                eleid.setText(obj.getString("lift_status"));

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("ELEVATOR","JSONObject conversion error : CustonListView");
            }
        }
        return super.getView(position, convertView, parent);
    }
}
