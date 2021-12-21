package com.tcd.paintsplat.group5;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

public class CanvasActivity extends AppCompatActivity {
    public static long milliseconds; // timer mililseconds
    private CanvasScreen canvasScreen; // instance of the canvas screen
    private float speed; // speed by which the rect would move
    private long tempTimer; // tempTimer to increase the speed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        canvasScreen = findViewById(R.id.screen_canvas);
        final TextView timerTV = findViewById(R.id.timer_txt);
        TextView scoreTV = findViewById(R.id.score_txt);
        canvasScreen.setBackgroundColor(getResources().getColor(R.color.colorBG));

        speed = 2.0f;
        //TODO:get timer from db
        final long timerMili = 120000; //2mins
        tempTimer = timerMili;
        new CountDownTimer(timerMili, 1000) {
            public void onTick(long millisUntilFinished) {
                milliseconds = millisUntilFinished;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("mm:ss");
                String time = df.format(new Date(milliseconds));
                timerTV.setText(time);
                //increase the speed as the timer decreases
                if (millisUntilFinished <= ((long) (tempTimer * 0.9))) {
                    tempTimer = millisUntilFinished;
                    speed *= 2.0f;
                    //max limit for speed
                    if (speed > 10.f) {
                        speed = 10.f;
                    }
                }
            }

            public void onFinish() {
//                TODO: game over
            }
        }.start();

        Timer movementTimer = new Timer();  // a timer to start the game and the movement of the canvas
        movementTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                canvasScreen.moveBitmap(speed, 5.0f);
                canvasScreen.invalidate();   //Calls screen update
            }
        }, 0, 30);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}