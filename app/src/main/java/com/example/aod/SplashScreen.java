package com.example.aod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(4000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    Intent i = new Intent(SplashScreen.this, Story.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        thread.start();


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}