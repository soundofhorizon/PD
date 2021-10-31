package com.example.android.ProjectDTeamA2Application;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrintPreviewActivity extends AppCompatActivity {
    ImageView imageView;

    String time;
    String area;
    String area2;
    String status;
    Bitmap bitmap;

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
            map = (HashMap<String, String>) mapper.readValue(root.toString(), new TypeReference<Map<String, String>>(){});
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

        time = map.get("timestamp");
        area = map.get("Latitude");
        area2 = map.get("Longitude");
        status = map.get("afk_mode");

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 本来ならAsyncTaskに投げ込むべきAPIリクエストであるが、まぁ面倒臭いので制限を壊す
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_printpreview);
        imageView = findViewById(R.id.imageView);
        // text_view： activity_main.xml の TextView の id
        TextView mImageDetails = findViewById(R.id.text_view);
        // テキストを設定。画像更新後、OCR用のString変数として利用。
        mImageDetails.setText(R.string.description_for_create_pdf);
        // printPDFボタンを押されたらPDFを発行する。それで終わり。
        findViewById(R.id.printPDF).setOnClickListener(v1 -> Log.d("push_pdf_button", "PDF発行関数をここに挿入してタイトルへ差し戻す"));
        copyFile();
        MyView();
        imageView.setImageBitmap(bitmap);
    }

    public void MyView(){

        Map<String , String> map = new HashMap<>();
        try {
            addDataToJson(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "seal.jpg");
        try(InputStream inputStream0 = new FileInputStream(file)) {
            // mutableなオブジェクトに変換を掛ける
            bitmap = BitmapFactory.decodeStream(inputStream0).copy(Bitmap.Config.ARGB_8888, true);
        }catch(IOException e){
            e.printStackTrace();
        }
        String url = "http://geoapi.heartrails.com/api/json?method=searchByGeoLocation&x="+area2+"&y="+area;
        JsonNode ApiResponse = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map_2 = null;
        try {
            // キーがString、値がObjectのマップに読み込みます。
            map_2 = (HashMap<String, Object>) mapper.readValue(ApiResponse.toString(), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            // エラー
            e.printStackTrace();
        }
        HashMap<String, List<HashMap<String,String>>> codeData_before2 = (HashMap<String,List<HashMap<String,String>>>) Objects.requireNonNull(map_2).get("response");
        List<HashMap<String,String>> codeData_before1 = codeData_before2.get("location");
        try {
            HashMap<String, String> codeData = codeData_before1.get(0);
            bitmap = drawStringonBitmap(bitmap, time.substring(0, time.length()-4), new Point(200,1190), Color.BLACK, 100, 60,false,957,1950);
            bitmap = drawStringonBitmap(bitmap, "〒"+codeData.get("postal")+"  "+codeData.get("prefecture")+codeData.get("city")+codeData.get("town"), new Point(200,1290), Color.BLACK, 100, 30,false,957,1950);
            bitmap = drawStringonBitmap(bitmap, status, new Point(200,1425), Color.BLACK, 100, 30,false,957,1950);
        }catch (NullPointerException e){
            // 日本以外は住所に対応していないため、ヌルポが出たら、その時点でアプリ終了
            moveTaskToBack(true);
        }
    }

    public static Bitmap drawStringonBitmap(Bitmap src, String string, Point location, int color, int alpha, int size, boolean underline, int width , int height) {

        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setUnderlineText(underline);
        if(string.length() >= 24){
            String string_1 = string.substring(0, 24);
            String string_2 = string.substring(24);
            canvas.drawText(string_1, location.x, location.y, paint);
            canvas.drawText(string_2, location.x, location.y+100, paint);
        }else {
            canvas.drawText(string, location.x, location.y, paint);
        }
        return result;
    }


    private void copyFile() {
        Context context = getApplicationContext();
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "seal.jpg");
        Resources r = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.seal_base);
        try {
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            // 保存(JPGで保存している。bitmapに直すときはここを参照せよ: https://qiita.com/aymikmts/items/7139fa6c4da3b57cb4fc)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            // 保存終了
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void insert_warn_info(){

    }
}