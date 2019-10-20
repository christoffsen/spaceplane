package game.spaceplane.thread;

import game.spaceplane.activity.GameOverActivity;
import game.spaceplane.activity.MultiplayerActivity;
import game.spaceplane.drawing.Sprite;
import game.spaceplane.physics.AsteroidL;
import game.spaceplane.physics.Body;
import game.spaceplane.physics.Engine;
import game.spaceplane.physics.GunAttack;
import game.spaceplane.physics.Spaceship;
import game.spaceplane.physics.Vector;
import game.spaceplane.view.MultiplayerHostView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
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

public class MultiplayerHostThread extends Thread implements OnTouchListener {

    private static final String TAG = MultiplayerHostThread.class
            .getSimpleName();

    private SurfaceHolder surfaceHolder;
    private MultiplayerHostView hostView;

    /*
     * physics engine objects
     */
    public Engine physics;
    public Spaceship shipOne, shipTwo;
    public List<Body> bodies;
    private CopyOnWriteArrayList<Boolean> RotAccelFire;

    private boolean isAccelerating = false;

    private int direction = 0;

    /*
     * Display for getting window dimensions
     */
    int width, height;
    WindowManager wm;
    Display display;

    /*
     * Number of asteroids and stuff
     */
    private final int MAX_ASTEROIDS = 5;
    private final int MIN_ASTEROIDS = 1;
    private int numAsteroids = 0;

    private boolean running = false;
    
    
    /*
     * FPS
     */
    private final static int MAX_FPS = 50;
    // max number of frames to be skipped
    private final static int MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;

    private DecimalFormat df = new DecimalFormat("0.##"); // 2 decimal places
    // reading every second
    private final static int STAT_INTERVAL = 1000; // ms
    // the last n FPSs
    private final static int FPS_HISTORY_NR = 10;
    // the last time the status was stored
    private long lastStatusStore = 0;
    // the status time counter
    private long statusIntervalTimer = 0l;
    // number of frames skipped since the game started
    private long totalFramesSkipped = 0l;
    // number of frames skipped in a store cycle(1 sec)
    private long framesSkippedPerStatCycle = 0l;

    // number of rendered frames in an interval
    private int frameCountPerStatCycle = 0;
    private long totalFrameCount = 0l;
    // the last FPS values
    private double fpsStore[];
    // number of times the stat has been read
    private long statsCount = 0;
    // the average FPS since the game started
    private double averageFps = 0.0;
    
    

    /**
     * The threads constructor which initialises the surface holder, game view,
     * ship, bodies, physics engine, gets the displays width and height, and
     * gets the sensor manager.
     * 
     * @param surfaceHolder
     * @param gameView
     * @param context
     */
    public MultiplayerHostThread(SurfaceHolder surfaceHolder,
            MultiplayerHostView mhv, Context context) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.hostView = mhv;

        // Get the display dimensions
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        // width = this.gameView.getWidth();
        // height = this.gameView.getHeight();

        shipOne = new Spaceship(width / 2, height / 2, 0);
        shipTwo = new Spaceship(width / 2, height / 2, 1);

        bodies = new CopyOnWriteArrayList<Body>();
        bodies.add(shipOne);
        bodies.add(shipTwo);

        RotAccelFire = new CopyOnWriteArrayList<Boolean>();

        physics = new Engine(bodies, 2);
        numAsteroids = 0;
        this.hostView.setOnTouchListener((View.OnTouchListener) this);
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
        
        initTimingElements();
        
        long beginTime; // the time when the cycle began
        long timeDiff; // the time it took for the cycle to execute
        int sleepTime = 0; // ms to sleep, if < 0 we're behind
        int framesSkipped;


