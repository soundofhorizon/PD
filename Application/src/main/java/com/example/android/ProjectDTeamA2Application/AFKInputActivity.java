package com.example.android.ProjectDTeamA2Application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

public class AFKInputActivity extends AppCompatActivity {
    private final CheckBox[] checkBox = new CheckBox[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afkinput);

        checkBox[0] = findViewById(R.id.checkBox);
        checkBox[1] = findViewById(R.id.checkBox2);
        checkBox[2] = findViewById(R.id.checkBox3);
        checkBox[3] = findViewById(R.id.checkBox4);
        checkBox[4] = findViewById(R.id.checkBox5);
        checkBox[5] = findViewById(R.id.checkBox6);
        checkBox[6] = findViewById(R.id.checkBox7);


        ArrayList<String> arrayList = new ArrayList<>();
        findViewById(R.id.button1).setOnClickListener(v1 -> {

            if (checkBox[0].isChecked()) {
                arrayList.add("道路標識により駐（停）車を禁止する場所");
            }
            if (checkBox[1].isChecked()) {
                arrayList.add("停車及び駐車を禁止する場所");
            }
            if (checkBox[2].isChecked()) {
                arrayList.add("法定の駐車禁止場所");
            }
            if (checkBox[3].isChecked()) {
                arrayList.add("無余地場所");
            }
            if (checkBox[4].isChecked()) {
                arrayList.add("停車又は駐車の方法に従わない駐車");
            }
            if (checkBox[5].isChecked()) {
                arrayList.add("時間制限駐車区間（パーキング・メーター設置区間）における駐車");
            }
            if (checkBox[6].isChecked()) {
                arrayList.add("高齢運転者等専用駐車区間における駐車");
            }


            Log.d("debug", "放置態様" + arrayList);
        });

        checkBox[1].setChecked(false);

        checkBox[0].setOnClickListener( v -> {
            // チェックステータス取得
            boolean check = checkBox[0].isChecked();
            if(check){
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);

            }
        });

        checkBox[1].setOnClickListener( v -> {
            // チェックステータス取得
            boolean check = checkBox[1].isChecked();
            if(check){
                checkBox[0].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);
            }
        });

        checkBox[2].setOnClickListener( v -> {
            // チェックステータス取得
            boolean check = checkBox[2].isChecked();
            if(check){
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);

            }
        });

        checkBox[3].setOnClickListener( v -> {
            // チェックステータス取得
            boolean check = checkBox[3].isChecked();
            if(check){
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);

            }
        });

        checkBox[4].setOnClickListener( v -> {
            // チェックステータス取得
            boolean check = checkBox[4].isChecked();
            if(check){
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);
            }
        });

        checkBox[5].setOnClickListener( v -> {
            // チェックステータス取得
            boolean check = checkBox[5].isChecked();
            if(check){
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[6].setChecked(false);
            }
        });
        checkBox[6].setOnClickListener( v -> {
            // チェックステータス取得
            boolean check = checkBox[6].isChecked();
            if(check){
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
            }
        });
    }
}
