package com.example.cameratest;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.Manifest;
//import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;

    private final static int RESULT_CAMERA = 1001;
    private ImageView imageView;
    private Uri cameraUri;
    static final int REQUEST_TAKE_PHOTO = 1;

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.JAPANESE).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug","onCreate()");

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new CameraView(this));
        setContentView(R.layout.activity_main);
        addContentView(new Box(this), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        imageView = findViewById(R.id.image_view);

        Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener( v -> {
            if(isExternalStorageWritable()){
                cameraIntent();
            }
        });

    }
    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 権限がない場合、許可ダイアログ表示
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);       // 位置情報更新間隔の希望
        locationRequest.setFastestInterval(5000); // 位置情報更新間隔の最速値
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest,  new MyLocationCallback(), null);
    }
    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(@NotNull LocationResult locationResult) {
            // 現在値を取得
            Location location = locationResult.getLastLocation();
            TextView view = findViewById(R.id.text_view);
            view.setText("緯度:" + location.getLatitude() + " 経度:" + location.getLongitude());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置情報取得開始
            startUpdateLocation();
        }
    }

    private boolean isExternalStorageWritable() {
                String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    private void cameraIntent(){

        Context context = getApplicationContext();
        // 保存先のフォルダー
        File cFolder = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        Log.d("log","path: " + cFolder);

        String fileDate = new SimpleDateFormat(
                "ddHHmmss", Locale.US).format(new Date());
        // ファイル名
        String fileName = String.format("CameraIntent_%s.jpg", fileDate);

        File cameraFile = new File(cFolder, fileName);

        cameraUri = FileProvider.getUriForFile(
                MainActivity.this,
                context.getPackageName() + ".fileprovider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);

        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("Error on saving Image","createImageFile()");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        context.getPackageName() + ".fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
        Log.d("debug","startActivityForResult()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RESULT_CAMERA) {

            if(cameraUri != null && isExternalStorageReadable()){
                imageView.setImageURI(cameraUri);
            }
            else{
                Log.d("debug","cameraUri == null");
            }
        }
        startUpdateLocation();
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

}