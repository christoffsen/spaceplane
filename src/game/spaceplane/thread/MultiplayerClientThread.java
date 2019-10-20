package game.spaceplane.thread;

import game.spaceplane.activity.GameOverActivity;
import game.spaceplane.activity.MultiplayerActivity;
import game.spaceplane.drawing.Sprite;
import game.spaceplane.physics.AsteroidL;
import game.spaceplane.physics.Body;
import game.spaceplane.physics.GunAttack;
import game.spaceplane.physics.Spaceship;
import game.spaceplane.view.MultiplayerClientView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class MultiplayerClientThread extends Thread implements OnTouchListener {
    private static final String TAG = MultiplayerHostThread.class
            .getSimpleName();

    private SurfaceHolder surfaceHolder;
    private MultiplayerClientView clientView;

    private boolean isAccelerating = false;
    private boolean isFiring = false;
    private CopyOnWriteArrayList<Boolean> RotAccelFire;

    /*
     * Display for getting window dimensions
     */
    int width, height;
    WindowManager wm;
    Display display;

    public Spaceship shipOne, // Host
            shipTwo; // Client
    public List<Body> bodies;

    /*
     * Number of asteroids and stuff
     */
    private final int MAX_ASTEROIDS = 5;
    private final int MIN_ASTEROIDS = 1;
    private int numAsteroids = 0;

    private int[] scores;

    private boolean running = false;

    public MultiplayerClientThread(SurfaceHolder surfaceHolder,
            MultiplayerClientView mcv, Context context) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.clientView = mcv;

        // Get the display dimensions
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        // width = this.gameView.getWidth();
        // height = this.gameView.getHeight();

        shipOne = new Spaceship(width / 2, height / 2, 1);
        shipTwo = new Spaceship(width / 2, height / 2, 0);

        bodies = new CopyOnWriteArrayList<Body>();
        bodies.add(shipOne);
        bodies.add(shipTwo);

        RotAccelFire = new CopyOnWriteArrayList<Boolean>();

        scores = new int[2];

        numAsteroids = 0;
        this.clientView.setOnTouchListener((View.OnTouchListener) this);
        Log.d(TAG, "Created a host thread..");
    }

    /**
     * Let the thread know that it's running
     * 
     * @param running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * 
     * @return The flag for whether or not the thread is running
     */
    public boolean getRunning() {
        return running;
    }

    @Override
    public void run() {
        Canvas canvas;

        while (running) {
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    // the server updates physics
                    List<Body> temp = ((MultiplayerActivity) this.clientView
                            .getContext()).getNetwork().getServerState();
                    if (temp != null)
                        bodies = temp;
                    if (bodies != null) {
                        this.clientView.purgeSprites();
                        updateShips();
                        // getSomeAsteroids();
                        updateBullets();
                    }
                    // Check if the game is over
                    if (shipOne != null && shipTwo != null) {
                        if (shipOne.gameOver() || shipTwo.gameOver()) {
                            break;
                        }
                    }

                    ((MultiplayerActivity) this.clientView.getContext())
                            .getNetwork().sendInputToServer(
                                    this.clientView.isRotating(),
                                    isAccelerating, isFiring);
                    
                    isFiring = false;
                    isAccelerating = false;
                    this.clientView.setRotating(Boolean.valueOf(null));

                    this.clientView.update();
                    this.clientView.render(canvas);
                    this.clientView.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void updateBullets() {
        for (Body b : bodies) {
            if (b.isGun()) {
                if (((GunAttack) b).getOwner() == 0) {
                    Sprite aBody = new Sprite(
                            this.clientView.getBulletOneBitmap(), (int) b
                                    .getPosition().getHead().X, (int) b
                                    .getPosition().getHead().Y);
                    this.clientView.addBulletSprite(aBody);
                } else if (((GunAttack) b).getOwner() == 1) {
                    Sprite aBody = new Sprite(
                            this.clientView.getBulletTwoBitmap(), (int) b
                                    .getPosition().getHead().X, (int) b
                                    .getPosition().getHead().Y);
                    this.clientView.addBulletSprite(aBody);
                }
            }
        }
    }

    public void onStop() {
        Intent intent = new Intent(clientView.getContext(),
                GameOverActivity.class);
        intent.putExtra("PlayerScores", scores);
        clientView.getContext().startActivity(intent);
        this.setRunning(false);
    }

    public void setNumAsteroids(int asteroids) {
        this.numAsteroids = asteroids;
    }

    public int getNumAsteroids() {
        return this.numAsteroids;
    }

    public void getSomeAsteroids() {
        for (Body b : bodies) {
            if (!(b.isShip() || b.isGun())) {
                Sprite aBody = new Sprite(this.clientView.getAsteroidBitmap(),
                        (int) b.getPosition().getHead().X, (int) b
                                .getPosition().getHead().Y);
                this.clientView.addAsteroidSprite(aBody);
            }
        }

    }

    public void updateShips() {
        for (Body b : bodies) {
            if (b.isShip()) {
                // If it's the host ship
                if (((Spaceship) b).getPlayerNum() == 0) {
                    shipOne = (Spaceship) b;
                }

                // If it's the client ship
                else if (((Spaceship) b).getPlayerNum() == 1) {
                    shipTwo = (Spaceship) b;
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            break;
        case MotionEvent.ACTION_MOVE:
            isAccelerating = true;

            //shipOne.moveForward();
            break;
        case MotionEvent.ACTION_UP:
            isFiring = true;
            // touch was released, fire the weapon!
            // create a gunattack object
            /*
            if (this.clientView.getBulletCount() < 5 && !isAccelerating) {
                GunAttack ga = new GunAttack(shipOne);
                this.clientView.createBullet(ga);
            }
            */
            isAccelerating = false;
            break;
        }
        return true;
    }

}
