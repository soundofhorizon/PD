package com.example.android.ProjectDTeamA2Application;

import android.os.StrictMode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SQLDataFetcherAndExecuter {

    protected static JsonNode userDataFetchResult(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://peteama-apiserver.herokuapp.com/api/rest/user_data";
        return getResult(url);
    }

    protected static Integer fetchMaxIndexOfWarnInfoTable(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://peteama-apiserver.herokuapp.com/api/rest/warn_info_id";
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<HashMap<String,Integer>> warnInfo = (List<HashMap<String, Integer>>) Objects.requireNonNull(map).get("warn_info");
        Integer max=0, now;
        for(int i = 0; i < warnInfo.size(); ++i){
            now = warnInfo.get(i).get("id");
            if(max < now){
                max = now;
            }
        }
        return max;
    }

    protected static Integer fetchMaxIndexOfPunishDataTable(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://peteama-apiserver.herokuapp.com/api/rest/punish_data_id";
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<HashMap<String,Integer>> punishData = (List<HashMap<String, Integer>>) Objects.requireNonNull(map).get("punish_data");
        Integer max=0, now;
        for(int i = 0; i < punishData.size(); ++i){
            now = punishData.get(i).get("id");
            if(max < now){
                max = now;
            }
        }
        return max;
    }

    protected static Integer fetchMaxIndexOfCarDataTable(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://peteama-apiserver.herokuapp.com/api/rest/car_data_id";
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<HashMap<String,Integer>> carData = (List<HashMap<String,Integer>>) Objects.requireNonNull(map).get("data");
        int max=0, now;
        for(int i = 0; i < carData.size(); ++i){
            now = carData.get(i).get("id");
            if(max < now){
                max = now;
            }
        }
        return max;
    }

    protected static Integer executeInsertWarnInfoResult(Integer id, String userId, String timestamp, Integer punishId, Double latitude, Double longitude, Integer carDataId, Boolean isPayment, String imageUrl){
        // https://peteama-apiserver.herokuapp.com/api/rest/insert_warn_info_data/:id/:user_id/:timestamp/:punish_id/:latitude/:longitude/:car_data_id/:is_payment/:image_url
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = "https://peteama-apiserver.herokuapp.com/api/rest/insert_warn_info_data/" + id.toString() + "/" + userId + "/" + timestamp + "/" + punishId.toString() + "/" + latitude.toString() + "/" + longitude.toString() + "/" + carDataId.toString() + "/" + isPayment.toString() + "/" + imageUrl;
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String,Object> warnInfoData = (HashMap<String, Object>) Objects.requireNonNull(map).get("insert_warn_info");
        List<HashMap<String,Integer>> now = (List<HashMap<String, Integer>>) warnInfoData.get("returning");
        return now.get(0).get("id");
    }

    private static JsonNode getResult(String urlString) {
        StringBuilder result = new StringBuilder();
        JsonNode root = null;
        try {

            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.connect(); // URL接続
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String tmp;

            while ((tmp = in.readLine()) != null) {
                result.append(tmp);
            }

            ObjectMapper mapper = new ObjectMapper();
            root = mapper.readTree(result.toString());
            in.close();
            con.disconnect();
        }catch(Exception e) {
            e.printStackTrace();
        }

        return root;
    }

}
