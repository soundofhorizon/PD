package com.example.cameratest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.AutoFocusCallback {
    private final SurfaceHolder holder;
    private Camera camera;

    public CameraView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
            camera.autoFocus(this);
        }
        return true;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        camera.autoFocus(null);
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            // bitmap画像をカットする処理など
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            // 中央部分の四角形をカット
            bitmap = cutBitmap(bitmap, (w-h)/2, 0, h, h);
            // 90度回転
            bitmap = rotateBitmap90(bitmap);

            // とりあえずもう一度カメラプレビュー
            camera.startPreview();
        } catch (Exception e) {
            Log.d("error","onPictureTaken()");
        }
    }

    /*
     * カット
     */
    public Bitmap cutBitmap(Bitmap bmp,int x,int y,int w,int h) {
        Bitmap result=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(result);
        canvas.drawBitmap(bmp,-x,-y,null);
        return result;
    }
    /*
     * 画像の回転 90度
     */
    public Bitmap rotateBitmap90(Bitmap bmp) {
        int w=bmp.getWidth();
        int h=bmp.getHeight();
        Bitmap result=Bitmap.createBitmap(h,w,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(result);
        canvas.rotate(90,0,0);
        canvas.drawBitmap(bmp,0,-h,null);
        return result;
    }
}