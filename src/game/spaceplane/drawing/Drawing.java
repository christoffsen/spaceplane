package game.spaceplane.drawing;

import game.spaceplane.asteroids.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class Drawing extends View {

	private Paint p;
	private List<Point> starField = null;
	private int starAlpha = 80;
	private int starFade = 2;
	private Rect sprite1Bounds = new Rect(0, 0, 0, 0);
	private Rect sprite2Bounds = new Rect(0, 0, 0, 0);
	private Point sprite1;
	private Point sprite2;
	private Bitmap bm1 = null;
	private Matrix m = null;
	private Bitmap bm2 = null;
	private Point lastCollision = new Point(-1, -1);
	private boolean collisionDetected = false;
	
	ExplosionAnimation explosion = new ExplosionAnimation(BitmapFactory.decodeResource(getResources(),R.drawable.explode)
			,10,50 // initial position
			,50,50 //width and height of sprite
			, 5, 5); // FPS and number of frames in animation)

	private int sprite1Rotation = 0;

	private static final int NUM_OF_STARS = 25;

	synchronized public void setSprite1(int x, int y) {
		sprite1 = new Point(x, y);
	}

	synchronized public int getSprite1X() {
		return sprite1.x;
	}

	synchronized public int getSprite1Y() {
		return sprite1.y;
	}

	synchronized public void setSprite2(int x, int y) {
		sprite2 = new Point(x, y);
	}

	synchronized public int getSprite2X() {
		return sprite2.x;
	}

	synchronized public int getSprite2Y() {
		return sprite2.y;
	}

	synchronized public void resetStarField() {
		starField = null;
	}

	synchronized public int getSprite1Width() {
		return sprite1Bounds.width();
	}

	synchronized public int getSprite1Height() {
		return sprite1Bounds.height();
	}

	synchronized public int getSprite2Width() {
		return sprite2Bounds.width();
	}

	synchronized public int getSprite2Height() {
		return sprite2Bounds.height();
	}

	synchronized public Point getLastCollision() {
		return lastCollision;
	}

	synchronized public boolean wasCollisionDetected() {
		return collisionDetected;
	}

	public Drawing(Context context, AttributeSet aSet) {
		super(context, aSet);
		p = new Paint();
		sprite1 = new Point(-1, -1);
		sprite2 = new Point(-1, -1);
		m = new Matrix();
		p = new Paint();
		bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid_s);
		bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.newship);
		sprite1Bounds = new Rect(0, 0, bm1.getWidth(), bm1.getHeight());
		sprite2Bounds = new Rect(0, 0, bm2.getWidth(), bm2.getHeight());
	}

	synchronized private void initializeStars(int maxX, int maxY) {
		starField = new ArrayList<Point>();
		for (int i = 0; i < NUM_OF_STARS; i++) {
			Random r = new Random();
			int x = r.nextInt(maxX - 5 + 1) + 5;
			int y = r.nextInt(maxY - 5 + 1) + 5;
			starField.add(new Point(x, y));
		}
		collisionDetected = false;
	}

	private boolean checkForCollision() {
		if (sprite1.x < 0 && sprite2.x < 0 && sprite1.y < 0 && sprite2.y < 0)
			return false;
		Rect r1 = new Rect(sprite1.x, sprite1.y, sprite1.x
				+ sprite1Bounds.width(), sprite1.y + sprite1Bounds.height());
		Rect r2 = new Rect(sprite2.x, sprite2.y, sprite2.x
				+ sprite2Bounds.width(), sprite2.y + sprite2Bounds.height());
		Rect r3 = new Rect(r1);
		if (r1.intersect(r2)) {
			for (int i = r1.left; i < r1.right; i++) {
				for (int j = r1.top; j < r1.bottom; j++) {
					if (bm1.getPixel(i - r3.left, j - r3.top) != Color.TRANSPARENT) {
						if (bm2.getPixel(i - r2.left, j - r2.top) != Color.TRANSPARENT) {
							lastCollision = new Point(sprite2.x + i - r2.left,
									sprite2.y + j - r2.top);
							return true;
						}
					}
				}
			}
		}
		lastCollision = new Point(-1, -1);
		return false;
	}

	@Override
	synchronized public void onDraw(Canvas canvas) {
		p.setColor(Color.BLACK);
		p.setAlpha(255);
		p.setStrokeWidth(1);
		canvas.drawRect(0, 0, getWidth(), getHeight(), p);

		if (starField == null) {
			initializeStars(canvas.getWidth(), canvas.getHeight());
		}
		p.setColor(Color.CYAN);
		p.setAlpha(starAlpha += starFade);
		if (starAlpha >= 252 || starAlpha <= 80)
			starFade = starFade * -1;
		p.setStrokeWidth(5);
		for (int i = 0; i < NUM_OF_STARS; i++) {
			canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
		}

		if (sprite1.x >= 0) {
			m.reset();
			m.postTranslate((float) (sprite1.x), (float) (sprite1.y));
			m.postRotate(sprite1Rotation,
					(float) (sprite1.x + sprite1Bounds.width() / 2.0),
					(float) (sprite1.y + sprite1Bounds.width() / 2.0));
			canvas.drawBitmap(bm1, m, null);
			sprite1Rotation += 5;
			if (sprite1Rotation >= 360)
				sprite1Rotation = 0;
		}
		if (sprite2.x >= 0) {
			canvas.drawBitmap(bm2, sprite2.x, sprite2.y, null);
		}
		collisionDetected = checkForCollision();
		if (collisionDetected) {
			int newX = lastCollision.x - 25;
			int newY = lastCollision.y - 25;
			explosion.draw(canvas, newX, newY);
			
			/*p.setColor(Color.RED);
			p.setAlpha(255);
			p.setStrokeWidth(5);
			canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
					lastCollision.x + 5, lastCollision.y + 5, p);
			canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
					lastCollision.x - 5, lastCollision.y + 5, p);*/
		}
	}
}