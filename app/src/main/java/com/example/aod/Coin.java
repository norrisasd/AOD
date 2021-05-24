package com.example.aod;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Coin extends GameObject {
    private int dx;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Coin (Bitmap res, int x, int y, int w, int h, int s, int numFrames) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        dx=GamePanel.MOVESPEED;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for(int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void update() {
        x+=dx;
        animation.update();
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(), x, y,null);
        } catch(Exception ex) {}
    }

    public int getWidth() {
        //offset slightly for more realistic collision detection
        return width - 10;
    }
}
