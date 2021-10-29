package com.example.android.ProjectDTeamA2Application;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrintPreviewActivity extends AppCompatActivity {
    File file;
    ImageView imageView;
    ImageView imageView1 ;

    String time;
    String area;
    String area2;
    String status;

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
        Log.d("debug_addDatatoJson", "取得状態");
        Log.d("debug_addDatatoJson", json);


        time = map.get("timestamp");
        area = map.get("location.getLatitude");
        area2 = map.get("location.getLongitude");
        status = map.get("afk_mode");

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printpreview);
        //imageView = findViewById(R.id.seal_view);
        // text_view： activity_main.xml の TextView の id
        TextView mImageDetails = findViewById(R.id.text_view);
        // テキストを設定。画像更新後、OCR用のString変数として利用。
        mImageDetails.setText(R.string.description_for_create_pdf);
        // printPDFボタンを押されたらPDFを発行する。それで終わり。
        findViewById(R.id.printPDF).setOnClickListener(v1 -> {
            Log.d("push_pdf_button", "PDF発行関数をここに挿入してタイトルへ差し戻す");
        });
        copyFile();
        MyView();

    }

    public void MyView(){
        setContentView(R.layout.activity_printpreview);
        Paint paint1 = new Paint();

        float StrokeWidth1 = 1.0f;

        imageView1 = findViewById(R.id.imageView1);
        Bitmap bitmap = Bitmap.createBitmap(900, 1200, Bitmap.Config.ARGB_8888);


        // Canvasの作成
        Canvas canvas;
        canvas = new Canvas(bitmap);


        paint1.setColor(Color.BLACK);
        paint1.setStrokeWidth(StrokeWidth1);
        paint1.setTypeface(Typeface.DEFAULT);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setTextSize(20);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.seal_base);


        Matrix matrix = new Matrix();
        Map<String , String> map = new HashMap<>();
        try {
            addDataToJson(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        canvas.drawBitmap(bmp, matrix, null);
        canvas.drawText(""+time, 100, 470, paint1);
        canvas.drawText(""+area + ","+area2, 100, 510, paint1);
        canvas.drawText(""+status, 100, 580, paint1);




        imageView1.setImageBitmap(bitmap);

    }


    private void copyFile() {
        Context context = getApplicationContext();
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "seal.jpg");
        Resources r = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.seal_base);
//        imageView.setImageBitmap(bmp);
        try {
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            // 保存(JPGで保存している。bitmapに直すときはここを参照せよ: https://qiita.com/aymikmts/items/7139fa6c4da3b57cb4fc)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            // 保存終了
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}