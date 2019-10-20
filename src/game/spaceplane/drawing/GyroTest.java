package game.spaceplane.drawing;

import game.spaceplane.asteroids.R;
import game.spaceplane.physics.GunAttack;
import game.spaceplane.physics.PointD;
import game.spaceplane.physics.Spaceship;
import game.spaceplane.physics.Vector;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class GyroTest extends View {
	private Paint p;
	private static Point sprite1;
	private Bitmap bm1 = null;
	private Matrix m = null;
	private static int sprite1Rotation = 0;
	private Rect sprite1Bounds = new Rect(0, 0, 0, 0);
	private static int pitch = 0, roll = 0;
	private float speedX = 0, speedY = 0;
	private float speed = 1;
	public static Spaceship ship;
	private static List<GunAttack> bullets;

	public GyroTest(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
		bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.newship);
		m = new Matrix();
		sprite1Bounds = new Rect(0, 0, bm1.getWidth(), bm1.getHeight());
		ship = new Spaceship(250,250, 1);
		ship.setPosition(new Vector(new PointD(getWidth() / 2,
				getHeight() / 2 - 10), new PointD(getWidth() / 2,
				getHeight() / 2)));
		bullets = new ArrayList<GunAttack>();
	}

	@Override
	synchronized public void onDraw(Canvas canvas) {
		p.setColor(Color.BLACK);
		sprite1 = new Point(getWidth() / 2, getHeight() / 2);
		p.setAlpha(255);
		p.setStrokeWidth(1);
		canvas.drawRect(0, 0, getWidth(), getHeight(), p);

		if (pitch >= 15) {
			m.reset();
			m.postTranslate((float) (sprite1.x), (float) (sprite1.y));
			sprite1Rotation -= 3;
			m.postRotate(sprite1Rotation,
					(float) (sprite1.x + sprite1Bounds.width() / 2.0),
					(float) (sprite1.y + sprite1Bounds.width() / 2.0));
		} else if (pitch <= -15) {
			m.reset();
			m.postTranslate((float) (sprite1.x), (float) (sprite1.y));
			sprite1Rotation += 3;
			m.postRotate(sprite1Rotation,
					(float) (sprite1.x + sprite1Bounds.width() / 2.0),
					(float) (sprite1.y + sprite1Bounds.width() / 2.0));
		}

		if (roll <= 20) {
			speedX = (float) Math
					.sin(sprite1Rotation * (Math.PI / 180) * speed);
			speedY = (float) -Math.cos(sprite1Rotation * (Math.PI / 180)
					* speed);
			sprite1.x += speedX;
			sprite1.y += speedY;
		} else {
			if (speed > 1) {
				speed *= 0.9;
			}
		}
		canvas.drawBitmap(bm1, m, null);

		if (sprite1Rotation > 360)
			sprite1Rotation = 3;
		else if (sprite1Rotation < 0)
			sprite1Rotation = 357;

		p.setColor(Color.WHITE);
		p.setStrokeWidth(2);

	}

	synchronized public int getSprite1Width() {
		return sprite1Bounds.width();
	}

	synchronized public int getSprite1Height() {
		return sprite1Bounds.height();
	}

	synchronized public static void rotate(int x) {
		pitch = x;
	}

	synchronized public int getSprite1X() {
		return sprite1.x;
	}

	synchronized public int getSprite1Y() {
		return sprite1.y;
	}

	synchronized public void setSprite1(int x, int y) {
		sprite1 = new Point(x, y);
	}

	synchronized public static void fireGun() {
		GunAttack temp = new GunAttack(ship);
		bullets.add(temp);
	}

	synchronized public void setPitch(int x) {
		pitch = x;
	}

	synchronized public void setRoll(int x) {
		roll = x;
	}

}
