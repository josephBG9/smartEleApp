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
/*
  2021-11-14:
    현재는 세로 화면에 대한 것만 작업이 되어 있음.
    가로 화면 리소스를 정의하는 경우 이를 위한 것도 작업을 해야 함.
    --> 컴포넌트의 width와 출력할 string의 width를 이용하여 적절하게
    출력되도록 해야 함.
 */
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

    /*
        이 앱의 경우 JSONArray[] 배열은 한 개의 JSONArray 객체를 저장하고 있기 때문에
        배열의 크기를 반화해야 하는 이 메소드는 JSONArray[] 배열의 크기가 아닌
        JSONArray[0]에 저장되어 있는 JSONArray 객체가 저장하고 있는 크기를
        반환해야 함.
        이를 위해 이 메소드를 overriding 함.
     */
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
        /*
            각 리스트 아이템에 출력해야 하는 것을 JSONArray에서 position 값을 이용하여
            JSONObject 객체를 읽어, 이를 이용함.
         */
        try {
            obj = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            Log.i("ELEVATOR","JSONObject error : CustonListView");

        }

        TextView eleid = (TextView) view.findViewById(R.id.eleid);
        TextView  elename = (TextView) view.findViewById(R.id.elename);
        TextView elelocation = (TextView) view.findViewById(R.id.elelocation);
        TextView elestate = (TextView) view.findViewById(R.id.elestate);

        /*
          2021-11-14:
            현재는 세로 화면에 대한 것만 되어 있어, 주소를 5글자만 출력하고, 상태는 visible=gone으로
            되어 있음.
            향후 가로 화면에 대한 리소스를 정의하는 경우 모두 표시할 수 있도록 이부분을 변경해야함.
         */
        if(obj != null) {
            try {
                eleid.setText(String.valueOf(obj.getInt("lift_id")));
                elename.setText(obj.getString("lift_name"));
                elelocation.setText(obj.getString("address").substring(0, 4) + "...");
                elestate.setText(obj.getString("lift_status"));

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("ELEVATOR","JSONObject conversion error : CustonListView");
            }
        }
        return view;
        //return super.getView(position, convertView, parent);
    }
}
