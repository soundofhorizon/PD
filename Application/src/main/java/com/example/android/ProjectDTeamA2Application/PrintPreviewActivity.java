package com.example.android.ProjectDTeamA2Application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    String Number;
    String car_classify_hiragana;
    String car_classify_num;
    String car_region_id;
    Boolean payment = false;
    UserLoginActivity userLoginActivity = new UserLoginActivity();
    String UserLogin = userLoginActivity.UserId;
    String UserName = userLoginActivity.UserName ;
    int id = SQLDataFetcherAndExecutor.fetchMaxIndexOfWarnInfoTable()+1;


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 本来ならAsyncTaskに投げ込むべきAPIリクエストであるが、まぁ面倒臭いので制限を壊す
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_printpreview);
        imageView = findViewById(R.id.seal_imageView);
        //imageView = findViewById(R.id.imageView);
        // text_view： activity_main.xml の TextView の id
        TextView mImageDetails = findViewById(R.id.text_view);
        // テキストを設定。画像更新後、OCR用のString変数として利用。
        mImageDetails.setText(R.string.description_for_create_pdf);
        // printPDFボタンを押されたらPDFを発行する。それで終わり。
        findViewById(R.id.printPDF).setOnClickListener(v1 -> {
            outputPDF();
            // 画像bse64データを挿入
            Resources r = getResources();
            File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "pic.jpg");
            try (InputStream inputStream0 = new FileInputStream(file)) {
                // mutableなオブジェクトに変換を掛ける
                Bitmap bmp = BitmapFactory.decodeStream(inputStream0).copy(Bitmap.Config.ARGB_8888, true);
                String b64 = encodeTobase64(bmp);
                Map<String, String> map = new HashMap<>();
                map.put("image_data", String.valueOf(b64));
                try {
                    addDataToJson(map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //TODO ここでwarn_infoへデータを挿入する
            String User_id = UserLogin;
            String TimeStamp = time;
            Double lat = Double.parseDouble(area);
            Double lon = Double.parseDouble(area2);
            int Id = SQLDataFetcherAndExecutor.fetchMaxIndexOfWarnInfoTable()+1;
            int PunishID = SQLDataFetcherAndExecutor.fetchMaxIndexOfPunishDataTable()+1;
            int CarDataID = SQLDataFetcherAndExecutor.fetchMaxIndexOfCarDataTable()+1;
            //WarnInfoInsert
            int a = SQLDataFetcherAndExecutor.executeInsertWarnInfoResult(Id,User_id,TimeStamp,PunishID,lat,lon,CarDataID,payment,"image_base64");
     //       int CarDataId = Insertcar_data();
            //TODO 挿入が完了したら、完了した旨を送信し、ログイン画面に差し戻す用のボタンを追加
            mImageDetails.setText("PDFの発行が完了しました。確認の上、ログイン画面にお戻りください。");
        });
        copyFile();
        MyView();
        imageView.setImageBitmap(bitmap);
        Insertcar_data();

    }

    private static String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

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
            Number = map.get("number");
            car_classify_hiragana = map.get("classify_hiragana");
            car_classify_num = map.get("classify_num");
            car_region_id = map.get("carNumber_region");
        } catch (Exception e) {
            // エラー
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(Objects.requireNonNull(json));
        } catch (IOException e) {
            e.printStackTrace();
        }

        time = map.get("timestamp");
        area = map.get("Latitude");
        area2 = map.get("Longitude");
        status = map.get("afk_mode");

    }


    public void MyView() {

        Map<String, String> map = new HashMap<>();
        try {
            addDataToJson(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "seal.jpg");
        try (InputStream inputStream0 = new FileInputStream(file)) {
            // mutableなオブジェクトに変換を掛ける
            bitmap = BitmapFactory.decodeStream(inputStream0).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = "http://geoapi.heartrails.com/api/json?method=searchByGeoLocation&x=" + area2 + "&y=" + area;
        JsonNode ApiResponse = getResult(url);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map_2 = null;
        try {
            // キーがString、値がObjectのマップに読み込みます。
            map_2 = (HashMap<String, Object>) mapper.readValue(ApiResponse.toString(), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            // エラー
            e.printStackTrace();
        }
        HashMap<String, List<HashMap<String, String>>> codeData_before2 = (HashMap<String, List<HashMap<String, String>>>) Objects.requireNonNull(map_2).get("response");
        List<HashMap<String, String>> codeData_before1 = codeData_before2.get("location");
        try {
            HashMap<String, String> codeData = codeData_before1.get(0);
            bitmap = drawStringonBitmap(bitmap, time.substring(0, time.length() - 4), new Point(80, 485), Color.BLACK, 100, 30, false, 420, 858, false);
            bitmap = drawStringonBitmap(bitmap, "〒" + codeData.get("postal") + "  " + codeData.get("prefecture") + codeData.get("city") + codeData.get("town"), new Point(80, 545), Color.BLACK, 100, 15, false, 420, 858, false);
            bitmap = drawStringonBitmap(bitmap, status, new Point(80, 640), Color.BLACK, 100, 15, false, 420, 858, true);
        } catch (NullPointerException e) {
            // 日本以外は住所に対応していないため、ヌルポが出たら、その時点でアプリ終了
            moveTaskToBack(true);
        }
    }

    public static Bitmap drawStringonBitmap(Bitmap src, String string, Point location, int color, int alpha, int size, boolean underline, int width, int height, Boolean orikaesi_frag) {

        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 20, null);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setUnderlineText(underline);
        if (string.length() >= 19 && orikaesi_frag) {
            String string_1 = string.substring(0, 19);
            String string_2 = string.substring(19);
            canvas.drawText(string_1, location.x, location.y, paint);
            canvas.drawText(string_2, location.x, location.y + 30, paint);
        } else {
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
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    private void outputPDF() {
        File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "seal.jpg");

        PdfDocument doc = new PdfDocument();
        PdfDocument.Page page = doc.startPage(new PdfDocument.PageInfo.Builder(595, 842, 0).create());
        Canvas canvas = page.getCanvas();
        ;
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect destRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawBitmap(bitmap, srcRect, destRect, null);
        doc.finishPage(page);
        File file_pdf = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "seal.pdf");
        try {
            if (file != null) {
                FileOutputStream outputStream = new FileOutputStream(file_pdf, false);
                doc.writeTo(outputStream);
            } else {
                File appSpecificExternalDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "seal.pdf");
                OutputStream outputStream = new FileOutputStream(appSpecificExternalDir);
                doc.writeTo(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.close();
    }
    private void Insertcar_data(){
        //SQLDataFetcherAndExecuter.executeInsertCarDataResult(SQLDataFetcherAndExecuter.fetchMaxIndexOfCarDataTable()+1,"ふ", 460, SQLDataFetcherAndExecuter.check2MatchRegionDataTable("愛媛"),"9001");
        //check2mach一致0不一致1を返す関数を作る
        //インサートの関数を作る（↓４つの変数を引数として）
        //jsonから値をとる
        String Car_number = Number;
        String Car_classify_hiragana = car_classify_hiragana;
        int Car_classify_num = Integer.parseInt(car_classify_num);
        int CarID = SQLDataFetcherAndExecutor.fetchMaxIndexOfCarDataTable();
        int Car_region_id = SQLDataFetcherAndExecutor.check2MatchRegionDataTable(car_region_id);
        int data = SQLDataFetcherAndExecutor.check2MatchCarDataTable(Car_classify_hiragana,Car_classify_num,Car_region_id,Car_number);
        Log.d("test",String.valueOf(data));
        if (data == 0){
            CarID = CarID+1;
            int Car_data = SQLDataFetcherAndExecutor.executeInsertCarDataResult(CarID,Car_classify_hiragana,Car_classify_num,Car_region_id,Car_number);
            Log.d("テストです",String.valueOf(Car_data));
        }
    }
    private void showPDF() {
        File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "seal.jpg");
        try {
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            PdfRenderer.Page page = pdfRenderer.openPage(0);
            Bitmap pdf = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_4444);
            page.render(pdf, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            page.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
