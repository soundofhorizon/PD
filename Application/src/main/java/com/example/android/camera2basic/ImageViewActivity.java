package com.example.android.camera2basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageViewActivity extends AppCompatActivity {

    static final int REQUEST_CAPTURE_IMAGE = 100;

    Button button1;
    ImageView imageView1;

    // asset の画像ファイル名
    private final String fileName = "pic.jpg";
    private File file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        findViews();
        setListeners();

        Context context = getApplicationContext();
        // 画像を置く外部ストレージ
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        Log.d("log","path: " + file);

        // text_view： activity_main.xml の TextView の id
        TextView textView = findViewById(R.id.text_view);
        // テキストを設定
        textView.setText(R.string.description_for_read_image);

        setUpWriteExternalStorage();
    }

    private void setUpWriteExternalStorage(){
        Button buttonRead = findViewById(R.id.button_read);
        buttonRead.setOnClickListener( v -> {
            if(isExternalStorageReadable()){
                try(InputStream inputStream0 =
                            new FileInputStream(file) ) {

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream0);
                    imageView1.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    protected void findViews(){
        button1 = (Button)findViewById(R.id.button1);
        imageView1 = (ImageView)findViewById(R.id.imageView1);
    }

    protected void setListeners(){
        button1.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                // 画面遷移用のintentを作成する。基本、「Intent(前の画面, 後の画面)」で書かれる。前の画面はgetApplication();でいいんじゃないか?
                // でも、CameraActivityは実質的にはCamera2BasicFragmentを動かしている。よって、ここに戻るためには、Fragment⇒Activityを行うことになる。
                Intent intent = new Intent(
                        getApplication(), CameraActivity.class);
                startActivityForResult(
                        intent,
                        REQUEST_CAPTURE_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        if(REQUEST_CAPTURE_IMAGE == requestCode
                && resultCode == Activity.RESULT_OK ){
            Bitmap capturedImage =
                    (Bitmap) data.getExtras().get("data");
            imageView1.setImageBitmap(capturedImage);
        }
    }
}