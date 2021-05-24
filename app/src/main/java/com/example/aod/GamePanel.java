package com.example.aod;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    public static final int WIDTH = 683;
    public static final int HEIGHT = 384;// CANVAS
    public static final int MOVESPEED = -7;// MOVEMENT SPEED
    private long missileStartTime;
    private long coinStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Missile> missiles;
    private ArrayList<Coin> coins;
    private Random rand = new Random();
    private boolean newGameCreated;
    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean disappear;
    private boolean started;
    private int best = 0;
    private int ctr=0;

    public GamePanel(Context context) {//add callback and set focusable true
        super(context);
        getHolder().addCallback(this); //add the callback to the surfaceholder to intercept events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {//instantiate and start the thread running
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.bg03));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.ufo1), 128, 56, 3);
        coins = new ArrayList<Coin>();
        coinStartTime = System.nanoTime();
        missiles = new ArrayList<Missile>();
        missileStartTime = System.nanoTime();
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {//when application sudden closed or closed it will stop the thread running
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000) {
            try {
                counter++;
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {// to start game
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!player.getPlaying() && newGameCreated && reset) {//to start the game
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.getPlaying()) {// updates the up and down (vertical position of the player)
                if(!started) started = true;
                reset = false;
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {//if wala na ga tap
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        if(player.getPlaying()) {
            bg.update();
            player.update();
            if(player.getScore()/500 >this.ctr){//change background sa score every divisible by 700
                this.ctr++;
                Bitmap img1=BitmapFactory.decodeResource(getResources(), R.drawable.bg01);
                Bitmap img2=BitmapFactory.decodeResource(getResources(), R.drawable.bg02);
                Bitmap img3=BitmapFactory.decodeResource(getResources(), R.drawable.bg03);
                Bitmap img4=BitmapFactory.decodeResource(getResources(), R.drawable.bg04);
                int rnd = rand.nextInt(5 - 1) + 1;
                switch (rnd){
                    case 1:
                        bg.setBackground(img1);
                        break;
                    case 2:
                        bg.setBackground(img2);
                        break;
                    case 3:
                        bg.setBackground(img3);
                        break;
                    case 4:
                        bg.setBackground(img4);
                        break;
                }
            }
            if(player.getY() > HEIGHT || player.getY() < -player.getHeight()) {// if malapas sa canvas
                player.setPlaying(false);
            }
            //add missiles on timer
            long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;// set timer for every missile to be produce
            if (missileElapsed > (1500 - player.getScore() / 4)) {//if time for missile to be produce
                //first missile always goes down the middle
                if (missiles.size() == 0) {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), WIDTH + 10, HEIGHT / 2, 45, 15, player.getScore(), 13));
                } else {

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile), WIDTH + 10, (int) (rand.nextDouble() * (HEIGHT-30)), 45, 15, player.getScore(), 13));
                }
                //reset timer
                missileStartTime = System.nanoTime();
            }

            //loop through every missile and check collision and remove
            for (int i = 0; i < missiles.size(); i++) {//check all missiles that has been produced
                //update missile
                missiles.get(i).update();
                if (collision(missiles.get(i), player)) {//if collision of missile
                    missiles.remove(i);
                    player.setPlaying(false);//game over
                    break;
                }
                //remove missile if it is way off the screen
                if (missiles.get(i).getX() < -100) {//if missile out of bounds
                    missiles.remove(i);//remove
                    break;
                }
            }
            long coinElapsed = (System.nanoTime() - coinStartTime) / 1000000;//same algo sa missile
            if(coinElapsed > (5000 - player.getScore() / 4)) {
                coins.add(new Coin(BitmapFactory.decodeResource(getResources(), R.drawable.coin), WIDTH + 10, (int) (rand.nextDouble() * (HEIGHT-30)), 40, 50, player.getScore(), 6));
                coinStartTime = System.nanoTime();
            }
            for(int i = 0; i < coins.size(); i ++) {
                coins.get(i).update();
                if (collision(coins.get(i), player)) {
                    coins.remove(i);
                    player.setScore(player.getScore() + 50);
                    break;
                }
                if(coins.get(i).getX() < -100) {
                    coins.remove(i);
                    break;
                }
            }

        }
        else {
            player.resetDY();//reset in  the middle
            if(!reset) {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion),
                        player.getX(), player.getY() - 30, 100, 100, 25);
            }
            explosion.update();
            long resetElapsed = (System.nanoTime() - startReset) / 1000000;
            if(resetElapsed > 2000 && !newGameCreated) {
                newGame();
            }
        }
    }

    public boolean collision(GameObject a, GameObject b) {
        if(Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float scaleFactorX = getWidth() / (WIDTH * 1.f);//screen ratio
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);
        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            if(!disappear) {
                player.draw(canvas);
            }
            for(Missile m : missiles) {
                m.draw(canvas);
            }
            for(Coin c:coins){
                c.draw(canvas);
            }
            if(started) {
                explosion.draw(canvas);
            }
            drawText(canvas);

            canvas.restoreToCount(savedState);
        }
    }

    public void newGame() {
        disappear = false;
        missiles.clear();
        coins.clear();
        this.ctr=0;
        player.resetDY();
        player.setY(HEIGHT / 2);
        bg.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.bg03));
        if(player.getScore() > best) {//get best score
            best = player.getScore();
        }
        player.resetScore();
        newGameCreated = true;
    }

    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(15);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("SCORE: " + player.getScore(), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best, WIDTH / 2, HEIGHT - 10, paint);
        canvas.drawText("FPS: " + thread.getAverageFPS(), 10, 20, paint);

        if(!player.getPlaying() && newGameCreated && reset) {
            Paint paint1 = new Paint();
            paint1.setColor(Color.WHITE);
            paint1.setTextSize(30);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH / 2 - 70, HEIGHT / 2, paint1);
            paint1.setTextSize(18);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH / 2 - 70, HEIGHT / 2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH / 2 - 70, HEIGHT / 2 + 40, paint1);
        }
    }
}
