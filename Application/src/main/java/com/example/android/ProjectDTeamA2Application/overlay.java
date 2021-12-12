package com.example.android.ProjectDTeamA2Application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Objects;

public class overlay extends View {
    private Paint paint = new Paint();

    overlay(Context context) {
        super(context);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) { // Override the onDraw() Method
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10);

        @SuppressLint("DrawAllocation") Paint mDotPaint = new Paint();
        mDotPaint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0)); // 5pixel描いたら5pixel描かないを繰り返す
        mDotPaint.setStyle(Paint.Style.STROKE); // スタイルは線(Stroke)を指定する
        mDotPaint.setStrokeWidth(10); // 線の太さ
        mDotPaint.setColor(Color.GREEN); // 線の色

        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = Objects.requireNonNull(wm).getDefaultDisplay();
        @SuppressLint("DrawAllocation") DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        //center
        canvas.drawRect(50, 500, 1050, 1000, paint);
        canvas.drawLine(50, 700, 1050, 700, mDotPaint);
        canvas.drawLine(550, 500, 550, 700, mDotPaint);
        canvas.drawLine(250, 700, 250, 1000, mDotPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

}
