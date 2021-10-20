package com.example.android.ProjectDTeamA2Application;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PrintPreviewActivity extends MainActivity{
    File file;
    Context context = getApplicationContext();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyFile();
    }
    private void copyFile(){
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"seal");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(R.drawable.seal_base);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
