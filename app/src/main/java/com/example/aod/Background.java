package com.example.aod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Background {
    private Bitmap image;
    private int x;
    private int y;
    private int dx;

    public Background(Bitmap image) {
        this.image = image;
        dx = GamePanel.MOVESPEED;
    }

    public void update() {
        x += dx;
        if(x < -GamePanel.WIDTH) {
            x = 0;
        }
    }
    public void setBackground(Bitmap image){
        this.image = image;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
        if(x < 0) {
            canvas.drawBitmap(image, x + GamePanel.WIDTH, y, null);
        }
    }

}
