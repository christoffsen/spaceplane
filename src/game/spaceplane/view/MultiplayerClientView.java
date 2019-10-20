package game.spaceplane.view;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import game.spaceplane.activity.MultiplayerActivity;
import game.spaceplane.activity.SingleplayerActivity;
import game.spaceplane.asteroids.R;
import game.spaceplane.drawing.Sprite;
import game.spaceplane.drawing.StarField;
import game.spaceplane.helpers.BoundingBoxHelper;
import game.spaceplane.network.NetworkEngine;
import game.spaceplane.physics.Body;
import game.spaceplane.physics.GunAttack;
import game.spaceplane.physics.Vector;
import game.spaceplane.thread.GameThread;
import game.spaceplane.thread.MultiplayerClientThread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MultiplayerClientView extends SurfaceView implements
        SurfaceHolder.Callback, SensorEventListener {
    private static final String TAG = MultiplayerActivity.class
            .getSimpleName();
    private final boolean DEBUG = false;
    private boolean rotating = false;
    private NetworkEngine network;

    public MultiplayerClientThread thread;

    private Paint p;
    private Paint starPaint;
    private Bitmap playerOne;
    private Bitmap playerTwo;
    private Bitmap bullet;
    private Bitmap bulletTwo;
    private Bitmap asteroidL;
    private Bitmap asteroidM;
    private Bitmap asteroidS;
    private Sprite shipOneSprite;
    private Sprite shipTwoSprite;

    private Rect shipBounds = new Rect(0, 0, 0, 0);
    private Rect bulletBounds = new Rect(0, 0, 0, 0);
    private Rect asteroidLBounds = new Rect(0, 0, 0, 0);
    private Rect asteroidMBounds = new Rect(0, 0, 0, 0);
    private Rect asteroidSBounds = new Rect(0, 0, 0, 0);
    private Matrix m;
    private int direction = 1;
    private int pitch = 1;
    private String position = "";
    private List<Sprite> bullets;
    private List<Sprite> asteroids;

    private StarField starField;

    // Sensor manager for getting pitch/roll/yaw
    private SensorManager mSM;

    public MultiplayerClientView(Context context) {
        super(context);
        
        getHolder().addCallback(this);

        thread = new MultiplayerClientThread(getHolder(), this, context);

        mSM = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        p = new Paint();
        starPaint = new Paint();
        starField = new StarField(80, 2, 25);
        playerOne = BitmapFactory.decodeResource(getResources(),
                R.drawable.p2ship);
        playerTwo = BitmapFactory.decodeResource(getResources(),
                R.drawable.newship);
        shipBounds = new Rect(0, 0, playerOne.getWidth(), playerOne.getHeight());
        bullet = BitmapFactory.decodeResource(getResources(), R.drawable.laser);
        bulletTwo = BitmapFactory.decodeResource(getResources(), R.drawable.laser2);
        bulletBounds = new Rect(0, 0, bullet.getWidth(), bullet.getHeight());
        bullets = new CopyOnWriteArrayList<Sprite>();
        asteroids = new CopyOnWriteArrayList<Sprite>();

        asteroidL = BitmapFactory.decodeResource(getResources(),
                R.drawable.asteroid);
        asteroidM = BitmapFactory.decodeResource(getResources(),
                R.drawable.asteroid_m);
        asteroidS = BitmapFactory.decodeResource(getResources(),
                R.drawable.asteroid_s);

        asteroidLBounds = new Rect(0, 0, asteroidL.getWidth(),
                asteroidL.getHeight());
        asteroidMBounds = new Rect(0, 0, asteroidM.getWidth(),
                asteroidM.getHeight());
        asteroidSBounds = new Rect(0, 0, asteroidS.getWidth(),
                asteroidS.getHeight());

        m = new Matrix();
        shipOneSprite = new Sprite(playerOne, (int) thread.shipOne
                .getPosition().getHead().X, (int) thread.shipOne.getPosition()
                .getHead().Y);
        shipTwoSprite = new Sprite(playerTwo, (int) thread.shipTwo
                .getPosition().getHead().X, (int) thread.shipTwo.getPosition()
                .getHead().Y);
        Log.d(TAG, ("Created a ship sprite at " + shipOneSprite.spriteCoord.x
                + ", " + shipOneSprite.spriteCoord.y));
        // make the view focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2,
            int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSM.registerListener(this,
                mSM.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);
        // After the surface has been created, start the loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // If the surface has been destroyed
        boolean retry = true;
        while (retry) {
            try {
                mSM.unregisterListener(this);
                thread.join();
                thread.setRunning(false);
                retry = false;
                thread = null;
                Log.d(TAG, "Thread was destroyed properly");

            } catch (InterruptedException e) {
                // try shutting down the thread again
            }
        }
    }

    synchronized public void draw(Canvas canvas) {

        boolean shipOneDrawn = false;
        boolean shipTwoDrawn = false;
        if(thread.bodies != null){
        for (Body b : thread.bodies) {
        	if(b == null)
        		continue;
            if (b.isShip()) {
                if (shipOneDrawn == false) {
                    shipOneSprite.resetMatrix();
                    shipOneSprite.rotate(shipOneSprite.spriteRot,
                            (float) shipBounds.width() / 2,
                            (float) shipBounds.width() / 2);
                    shipOneSprite.translate(shipOneSprite.spriteCoord.x,
                            shipOneSprite.spriteCoord.y);
                    shipOneSprite.draw(canvas, p);
                    shipOneDrawn = true;
                }
                if (shipTwoDrawn == false) {
                    shipTwoSprite.resetMatrix();
                    shipTwoSprite.rotate(shipTwoSprite.spriteRot,
                            (float) shipBounds.width() / 2,
                            (float) shipBounds.width() / 2);
                    shipTwoSprite.translate(shipTwoSprite.spriteCoord.x,
                            shipTwoSprite.spriteCoord.y);
                    shipTwoSprite.draw(canvas, p);
                    shipTwoDrawn = true;
                }

            }
        }
        for (Sprite s : asteroids) {
            s.resetMatrix();
            s.translate(s.spriteCoord.x, s.spriteCoord.y);
            s.draw(canvas, p);
        }

        for (Sprite s : bullets) {
            s.resetMatrix();
            s.translate(s.spriteCoord.x, s.spriteCoord.y);
            s.draw(canvas, p);

        }

        if (DEBUG)
            for (Body b : thread.bodies)
                BoundingBoxHelper.drawBoundingBox(b, canvas, p);
        }
    }

    public int getBulletCount() {
        return bullets.size();
    }

    public void update() {
        /*
         * If the ship exits the screen, warp it to the other end
         */
        int i = 0;
        if(thread.bodies != null){
	        for (Body b : thread.bodies) {
	
	            if (b.isGun())
	                continue;
	
	            if (b.getNew()) {
	                int type = b.GetAsteroidType();
	                Sprite s = null;
	                if(type == 1)
	                	s = new Sprite(asteroidL,
	                            (int) b.getPosition().getHead().X, (int) b
                                .getPosition().getHead().Y);
	                if (type == 2)
	                    s = new Sprite(asteroidM,
	                            (int) b.getPosition().getHead().X, (int) b
	                                    .getPosition().getHead().Y);
	
	                if (type == 3)
	                    s = new Sprite(asteroidS,
	                            (int) b.getPosition().getHead().X, (int) b
	                                    .getPosition().getHead().Y);
	
	                asteroids.add(s);
	                b.setNew(false);
	            }
	            double tempY = b.getPosition().getHead().Y;
	            double tempX = b.getPosition().getHead().X;
	
	            if (b.getPosition().getHead().X > getWidth()) {
	                b.setPosition(new Vector(tempX - getWidth(), tempY));
	            } else if (b.getPosition().getHead().X < 0) {
	                b.setPosition(new Vector(tempX + getWidth(), tempY));
	            }
	
	            if (b.getPosition().getHead().Y > getHeight()) {
	                b.setPosition(new Vector(tempX, tempY - getHeight()));
	            } else if (b.getPosition().getHead().Y < 0) {
	                b.setPosition(new Vector(tempX, tempY + getHeight()));
	            }
	            i++;
	        }
	        updateAsteroids();
	        updateShip();
	        updateBullets();
	    }
    }

    public void updateAsteroids() {
        int i = 0;
        for (Body b : thread.bodies) {
            if (i >= asteroids.size())
                break;
            if (!(b.isGun() || b.isShip())) {
                if (b.isDestroyed()) {
                    asteroids.remove(i);
                    continue;
                }
                Sprite a = asteroids.get(i);
                a.spriteCoord.x = (int) b.getPosition().getHead().X
                        - a.getSpriteBounds().right / 2;
                a.spriteCoord.y = (int) b.getPosition().getHead().Y
                        - a.getSpriteBounds().bottom / 2;
                i++;
            }
        }
    }

    public void updateShip() {
        shipTwoSprite.spriteCoord.x = (int) thread.shipOne.getPosition()
                .getHead().X - shipTwoSprite.getSpriteBounds().right / 2;
        shipTwoSprite.spriteCoord.y = (int) thread.shipOne.getPosition()
                .getHead().Y - shipTwoSprite.getSpriteBounds().bottom / 2;
        shipTwoSprite.spriteRot = (float) ((float) thread.shipOne
                .getDirection().getAngle() * (180 / Math.PI));
        shipOneSprite.spriteCoord.x = (int) thread.shipTwo.getPosition()
                .getHead().X - shipOneSprite.getSpriteBounds().right / 2;
        shipOneSprite.spriteCoord.y = (int) thread.shipTwo.getPosition()
                .getHead().Y - shipOneSprite.getSpriteBounds().bottom / 2;
        shipOneSprite.spriteRot = (float) ((float) thread.shipTwo
                .getDirection().getAngle() * (180 / Math.PI));
    }

    public void updateBullets() {

        int i = 0;
        for (Body b : thread.bodies) {
            if (b.isGun()) {
                if (i >= bullets.size())
                    break;
                if (b.isDestroyed()) {
                    bullets.remove(i);
                    continue;
                }
                
                Sprite bullet = bullets.get(i);

                bullet.spriteCoord.x = (int) b.getPosition().getHead().X
                        - bullet.spriteBounds.right / 2;
                bullet.spriteCoord.y = (int) b.getPosition().getHead().Y
                        - bullet.spriteBounds.bottom / 2;
                if (bullet.spriteCoord.x > getWidth()
                        || bullet.spriteCoord.x < 0
                        || bullet.spriteCoord.y > getHeight()
                        || bullet.spriteCoord.y < 0) {
                    bullets.remove(bullet);
                    i--;
                }
                i++;
            }

        }
    }

    public void render(Canvas canvas) {

        p.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        starField.draw(canvas, p);
        p.reset();
    }

    public void setDirection(int string) {
        this.direction = string;
    }

    public void setPitch(int string) {
        this.pitch = string;
    }

    private void displayFps(Canvas canvas, String fps) {
        if (canvas != null && fps != null) {
            Paint paint = new Paint();
            paint.setARGB(255, 255, 255, 255);
            canvas.drawText(fps, this.getWidth() - 100, 80, paint);
        }
    }

    public void setPosition(String string) {
        this.position = string;
    }

    public int width() {
        return getWidth();
    }

    public int height() {
        return getHeight();
    }

    public void createBullet(GunAttack ga) {
        Sprite tempBullet = null;
        if(ga.getOwner() == 0) {
            tempBullet = new Sprite(bullet,
                    (int) ga.getPosition().getHead().X, (int) ga.getPosition()
                            .getHead().Y);
        }
        if(ga.getOwner() == 1) {
            tempBullet = new Sprite(bulletTwo,
                    (int) ga.getPosition().getHead().X, (int) ga.getPosition()
                            .getHead().Y);
        }
        tempBullet.spriteRot = (float) ((float) thread.shipOne.getDirection()
                .getAngle() * (180 / Math.PI));
        bullets.add(tempBullet);
        }

    public Bitmap getAsteroidBitmap() {
        return asteroidL;
    }

    public void addAsteroidSprite(Sprite s) {
        this.asteroids.add(s);
    }

    public void removeAsteroidSprite(Sprite s) {
        this.asteroids.remove(s);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    synchronized public void onSensorChanged(SensorEvent event) {
        //if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
          //  return;
        if (thread != null) {
            int pitch = (int) event.values[1];

            if (pitch > 10) {
                direction -= 1;
                setRotating(true);
            } else if (pitch < -10) {
                direction += 1;
                setRotating(false);
            }
            

            if (direction > 360)
                direction -= 360;
            else if (direction < 0)
                direction += 360;
            
            //thread.shipTwo.setForward((double) direction * (Math.PI / 180.0));

        }
    }

    public int getAsteroidsSize() {
        return asteroids.size();
    }

    public NetworkEngine getNetwork() {
        return network;
    }

    public void setNetwork(NetworkEngine network) {
        this.network = network;
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
    }

    public void purgeSprites() {
        asteroids.clear();
        bullets.clear();
    }

    public Bitmap getBulletOneBitmap() {
        return bullet;
    }
    
    public Bitmap getBulletTwoBitmap() {
        return bulletTwo;
    }

    public void addBulletSprite(Sprite aBody) {
       this.bullets.add(aBody);
        
    }

}
