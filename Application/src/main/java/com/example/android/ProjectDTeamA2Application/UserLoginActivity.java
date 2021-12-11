package com.example.android.ProjectDTeamA2Application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserLoginActivity extends AppCompatActivity {

    private File file;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void startSignIn() {
        // [START sign_in_custom]
        mAuth.signInWithEmailAndPassword("b9p31013@bunkyo.ac.jp", "password").addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Login", "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
            } else {
                // If sign in fails, display a message to the user.
                Log.w("Login", "signInWithEmail:failure", task.getException());
                Toast.makeText(UserLoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        startSignIn();
        createJson();

        setContentView(R.layout.activity_userlogin);
        TextView resultTextView = findViewById(R.id.user_login_result);
        resultTextView.setText("ユーザーデータを照合には少々時間がかかります。\nログインボタンは一度のみタップしてください。");
        Button toMainButton = findViewById(R.id.to_login_and_main_button);

               toMainButton.setOnClickListener(v -> {
            // APIのresponseはdelayがあるので、待ってくれとtextviewで伝えよう
            resultTextView.setText("ユーザーデータを照合中です。しばらくお待ちください・・・");
            // まずは、ユーザーデータを取ってくる。
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // REST APIに問い合わせを行い、user_dataのjsonを取得する。
            JsonNode ApiResponse = SQLDataFetcherAndExecutor.userDataFetchResult();
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = null;
            try {
                // キーがString、値がObjectのマップに読み込みます。
                map = (HashMap<String, Object>) mapper.readValue(ApiResponse.toString(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                // エラー
                e.printStackTrace();
            }
            List<HashMap<String, String>> userData = (List<HashMap<String, String>>) Objects.requireNonNull(map).get("user_data");

            // 入力されたuser_idとpassを代入
            EditText inputUserID = findViewById(R.id.user_login_edittext_id);
            EditText inputUserPassword = findViewById(R.id.user_login_edittext_pass);

            for (int i = 0; i < Objects.requireNonNull(userData).size(); ++i) {
                HashMap<String, String> s = userData.get(i);
                if (String.valueOf(s.get("user_id")).equals(inputUserID.getText().toString())) {
                    if (String.valueOf(s.get("password")).equals(inputUserPassword.getText().toString())) {
                        // user_idとpassの一致を確認したためMainに飛ばす。ユーザーIDをjsonに記録
                        Map<String, String> map_2 = new HashMap<>();
                        map_2.put("user_id", inputUserID.getText().toString());
                        map_2.put("user_name", String.valueOf(s.get("user_name")));
                        try {
                            addDataToJson(map_2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        // user_idはあるが、passが合っていないので再入力を促す
                        resultTextView.setText(getString(R.string.wrong_pass));
                        break;
                    }
                }
            }
            resultTextView.setText(getString(R.string.wrong_pass));
        });
    }

    // この関数は、Jsonの初期化を行うための物である。
    private void createJson() {
        // 空HashMapの作成
        Map<String, String> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        String json = null;
        try {
            // mapをjson文字列に変換
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            // エラー
            e.printStackTrace();
        }
        Context context = getApplicationContext();
        String fileName = "data.json";
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(Objects.requireNonNull(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("debug", json);
    }

    void addDataToJson(Map<String, String> addData) throws IOException {
        // data.jsonの中身をJsonNode.toString()で全部書きだす。
        Context context = getApplicationContext();
        String fileName = "data.json";
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        Map<String, Object> map = new HashMap<>();
        try {
            // キーがString、値がObjectのマップに読み込みます。
            map = mapper.readValue(root.toString(), new TypeReference<Map<String, Object>>() {
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
}
