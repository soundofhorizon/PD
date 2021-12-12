package com.example.android.ProjectDTeamA2Application;

import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpsJson {
    // HTTPSで、JSONデータをPOSTして、JSONデータをもらう
    // targetUrl: 送信先URL
    // jsonstr: 送信したいJSON文字列
    // 戻り値: 文字列
    public static String post(String targetUrl, String jsonstr) {

        try {
            // 接続する
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(targetUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            // POSTのデータを送る
            OutputStream os = con.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(jsonstr);
            ps.close();
            // レスポンスをもらって文字列化する
            String str = InputStreamToString(con.getInputStream());
            // 接続を切る
            con.disconnect();
            // 値を返す
            return str;
        } catch(Exception ex) {
            // エラーの時
            ex.printStackTrace();
            return ex.toString();
        }
    }

    /*
    文字列に変換
    */
    private static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

}
