package com.example.aod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Story extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_story);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(7000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    Intent i = new Intent(Story.this, Game.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        thread.start();

    }
}