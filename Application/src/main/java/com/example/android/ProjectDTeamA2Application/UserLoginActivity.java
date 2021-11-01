package com.example.android.ProjectDTeamA2Application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserLoginActivity extends AppCompatActivity {

    private File file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_userlogin);
        TextView resultTextView = findViewById(R.id.user_login_result);
        resultTextView.setText("ユーザーデータを照合には少々時間がかかります。\nログインボタンは一度のみタップしてください。");
        Button toMainButton = findViewById(R.id.to_login_and_main_button);

        // 突っ込んでみる！！！！！！！！！
        // todo: これは読んで。それぞれ型に合わせて引数にデータを代入します。ただ、timestampは"yyyy-mm-ddTHH:MM:SS"の形式でしか入りません。なので、最初に文字列の操作が必要です。あとは各種表示された引数名に従ってください
        // responseCode:200（成功）を以下で確認する。
        // 上記executeInsertWarnInfoResultは返り値に、引数の最初のidを返却する。つまり、上記であれば11が返るわけである。よって、その一致を確かめる事で正常にデータが挿入できたことを立証したことになる。
        // 各種insertする際のid引数はuniqueである必要があるため、fetchMaxIndexOfCarDataTable()等で、現在テーブルにある最大のindexの値を取得した上で+1して引数に挿入してください

        // sample-region return ID
        // Log.d("find_region", String.valueOf(SQLDataFetcherAndExecuter.check2MatchRegionDataTable("つくば")));

        // sample-fine return ID
        // Log.d("find_fine", String.valueOf(SQLDataFetcherAndExecuter.check2MatchFineDataTable(10000)));

        // sample-afk-mode return ID
        // Log.d("find_afk", String.valueOf(SQLDataFetcherAndExecuter.check2MatchAfkModeDataTable("停車及び駐車を禁止する場所")));

        // sample-insert-warn-info return ID
        // Log.d("insert", String.valueOf(SQLDataFetcherAndExecuter.executeInsertWarnInfoResult(11,"1", "2020-09-28T11:11:11",2,37.69790673216146, 133.41260169608,1,false, "6b78ecc860e1a91752074d95b7227da4")));

        // sample-insert-car-data return ID
        // Log.d("insert_car_data", String.valueOf(SQLDataFetcherAndExecuter.executeInsertCarDataResult(SQLDataFetcherAndExecuter.fetchMaxIndexOfCarDataTable()+1,"ふ", 460, SQLDataFetcherAndExecuter.check2MatchRegionDataTable("愛媛"),"9001")));

        // sample-insert-punish-data return ID
        // Log.d("insert-punish", String.valueOf(SQLDataFetcherAndExecuter.executeInsertPunishDataResult(SQLDataFetcherAndExecuter.fetchMaxIndexOfPunishDataTable()+1,SQLDataFetcherAndExecuter.check2MatchFineDataTable(10000),SQLDataFetcherAndExecuter.check2MatchAfkModeDataTable("時間制限駐車区間（パーキング・メーター設置区間）における駐車"),"2022-02-22T00:00:11")));

        toMainButton.setOnClickListener( v -> {
            // APIのresponseはdelayがあるので、待ってくれとtextviewで伝えよう
            resultTextView.setText("ユーザーデータを照合中です。しばらくお待ちください・・・");
            // まずは、ユーザーデータを取ってくる。
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String url = "https://peteama-apiserver.herokuapp.com/api/rest/user_data";

            // REST APIに問い合わせを行い、user_dataのjsonを取得する。
            JsonNode ApiResponse = SQLDataFetcherAndExecuter.userDataFetchResult();
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = null;
            try {
                // キーがString、値がObjectのマップに読み込みます。
                map = (HashMap<String, Object>) mapper.readValue(ApiResponse.toString(), new TypeReference<Map<String, Object>>(){});
            } catch (Exception e) {
                // エラー
                e.printStackTrace();
            }
            List<HashMap<String,String>> userData = (List<HashMap<String, String>>) Objects.requireNonNull(map).get("user_data");

            // 入力されたuser_idとpassを代入
            EditText inputUserID = findViewById(R.id.user_login_edittext_id);
            EditText inputUserPassword = findViewById(R.id.user_login_edittext_pass);

            for(int i = 0; i < userData.size(); ++i){
                HashMap<String, String> s = userData.get(i);
                if(String.valueOf(s.get("user_id")).equals(inputUserID.getText().toString())){
                    if(String.valueOf(s.get("password")).equals(inputUserPassword.getText().toString())){
                        // user_idとpassの一致を確認したためMainに飛ばす。ユーザーIDをjsonに記録
                        Map<String , String> map_2 = new HashMap<>();
                        map_2.put("user_id",String.valueOf(inputUserID.getText().toString()));
                        map_2.put("user_name",String.valueOf(s.get("user_name")));
                        try {
                            addDataToJson(map_2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(this, MainActivity.class));
                    }else{
                        // user_idはあるが、passが合っていないので再入力を促す
                        resultTextView.setText(getString(R.string.wrong_pass));
                        break;
                    }
                }
            }
            resultTextView.setText(getString(R.string.wrong_pass));
        } );
    }

    void  addDataToJson(Map<String, String> addData) throws IOException {
        // data.jsonの中身をJsonNode.toString()で全部書きだす。
        Context context = getApplicationContext();
        String fileName = "data.json";
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        Map<String, Object> map = new HashMap<>();
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
}
