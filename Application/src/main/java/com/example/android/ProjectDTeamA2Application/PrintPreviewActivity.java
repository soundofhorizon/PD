package com.example.android.ProjectDTeamA2Application;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrintPreviewActivity extends MainActivity{
    File file;
    ImageView imageView1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printpreview);
        imageView1 = findViewById(R.id.seal_view);
        // text_view： activity_main.xml の TextView の id
        TextView mImageDetails = findViewById(R.id.text_view);
        // テキストを設定。画像更新後、OCR用のString変数として利用。
        mImageDetails.setText(R.string.description_for_create_pdf);
        // printPDFボタンを押されたらPDFを発行する。それで終わり。
        findViewById(R.id.printPDF).setOnClickListener(v1 -> {
            Log.d("push_pdf_button", "PDF発行関数をここに挿入してタイトルへ差し戻す");
        });
        copyFile();
    }
    private void copyFile(){
        Context context = getApplicationContext();
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"seal.jpg");
        Resources r = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.seal_base);
        imageView1.setImageBitmap(bmp);
        try{
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
