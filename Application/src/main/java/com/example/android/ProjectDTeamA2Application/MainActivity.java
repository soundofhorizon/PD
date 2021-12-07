package com.example.android.ProjectDTeamA2Application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CAPTURE_IMAGE = 100;

    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Bitmap cvs = Bitmap.createBitmap(1080,1993, Bitmap.Config.ARGB_4444);
    Bitmap cvs2 = Bitmap.createBitmap(1080,1993, Bitmap.Config.ARGB_4444);
    Bitmap cvs3 = Bitmap.createBitmap(1080,1993, Bitmap.Config.ARGB_4444);
    Bitmap cvs4 = Bitmap.createBitmap(1080,1993, Bitmap.Config.ARGB_4444);
    ImageView imageView1;

    // asset の画像ファイル名
    private final String fileName = "pic.jpg";
    private File file;

    private static String carNumber ="nothing";
    private static String carNumber_region;
    private static String classify_num;
    private static String classify_hiragana;
    private static String number ;



    private static final String TAG = MainActivity.class.getSimpleName();


    private FirebaseFunctions mFunctions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFunctions = FirebaseFunctions.getInstance();
        setContentView(R.layout.activity_imageview);
        findViews();
        setListeners();
        checkPermission();
        Context context = getApplicationContext();
        // 画像を置く外部ストレージ
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        // text_view： activity_main.xml の TextView の id
        TextView mImageDetails = findViewById(R.id.text_view);
        // テキストを設定。画像更新後、OCR用のString変数として利用。
        mImageDetails.setText(R.string.description_for_read_image);

        setUpWriteExternalStorage();

        Button toAFKInputButton = findViewById(R.id.toAFKInput);
        toAFKInputButton.setOnClickListener( v -> {
            Map<String , String> map = new HashMap<>();
            map.put("carNumber", carNumber);
            try {
                addDataToJson(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, AFKInputActivity.class));
        } );
    }

    private Task<JsonElement> annotateImage(String requestJson) {
        return mFunctions
                .getHttpsCallable("annotateImage")
                .call(requestJson)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    return JsonParser.parseString(new Gson().toJson(Objects.requireNonNull(task.getResult()).getData()));
                });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可を取得する
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
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
        Log.d("debug_addDatatoJsonだよ", json);
    }



    @SuppressLint("SetTextI18n")
    private void setUpWriteExternalStorage(){
        Button buttonRead = findViewById(R.id.button_read);
        buttonRead.setOnClickListener( v -> {
            if(isExternalStorageReadable()){
                try(InputStream inputStream0 = new FileInputStream(file)) {
                    TextView imageDetail = findViewById(R.id.text_view);
                    imageDetail.setText("読み取り中です。結果が表示されるまでそのままお待ちください…");
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream0);
                    findViewById(R.id.button_read).setVisibility(View.INVISIBLE);
                    findViewById(R.id.toAFKInput).setVisibility(View.VISIBLE);
                    // TODO: この時点の写真をgyazoにあげる。
                    Matrix mat = new Matrix();
                    mat.postRotate(90);
                    WindowManager wm = (WindowManager)this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                    Display display = Objects.requireNonNull(wm).getDefaultDisplay();
                    @SuppressLint("DrawAllocation") DisplayMetrics displayMetrics = new DisplayMetrics();
                    display.getMetrics(displayMetrics);
                    // rotate 90 degree and attach gray scale
                    Bitmap bmp_rotate = toGrayscale(Bitmap.createBitmap(bitmap,0,0,4032,3024,mat,true));
                    // and, trimming. widthとheightは計算できるが、まぁ、いいでしょう(ほんとか？)
                    Bitmap bmp = Bitmap.createBitmap(bmp_rotate, 100, 1450, 2800, 1330, null, true);

                    Rect srcRect1 = new Rect(0,0,1400,500);
                    Rect srcRect2 = new Rect(1400, 0, 2800, 500);
                    Rect srcRect3 = new Rect(0,500,630,1330);
                    Rect srcRect4 = new Rect(630, 500, 2800, 1330);

                    // 拡縮を揃えることを考えれば、width 1/2なら、heightも1/2だろう？
                    Rect destRect1 = new Rect(0,0,1400,500);
                    Rect destRect2 = new Rect(0, 0, 1400, 500);
                    Rect destRect3 = new Rect(0,0,630/2,830/2);
                    Rect destRect4 = new Rect(0, 0, 2170/2, 830/2);


                    Canvas canvas = new Canvas(bmp_rotate);
                    canvas.drawBitmap(bmp,srcRect1,destRect1,null);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    cvs.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();
                    String base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                    // Create json request to cloud vision
                    JsonObject request = new JsonObject();
                    // Add image to request
                    JsonObject image = new JsonObject();
                    image.add("content", new JsonPrimitive(base64encoded));
                    request.add("image", image);
                    //Add features to the request
                    JsonObject feature = new JsonObject();
                    feature.add("type", new JsonPrimitive("TEXT_DETECTION"));
                    JsonArray features = new JsonArray();
                    features.add(feature);
                    request.add("features", features);
                    JsonObject imageContext = new JsonObject();
                    JsonArray languageHints = new JsonArray();
                    languageHints.add("ja");
                    imageContext.add("languageHints", languageHints);
                    request.add("imageContext", imageContext);
                    annotateImage(request.toString())
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    // Task failed with an exception
                                    imageDetail.setText("読み取りに失敗しました。エラーは以下の通りです:\n\n" + Objects.requireNonNull(task.getResult()).toString());
                                    carNumber_region = "null";


                                } else {
                                    try {
                                        // Task completed successfully
                                        JsonObject annotation = Objects.requireNonNull(task.getResult()).getAsJsonArray().get(0).getAsJsonObject();
                                        carNumber_region = annotation.get("textAnnotations").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
                                        imageDetail.setText("読み取り結果は以下の通りです。:\n\n" + carNumber_region + "\n\n 「放置態様入力画面に移動」ボタンを押してください。");

                                    }catch(IndexOutOfBoundsException e){
                                        e.printStackTrace();
                                        imageDetail.setText("ナンバープレートが確認できませんでした1");
                                        carNumber_region = "null";

                                    }
                                }
                            });
                    //ocrImage(cvs);

                    Canvas canvas2 = new Canvas(cvs2);
                    canvas2.drawBitmap(bmp,srcRect2,destRect2,null);

                    Canvas canvas3 = new Canvas(cvs3);
                    canvas3.drawBitmap(bmp,srcRect3,destRect3,null);

                    Canvas canvas4 = new Canvas(cvs4);
                    canvas4.drawBitmap(bmp,srcRect4,destRect4,null);


                    imageView1.setImageBitmap(cvs4);//bmp_rotate

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(v -> {
            //ocrImage2(cvs2);
            TextView imageDetail = findViewById(R.id.text_view);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            cvs2.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            // Create json request to cloud vision
            JsonObject request = new JsonObject();
            // Add image to request
            JsonObject image = new JsonObject();
            image.add("content", new JsonPrimitive(base64encoded));
            request.add("image", image);
            //Add features to the request
            JsonObject feature = new JsonObject();
            feature.add("type", new JsonPrimitive("TEXT_DETECTION"));
            JsonArray features = new JsonArray();
            features.add(feature);
            request.add("features", features);
            JsonObject imageContext = new JsonObject();
            JsonArray languageHints = new JsonArray();
            languageHints.add("ja");
            imageContext.add("languageHints", languageHints);
            request.add("imageContext", imageContext);
            annotateImage(request.toString())
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            // Task failed with an exception
                            imageDetail.setText("読み取りに失敗しました。エラーは以下の通りです:\n\n" + Objects.requireNonNull(task.getResult()).toString());
                            classify_num = "null";


                        } else {
                            try {
                                // Task completed successfully
                                JsonObject annotation = Objects.requireNonNull(task.getResult()).getAsJsonArray().get(0).getAsJsonObject();
                                classify_num = annotation.get("textAnnotations").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
                                imageDetail.setText("読み取り結果は以下の通りです。:\n\n" + classify_num + "\n\n 「放置態様入力画面に移動」ボタンを押してください。");


                            }catch(IndexOutOfBoundsException e){
                                e.printStackTrace();
                                imageDetail.setText("ナンバープレートが確認できませんでした2");
                              classify_num = "null";

                            }
                        }
                    });
        });
        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(v -> {
            //ocrImage3(cvs3);
            TextView imageDetail = findViewById(R.id.text_view);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            cvs3.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            // Create json request to cloud vision
            JsonObject request = new JsonObject();
            // Add image to request
            JsonObject image = new JsonObject();
            image.add("content", new JsonPrimitive(base64encoded));
            request.add("image", image);
            //Add features to the request
            JsonObject feature = new JsonObject();
            feature.add("type", new JsonPrimitive("TEXT_DETECTION"));
            JsonArray features = new JsonArray();
            features.add(feature);
            request.add("features", features);
            JsonObject imageContext = new JsonObject();
            JsonArray languageHints = new JsonArray();
            languageHints.add("ja");
            imageContext.add("languageHints", languageHints);
            request.add("imageContext", imageContext);
            annotateImage(request.toString())
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            // Task failed with an exception
                            imageDetail.setText("読み取りに失敗しました。エラーは以下の通りです:\n\n" + Objects.requireNonNull(task.getResult()).toString());
                            classify_hiragana = "null";


                        } else {
                            try {
                                // Task completed successfully
                                JsonObject annotation = Objects.requireNonNull(task.getResult()).getAsJsonArray().get(0).getAsJsonObject();
                                classify_hiragana = annotation.get("textAnnotations").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();

                                imageDetail.setText("読み取り結果は以下の通りです。:\n\n" + classify_hiragana + "\n\n 「放置態様入力画面に移動」ボタンを押してください。");



                            }catch(IndexOutOfBoundsException e){
                                e.printStackTrace();
                                imageDetail.setText(Objects.requireNonNull(task.getResult()).getAsJsonArray().toString());
                                classify_hiragana = "null";

                            }
                        }
                    });
        });
        button4 = findViewById(R.id.button4);
        button4.setOnClickListener(v -> {
            //ocrImage4(cvs4);
            TextView imageDetail = findViewById(R.id.text_view);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            cvs4.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            // Create json request to cloud vision
            JsonObject request = new JsonObject();
            // Add image to request
            JsonObject image = new JsonObject();
            image.add("content", new JsonPrimitive(base64encoded));
            request.add("image", image);
            //Add features to the request
            JsonObject feature = new JsonObject();
            feature.add("type", new JsonPrimitive("TEXT_DETECTION"));
            JsonArray features = new JsonArray();
            features.add(feature);
            request.add("features", features);
            JsonObject imageContext = new JsonObject();
            JsonArray languageHints = new JsonArray();
            languageHints.add("ja");
            imageContext.add("languageHints", languageHints);
            request.add("imageContext", imageContext);
            annotateImage(request.toString())
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            // Task failed with an exception
                            imageDetail.setText("読み取りに失敗しました。エラーは以下の通りです:\n\n" + Objects.requireNonNull(task.getResult()).toString());
                            number = "null";

                        } else {
                            try {
                                // Task completed successfully
                                JsonObject annotation = Objects.requireNonNull(task.getResult()).getAsJsonArray().get(0).getAsJsonObject();
                                number = annotation.get("textAnnotations").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();

                                //imageDetail.setText("読み取り結果は以下の通りです。:\n\n" + number + "\n\n 「放置態様入力画面に移動」ボタンを押してください。");

                                imageDetail.setText(carNumber_region+","+classify_num+","+classify_hiragana+","+number);


                            }catch(IndexOutOfBoundsException e){
                                e.printStackTrace();
                                imageDetail.setText(Objects.requireNonNull(task.getResult()).getAsJsonArray().toString());
                                number = "null";

                            }
                        }
                    });
        });


    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    protected void findViews(){
        button1 = findViewById(R.id.button1);
        imageView1 = findViewById(R.id.imageView1);
    }

    protected void setListeners(){

        findViewById(R.id.button_read).setVisibility(View.INVISIBLE);
        findViewById(R.id.toAFKInput).setVisibility(View.INVISIBLE);
        button1.setOnClickListener(v -> {
            // 画面遷移用のintentを作成する。基本、「Intent(前の画面, 後の画面)」で書かれる。前の画面はgetApplication();でいいんじゃないか?
            // でも、CameraActivityは実質的にはCamera2BasicFragmentを動かしている。よって、ここに戻るためには、Fragment⇒Activityを行うことになる。
            Intent intent = new Intent(
                    getApplication(), CameraActivity.class);
            startActivityForResult(
                    intent,
                    REQUEST_CAPTURE_IMAGE);

            findViewById(R.id.button_read).setVisibility(View.VISIBLE);
            findViewById(R.id.toAFKInput).setVisibility(View.INVISIBLE);
            findViewById(R.id.button1).setVisibility(View.GONE);
        });
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
}