package com.example.android.ProjectDTeamA2Application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AFKInputActivity extends AppCompatActivity {
    private final CheckBox[] checkBox = new CheckBox[7];

    void  addDataToJson(Map<String, String> addData) throws IOException {
        // data.jsonの中身をJsonNode.toString()で全部書きだす。
        Context context = getApplicationContext();
        String fileName = "data.json";
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        HashMap<String, String> map = new HashMap<>();
        try {
            // キーがString、値がObjectのマップに読み込みます。
            map = mapper.readValue(root.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            // エラー
            e.printStackTrace();
        }

        // addDataのMapに入っているkeyとvalueで上書き
        map.putAll(addData);

        String json = null;
        try {
            // mapをjson文字列に変換
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            // エラー
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(file)){
            writer.write(Objects.requireNonNull(json));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("debug_addDatatoJson", json);
    }


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
            Map<String , String> map = new HashMap<>();
            map.put("afk_mode",String.valueOf(arrayList.get(0)));
            try {
                addDataToJson(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, PrintPreviewActivity.class));
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


