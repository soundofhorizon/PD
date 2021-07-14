package com.example.cameratest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class CameraOverlayView extends View {
    private int width, height;

    public CameraOverlayView(Context context) {
        super(context);
        setFocusable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        // ビューのサイズを取得
        width= w;
        height= h;
    }


    /**
     * 描画処理
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 背景色を設定
        canvas.drawColor(Color.TRANSPARENT);

        // 描画するための線の色を設定
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(100, 0, 0, 0);

        // 上枠表示
        canvas.drawRect(0, 0, (width-height)/2, height, paint);
        // 下枠表示
        canvas.drawRect((width-height)/2 + height, 0, width, height, paint);

        // 中央十字憑依時
        int len = height / 10;
        paint.setARGB(255, 255, 0, 0);
        canvas.drawLine(width/2, height/2-len, width/2, height/2+len, paint);
        canvas.drawLine(width/2-len, height/2, width/2+len, height/2, paint);
        // 円表示
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(width/2, height/2, len*5, paint);
    }
}