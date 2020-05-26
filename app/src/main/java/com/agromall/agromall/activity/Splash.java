package com.agromall.agromall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.agromall.agromall.R;

import java.util.Calendar;

public class Splash extends AppCompatActivity {

    private static boolean isFirstTimeSplash = true;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ProgressBar progressBar = findViewById(R.id.splashProgress);
        TextView copyRight = findViewById(R.id.tvCopyRight);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                isFirstTimeSplash = false;
                startActivity();
            }
        };

        if(isFirstTimeSplash) {


            int year = Calendar.getInstance().get(Calendar.YEAR);
            String copy_right = String.format("AgroMall. © %s", String.valueOf(year));
            copyRight.setText(copy_right);
            progressBar.setVisibility(View.VISIBLE);

            handler.postDelayed(runnable, 3000);

        } else {
            startActivity();
        }
    }

    private void startActivity() {
        finish();
        startActivity(new Intent(Splash.this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstTimeSplash) finish();
        else if (handler != null)
            handler.postDelayed(runnable, 4000);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isFirstTimeSplash) finish();
        else if (handler != null)
            handler.postDelayed(runnable, 4000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null)
            handler.removeCallbacks(runnable);
    }
}
