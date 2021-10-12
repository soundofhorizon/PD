package com.example.android.ProjectDTeamA2Application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Objects;

public class overlay extends View {
    private Paint paint = new Paint();
    overlay(Context context)
    {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) { // Override the onDraw() Method
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10);

        WindowManager wm = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = Objects.requireNonNull(wm).getDefaultDisplay();
        @SuppressLint("DrawAllocation") DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);


        //center
        canvas.drawRect(display.getWidth()/2 -500, display.getHeight()/2 -400 , 450 + display.getWidth() / 2, display.getHeight()/2 + 160 , paint);

    }

    public int dp2px(float dp,Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

}
