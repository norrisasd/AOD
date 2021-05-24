package com.example.aod;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillSec;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/FPS;

        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception ex) {}
            finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            timeMillSec = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillSec;

            try {
                this.sleep(waitTime);
            } catch (Exception ex) {}

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public double getAverageFPS() {
        return averageFPS;
    }
}
