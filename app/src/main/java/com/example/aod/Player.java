package com.example.aod;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Player extends GameObject {
    private Bitmap spritesheet;
    private int score;
    private boolean up;
    private boolean playing;
    private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap res, int w, int h, int numFrames) {
        x = 50;
        y = GamePanel.HEIGHT / 2;
        dy = 0;
        score = 0;
        width = w;
        height = h;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for(int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setUp(boolean u) {
        up = u;
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if(elapsed > 100) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        if(up) {
            dy -= 3;
        } else {
            dy += 3;
        }

        if(dy > 14) dy = 14;
        if(dy < -14) dy = -14;

        y += dy*2;
        dy = 0;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }

    public int getScore() {
        return score;
    }

    public boolean getPlaying() {
        return playing;
    }

    public void setPlaying(boolean p) {
        playing = p;
    }

    public void resetDY() {
        dy = 0;
    }

    public void resetScore() {
        score = 0;
    }
}
