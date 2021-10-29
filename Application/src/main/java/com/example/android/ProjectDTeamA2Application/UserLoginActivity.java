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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        toMainButton.setOnClickListener( v -> {
            // APIのresponseはdelayがあるので、待ってくれとtextviewで伝えよう
            resultTextView.setText("ユーザーデータを照合中です。しばらくお待ちください・・・");
            // まずは、ユーザーデータを取ってくる。
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String url = "https://peteama-apiserver.herokuapp.com/api/rest/user_data";
            JsonNode ApiResponse = getResult(url);
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = null;
            try {
                // キーがString、値がObjectのマップに読み込みます。
                map = mapper.readValue(ApiResponse.toString(), new TypeReference<Map<String, Object>>(){});
            } catch (Exception e) {
                // エラー
                e.printStackTrace();
            }
            Log.d("json", String.valueOf(ApiResponse));
            List<HashMap<String,String>> userData = (List<HashMap<String, String>>) map.get("user_data");

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
                        resultTextView.setText("ユーザーIDかPasswordが間違えているようです。再度入力してください。");
                        break;
                    }
                }
            }
            resultTextView.setText("ユーザーIDかPasswordが間違えているようです。再度入力してください。");
        } );
    }

    void  addDataToJson(Map<String, String> addData) throws IOException {
        // data.jsonの中身をJsonNode.toString()で全部書きだす。
        Context context = getApplicationContext();
        String fileName = "data.json";
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
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
    private static JsonNode getResult(String urlString) {
        String result = "";
        JsonNode root = null;
        try {

            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.connect(); // URL接続
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String tmp = "";

            while ((tmp = in.readLine()) != null) {
                result += tmp;
            }

            ObjectMapper mapper = new ObjectMapper();
            root = mapper.readTree(result);
            in.close();
            con.disconnect();
        }catch(Exception e) {
            e.printStackTrace();
        }

        return root;
    }
}
