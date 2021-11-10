package kr.ac.ut.smartelevator.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONArray;

import kr.ac.ut.smartelevator.app.R;

public class ListMainActivity extends AppCompatActivity {

    CustomListView adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);

        JSONArray array = new JSONArray();


    }
}