        while (running) {
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0; // reset the # frames skipped
                    
                    // GET THE CLIENTS UPDATED POSITIONS
                    RotAccelFire =
                            ((MultiplayerActivity) this.hostView.getContext()).getNetwork().getClientInput();

                    if (RotAccelFire.get(0) != null) {
                        if (RotAccelFire.get(0) == true) {
                            direction -= 1;
                        } else if (RotAccelFire.get(0) == false) {
                            direction += 1;
                        }
                        shipTwo.setForward((double) direction
                                * (Math.PI / 180.0));
                    }
                    if (RotAccelFire.get(1) == true) {
                        hostView.thrustShipTwo();
                        shipTwo.moveForward();
                    } else {
                        hostView.stopShipTwo();
                    }
                    if (RotAccelFire.get(2) == true) {
                        Log.d(TAG, "Player 2 bullets: " + hostView.getPlayerTwoBullets());
                        if (this.hostView.getPlayerTwoBullets() < 5) {
                         
                            GunAttack ga = new GunAttack(shipTwo);
                            this.hostView.createBullet(ga);
                            bodies.add(ga);
                            hostView.incPlayerTwoBullets();
                            Log.d(TAG, "Created a player 2 bullet");
                        }
                    }

                    if (this.hostView.getAsteroidsSize() < MIN_ASTEROIDS) {
                        makeSomeAsteroids(canvas.getWidth(), canvas.getHeight());
                    }
                    // updates the clients ship position, and rotation

                    physics.update(bodies);

                    ((MultiplayerActivity) this.hostView.getContext()).getNetwork().sendUpdatesToClients(bodies);

                    if (shipOne.gameOver() || shipTwo.gameOver()) {
                        break;
                    }
                    this.hostView.update();
                    this.hostView.render(canvas);
                    this.hostView.draw(canvas);
                    
                    
                 // how long did the cycle take?
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int) (FRAME_PERIOD - timeDiff);

                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException ex) {
                        }
                    }
                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        // we need to catch up
                        // update without rendering
                        physics.update(bodies);
                        this.hostView.update();
                        // add frame period to check if in next frame
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }
                    if (framesSkipped > 0)
                        Log.d(TAG, "Skipped: " + framesSkipped);
                    framesSkippedPerStatCycle += framesSkipped;
                    storeStats();

                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        Log.d(TAG, "STOPPED GAME LOOP");
        onStop();
    }

    public void onStop() {
        Intent intent = new Intent(hostView.getContext(),
                GameOverActivity.class);
        intent.putExtra("PlayerScores", physics.getScores());
        hostView.getContext().startActivity(intent);
        this.setRunning(false);
        Log.d(TAG, "Thread stopped");
    }

    public void setNumAsteroids(int asteroids) {
        this.numAsteroids = asteroids;
    }

    public int getNumAsteroids() {
        return this.numAsteroids;
    }

    private void makeSomeAsteroids(int width, int height) {
//        for (int i = 0; i < MAX_ASTEROIDS; i++) {
//            Random r = new Random();
//            int x = r.nextInt(width - 5 + 1) + 5;
//            int y = r.nextInt(height - 5 + 1) + 5;
//            AsteroidL a = new AsteroidL(x, y);
//
//            a.setPosition(new Vector(r.nextInt(600), r.nextInt(600)));
//            bodies.add(a);
//            Sprite aBody = new Sprite(this.hostView.getAsteroidBitmap(),
//                    (int) a.getPosition().getHead().X, (int) a.getPosition()
//                            .getHead().Y);
//            this.hostView.addAsteroidSprite(aBody);
//        }
    	AsteroidL a = new AsteroidL(300, 300);
		AsteroidL b = new AsteroidL(500, 200);
		AsteroidL c = new AsteroidL(200, 400);
		AsteroidL d = new AsteroidL(200, 500);
		
		a.setPosition(new Vector(0, 0));
		b.setPosition(new Vector(300, 200));
		c.setPosition(new Vector(100, 500));
		d.setPosition(new Vector(500, 600));
		
		
		a.setVelocity(new Vector (1, -1));
		b.setVelocity(new Vector(-1, 1));
		c.setVelocity(new Vector(-1, -1));
		d.setVelocity(new Vector(1,1));
		


		bodies.add(a);
		bodies.add(b);
		bodies.add(c);
		bodies.add(d);

		Sprite aBody = new Sprite(this.hostView.getAsteroidBitmap(), (int) a
				.getPosition().getHead().X, (int) a.getPosition().getHead().Y);
		Sprite bBody = new Sprite(this.hostView.getAsteroidBitmap(), (int) b
				.getPosition().getHead().X, (int) b.getPosition().getHead().Y);
		Sprite cBody = new Sprite(this.hostView.getAsteroidBitmap(), (int) c
				.getPosition().getHead().X, (int) c.getPosition().getHead().Y);
		Sprite dBody = new Sprite(this.hostView.getAsteroidBitmap(), (int) d
				.getPosition().getHead().X, (int) d.getPosition().getHead().Y);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.d(TAG, "TOUCH HAPPENED");
            break;
        case MotionEvent.ACTION_MOVE:
            isAccelerating = true;

            shipOne.moveForward();
            hostView.thrustShipOne();
            break;
        case MotionEvent.ACTION_UP:
            Log.d(TAG, "Player 1 bullets: " + hostView.getPlayerOneBullets());
            // touch was released, fire the weapon!
            // create a gunattack object
            if (this.hostView.getPlayerOneBullets() < 5 && !isAccelerating) {
                GunAttack ga = new GunAttack(shipOne);
                this.hostView.createBullet(ga);
                bodies.add(ga);
                hostView.incPlayerOneBullets();
            }
            isAccelerating = false;
            hostView.stopShipOne();
            break;
        }
        return true;
    }

    
    private void storeStats() {
        frameCountPerStatCycle++;
        totalFrameCount++;

        // check the time
        statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);
        if (statusIntervalTimer >= lastStatusStore + STAT_INTERVAL) {
            // calculate the actual frames per status check interval
            double actualFps = (double) (frameCountPerStatCycle / (STAT_INTERVAL / 1000));

            // stores the latest fps in the array
            fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;

            // increase the umber of times statistics was calculated
            statsCount++;

            double totalFps = 0.0;
            // sum up the stored fps values
            for (int i = 0; i < FPS_HISTORY_NR; i++) {
                totalFps += fpsStore[i];
            }

            // obtain the average
            if (statsCount < FPS_HISTORY_NR) {
                // in case of the first 10 triggers
                averageFps = totalFps / statsCount;
            } else {
                averageFps = totalFps / FPS_HISTORY_NR;
            }
            // save the number of total frames skipped
            totalFramesSkipped += framesSkippedPerStatCycle;
            // reset the counters after a status record (1 sec)
            framesSkippedPerStatCycle = 0;
            statusIntervalTimer = 0;
            frameCountPerStatCycle = 0;

            statusIntervalTimer = System.currentTimeMillis();
            lastStatusStore = statusIntervalTimer;
            hostView.setAvgFps("FPS: " + df.format(averageFps));
        }
    }

    private void initTimingElements() {
        fpsStore = new double[FPS_HISTORY_NR];
        for (int i = 0; i < FPS_HISTORY_NR; i++)
            fpsStore[i] = 0.0;
        Log.d(TAG + ". initTimingElements() ",
                "Timing elements for stats initialised");
    }
}
