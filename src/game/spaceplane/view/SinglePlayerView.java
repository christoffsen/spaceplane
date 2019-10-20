package game.spaceplane.view;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import game.spaceplane.activity.SingleplayerActivity;
import game.spaceplane.asteroids.R;
import game.spaceplane.drawing.Sprite;
import game.spaceplane.drawing.StarField;
import game.spaceplane.helpers.BoundingBoxHelper;
import game.spaceplane.physics.Body;
import game.spaceplane.physics.GunAttack;
import game.spaceplane.physics.Vector;
import game.spaceplane.thread.GameThread;

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

public class SinglePlayerView extends SurfaceView implements
		SurfaceHolder.Callback, SensorEventListener {

	private static final String TAG = SingleplayerActivity.class
			.getSimpleName();

	public GameThread thread;
	private final boolean DEBUG = false;
	private String avgFps;
	private Paint p;
	private Paint starPaint;
	private Bitmap player;
	private Bitmap bullet;
	private Bitmap asteroidL;
	private Bitmap asteroidM;
	private Bitmap asteroidS;
	private Bitmap ufo;
	private Sprite shipSprite;
	private Sprite ufoSprite;

	/* Bitmap and Sprite for thrusting */
	private Sprite p1TSprite;
	private Bitmap p1Thrust;

	private Rect shipBounds = new Rect(0, 0, 0, 0);
	private Rect bulletBounds = new Rect(0, 0, 0, 0);
	private Rect asteroidLBounds = new Rect(0, 0, 0, 0);
	private Rect asteroidMBounds = new Rect(0, 0, 0, 0);
	private Rect asteroidSBounds = new Rect(0, 0, 0, 0);
	private Rect ufoBounds = new Rect(0,0,0,0);
	private Matrix m;
	private int direction = 1;
	private int pitch = 1;
	private String position = "";
	private List<Sprite> bullets;
	private List<Sprite> asteroids;
	private List<Sprite> ufos;

	private StarField starField;

	// Sensor manager for getting pitch/roll/yaw
	private SensorManager mSM;

	public SinglePlayerView(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// Create the main game loop
		thread = new GameThread(getHolder(), this, context);

		// Get the sensor service
		mSM = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		p = new Paint();
		starPaint = new Paint();
		starField = new StarField(80, 2, 50);
		player = BitmapFactory.decodeResource(getResources(),
				R.drawable.newship);
		p1Thrust = BitmapFactory.decodeResource(getResources(),
				R.drawable.shipthrust);
		
		ufo = BitmapFactory.decodeResource(getResources(),
                R.drawable.ufo);
		ufoBounds = new Rect(0,0, ufo.getWidth(), ufo.getHeight());

		shipBounds = new Rect(0, 0, player.getWidth(), player.getHeight());
		bullet = BitmapFactory.decodeResource(getResources(), R.drawable.laser);
		bulletBounds = new Rect(0, 0, bullet.getWidth(), bullet.getHeight());
		bullets = new CopyOnWriteArrayList<Sprite>();
		asteroids = new CopyOnWriteArrayList<Sprite>();
		ufos = new CopyOnWriteArrayList<Sprite>();

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
		shipSprite = new Sprite(player, (int) thread.ship.getPosition()
				.getHead().X, (int) thread.ship.getPosition().getHead().Y);
		p1TSprite = new Sprite(p1Thrust, (int) thread.ship.getPosition()
				.getHead().X, (int) thread.ship.getPosition().getHead().Y);
		Log.d(TAG, ("Created a ship sprite at " + shipSprite.spriteCoord.x
				+ ", " + shipSprite.spriteCoord.y));

		// make the view focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

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

		for (Body b : thread.bodies) {
			if (b.isShip()) {
				shipSprite.resetMatrix();
				shipSprite.rotate(shipSprite.spriteRot,
						(float) shipBounds.width() / 2,
						(float) shipBounds.width() / 2);
				shipSprite.translate(shipSprite.spriteCoord.x,
						shipSprite.spriteCoord.y);
				/*
				 * have transformations done to the thrust loop so we can draw
				 * it thrusting in the correct direction
				 */
				p1TSprite.resetMatrix();
				p1TSprite.rotate(p1TSprite.spriteRot,
						(float) shipBounds.width() / 2,
						(float) shipBounds.width() / 2);
				p1TSprite.translate(p1TSprite.spriteCoord.x,
						p1TSprite.spriteCoord.y);

				if (thread.isAccelerating == true) {
					p1TSprite.draw(canvas, p);
				}

				else {
					shipSprite.draw(canvas, p);
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

	public int getBulletCount() {
		return bullets.size();
	}

	public void update() {
		/*
		 * If the ship exits the screen, warp it to the other end
		 */
		int i = 0;

		for (Body b : thread.bodies) {

			if (b.isGun())
				continue;

			if (b.getNew()) {
				int type = b.GetAsteroidType();
				Sprite s = null;
				if(type == 1)
					s = new Sprite(asteroidL, (int) b.getPosition().getHead().X, (int) b
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
		updateUfo();
	}
	
	public void createUfo() {


        ufoSprite = new Sprite(ufo, (int)thread.ufo.getPosition().getHead().X, (int)thread.ufo.getPosition().getHead().Y);
        ufos.add(ufoSprite);
	}

	public void updateAsteroids() {
		int i = 0;
		for (Body b : thread.bodies) {
			if (i >= asteroids.size())
				break;
			if (!(b.isGun() || b.isShip())) {
				if (b.isDestroyed()) {
					thread.bodies.remove(b);
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
	
	public void updateUfo() {
	    if(thread.ufo == null) {
	        
	    }
	    if(thread.ufo != null) {
	        
	    }
	}

	public void updateShip() {
		shipSprite.spriteCoord.x = (int) thread.ship.getPosition().getHead().X
				- shipSprite.getSpriteBounds().right / 2;
		shipSprite.spriteCoord.y = (int) thread.ship.getPosition().getHead().Y
				- shipSprite.getSpriteBounds().bottom / 2;
		shipSprite.spriteRot = (float) ((float) thread.ship.getDirection()
				.getAngle() * (180 / Math.PI));

		/* also update the thrusting frame */
		p1TSprite.spriteCoord.x = (int) thread.ship.getPosition().getHead().X
				- p1TSprite.getSpriteBounds().right / 2;
		p1TSprite.spriteCoord.y = (int) thread.ship.getPosition().getHead().Y
				- p1TSprite.getSpriteBounds().bottom / 2;
		p1TSprite.spriteRot = (float) ((float) thread.ship.getDirection()
				.getAngle() * (180 / Math.PI));
	}

	public void updateBullets() {

		int i = 0;
		for (Body b : thread.bodies) {
			if (b.isGun()) {
				if (i >= bullets.size())
					break;
				if (b.isDestroyed()) {
					thread.bodies.remove(b);
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
					thread.bodies.remove(b);
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
		displayFps(canvas, avgFps);
	}

	public void setAvgFps(String string) {
		this.avgFps = string;
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
			canvas.drawText(fps, this.getWidth() - 100, 20, paint);
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
		Sprite tempBullet = new Sprite(bullet,
				(int) ga.getPosition().getHead().X, (int) ga.getPosition()
						.getHead().Y);
		tempBullet.spriteRot = (float) ((float) thread.ship.getDirection()
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
		//	return;
		if (thread != null) {
			int pitch = (int) event.values[1];

			if (pitch > 10) {
				direction -= 1;
			} else if (pitch < -10) {
				direction += 1;
			}

			if (direction > 360)
				direction -= 360;
			else if (direction < 0)
				direction += 360;

			thread.ship.setForward((double) direction * (Math.PI / 180.0));

			this.setDirection(direction);
			this.setPitch(pitch);

			position = ("X: " + thread.ship.getPosition().getHead().X + ", Y: " + thread.ship
					.getPosition().getHead().Y);
			this.setPosition(position);
		}
	}

	public int getAsteroidsSize() {
		return asteroids.size();
	}
}
