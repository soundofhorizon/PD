package com.example.android.ProjectDTeamA2Application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AFKInputActivity extends AppCompatActivity {
    private final CheckBox[] checkBox = new CheckBox[12];

    void addDataToJson(Map<String, String> addData) throws IOException {
        // data.jsonの中身をJsonNode.toString()で全部書きだす。
        Context context = getApplicationContext();
        String fileName = "data.json";
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        HashMap<String, String> map = new HashMap<>();
        try {
            // キーがString、値がObjectのマップに読み込みます。
            map = (HashMap<String, String>) mapper.readValue(root.toString(), new TypeReference<Map<String, String>>() {
            });
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
        try (FileWriter writer = new FileWriter(file)) {
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
        checkBox[7] = findViewById(R.id.checkBox8);
        checkBox[8] = findViewById(R.id.checkBox9);
        checkBox[9] = findViewById(R.id.checkBox10);
        checkBox[10] = findViewById(R.id.checkBox11);
        checkBox[11] = findViewById(R.id.checkBox12);


        ArrayList<String> arrayList = new ArrayList<>();
        findViewById(R.id.button1).setOnClickListener(v1 -> {
            int Fine_amount = 0;
            if (checkBox[0].isChecked()) {
                arrayList.add("駐停車違反(二輪車・原付)：駐車禁止場所");
                Fine_amount = 6000;
            }
            if (checkBox[1].isChecked()) {
                arrayList.add("駐停車違反(二輪車・原付)：駐停車禁止場所");
                Fine_amount = 7000;
            }
            if (checkBox[2].isChecked()) {
                arrayList.add("放置駐車違反(二輪車・原付)：駐停禁止場所");
                Fine_amount = 9000;
            }
            if (checkBox[3].isChecked()) {
                arrayList.add("駐停車違反(二輪車・原付)：駐停車禁止場所");
                Fine_amount = 10000;
            }
            if (checkBox[4].isChecked()) {
                arrayList.add("駐停車違反(普通車)：駐停車禁止場所");
                Fine_amount = 12000;
            }
            if (checkBox[5].isChecked()) {
                arrayList.add("放置駐車違反(普通車)：駐車禁止場所");
                Fine_amount = 15000;
            }
            if (checkBox[6].isChecked()) {
                arrayList.add("駐停車違反(普通車)：駐車禁止場所");
                Fine_amount = 10000;
            }
            if (checkBox[7].isChecked()) {
                arrayList.add("放置駐車違反(普通車)：駐停車禁止場所");
                Fine_amount = 18000;
            }
            if (checkBox[8].isChecked()) {
                arrayList.add("放置駐車違反(大型車)：駐停車禁止場所");
                Fine_amount = 25000;
            }
            if (checkBox[9].isChecked()) {
                arrayList.add("駐停車違反(大型車)：駐停車禁止場所");
                Fine_amount = 15000;
            }
            if (checkBox[10].isChecked()) {
                arrayList.add("放置駐車違反(大型車)：駐車禁止場所");
                Fine_amount = 21000;
            }
            if (checkBox[11].isChecked()) {
                arrayList.add("駐停車違反(大型車)：駐車禁止場所");
                Fine_amount = 12000;
            }

            //ID取得
            int PunishID = SQLDataFetcherAndExecutor.fetchMaxIndexOfPunishDataTable();
            int InsertPunishID = PunishID + 1;
            //AFK_ID取得
            int AFKInfo_ID = SQLDataFetcherAndExecutor.result2MatchAfkModeDataTable(arrayList.get(0));
            //Fine_amountをもとにID取得
//          int FineData = SQLDataFetcherAndExecuter.return2FineDataTable(Fine_amount);
            int FineID = SQLDataFetcherAndExecutor.return2MatchFineID(Fine_amount);
            //exp_dateを取得
            String exp_date = resultTimeStamp();
            //PunishDataのinsert
            SQLDataFetcherAndExecutor.executeInsertPunishDataResult(InsertPunishID, FineID, AFKInfo_ID, exp_date);


            Log.d("debug", "放置態様" + arrayList);
            Map<String, String> map = new HashMap<>();
            map.put("afk_mode", String.valueOf(arrayList.get(0)));
            map.put("punish_id", String.valueOf(FineID));
            try {
                addDataToJson(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, PrintPreviewActivity.class));
        });

        checkBox[1].setChecked(false);

        checkBox[0].setOnClickListener(v -> {
            // チェックステータス取得
            boolean check = checkBox[0].isChecked();
            if (check) {
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);

            }
        });

        checkBox[1].setOnClickListener(v -> {
            // チェックステータス取得
            boolean check = checkBox[1].isChecked();
            if (check) {
                checkBox[0].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);
            }
        });

        checkBox[2].setOnClickListener(v -> {
            // チェックステータス取得
            boolean check = checkBox[2].isChecked();
            if (check) {
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);

            }
        });

        checkBox[3].setOnClickListener(v -> {
            // チェックステータス取得
            boolean check = checkBox[3].isChecked();
            if (check) {
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);

            }
        });

        checkBox[4].setOnClickListener(v -> {
            // チェックステータス取得
            boolean check = checkBox[4].isChecked();
            if (check) {
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[5].setChecked(false);
                checkBox[6].setChecked(false);
            }
        });

        checkBox[5].setOnClickListener(v -> {
            // チェックステータス取得
            boolean check = checkBox[5].isChecked();
            if (check) {
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[6].setChecked(false);
            }
        });
        checkBox[6].setOnClickListener(v -> {
            // チェックステータス取得
            boolean check = checkBox[6].isChecked();
            if (check) {
                checkBox[0].setChecked(false);
                checkBox[1].setChecked(false);
                checkBox[2].setChecked(false);
                checkBox[3].setChecked(false);
                checkBox[4].setChecked(false);
                checkBox[5].setChecked(false);
            }
        });


    }

    private String resultTimeStamp() {
        Timestamp timestamp;
        long miles = System.currentTimeMillis();
        timestamp = new Timestamp(miles);
        Date date = new Date(timestamp.getTime());
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.DATE, 31);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strTime = sdf.format(cl.getTime());
        strTime = strTime + "T00:00:00";
        return strTime;
    }

}



