package com.example.android.ProjectDTeamA2Application;

import android.os.StrictMode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SQLDataFetcherAndExecutor {

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
        List<HashMap<String,Integer>> carData = (List<HashMap<String,Integer>>) Objects.requireNonNull(map).get("car_data");
        int max=0, now;
        for(int i = 0; i < carData.size(); ++i){
            now = carData.get(i).get("id");
            if(max < now){
                max = now;
            }
        }
        return max;
    }

    protected static Integer check2MatchCarDataTable(String car_classify_hiragana, Integer car_classify_num, Integer car_region_id, String car_number){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://peteama-apiserver.herokuapp.com/api/rest/car_data";
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        int Result = 0;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<HashMap<String,Object>> carData = (List<HashMap<String,Object>>) Objects.requireNonNull(map).get("car_data");
        for(int i = 0; i < carData.size(); ++i){
            if(carData.get(i).get("car_region_id").toString().equals(car_region_id.toString()) && carData.get(i).get("car_classify_num").toString().equals(car_classify_num.toString()) && carData.get(i).get("car_classify_hiragana").toString().equals(car_classify_hiragana) && carData.get(i).get("car_number").toString().equals(car_number)){
                Result = 0;
            }else {
                Result = 1;
            }
        }
        // ここまで来たら一致0により返却
        return Result;
    }

    protected static Integer check2MatchFineDataTable(Integer fine_amount){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://peteama-apiserver.herokuapp.com/api/rest/fine_data";
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<HashMap<String,Object>> fineData = (List<HashMap<String,Object>>) Objects.requireNonNull(map).get("fine_data");
        for(int i = 0; i < fineData.size(); ++i){
            if(fineData.get(i).get("fine_amount").toString().equals(fine_amount.toString())){
                return (int)fineData.get(i).get("id");
            }
        }
        // ここまで来たら一致0により返却
        return 0;
    }

    protected static Integer check2MatchAfkModeDataTable(String afk_mode){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://peteama-apiserver.herokuapp.com/api/rest/afk_mode_data";
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<HashMap<String,Object>> fineData = (List<HashMap<String,Object>>) Objects.requireNonNull(map).get("afk_mode_data");
        for(int i = 0; i < fineData.size(); ++i){
            if(fineData.get(i).get("afk_mode").toString().equals(afk_mode)){
                return (int)fineData.get(i).get("id");
            }
        }
        // ここまで来たら一致0により返却
        return 0;
    }

    protected static Integer check2MatchRegionDataTable(String region_name){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://peteama-apiserver.herokuapp.com/api/rest/region_data";
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<HashMap<String,Object>> regionData = (List<HashMap<String,Object>>) Objects.requireNonNull(map).get("region_data");
        for(int i = 0; i <  regionData.size(); ++i){
            if( regionData.get(i).get("region_name").toString().equals(region_name)){
                return (int) regionData.get(i).get("id");
            }
        }
        // ここまで来たら一致0により返却
        return 0;
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

    protected static Integer executeInsertCarDataResult(Integer id, String car_classify_hiragana, Integer car_classify_num, Integer car_region_id, String car_number){
        // https://peteama-apiserver.herokuapp.com/api/rest/insert_car_data/:id/:car_classify_hiragana/:car_classify_num/:car_region_id/:car_number
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = "https://peteama-apiserver.herokuapp.com/api/rest/insert_car_data/" + id.toString() + "/" + car_classify_hiragana + "/" + car_classify_num.toString() + "/" + car_region_id.toString() + "/" + car_number;
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String,Object> warnInfoData = (HashMap<String, Object>) Objects.requireNonNull(map).get("insert_car_data");
        List<HashMap<String,Integer>> now = (List<HashMap<String, Integer>>) warnInfoData.get("returning");
        return now.get(0).get("id");
    }

    protected static Integer executeInsertPunishDataResult(Integer id, Integer fine_id, Integer afk_mode_id, String exp_date){
        // https://peteama-apiserver.herokuapp.com/api/rest/insert_punish_data/:id/:fine_id/:afk_mode_id/:exp_date
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = "https://peteama-apiserver.herokuapp.com/api/rest/insert_punish_data/" + id.toString() + "/" + fine_id.toString() + "/" + afk_mode_id.toString() + "/" + exp_date;
        JsonNode result = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = null;
        try {
            map = (HashMap<String, Object>) mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String,Object> warnInfoData = (HashMap<String, Object>) Objects.requireNonNull(map).get("insert_punish_data");
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