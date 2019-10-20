package game.spaceplane.drawing;

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
import game.spaceplane.asteroids.R;

public class Sprite {
	public Rect spriteBounds;
	public Point spriteCoord;
	Bitmap sMap;
	public Matrix m;
	public float spriteRot;

	public Sprite(Bitmap bitmap, int x, int y) {
	    spriteCoord = new Point();
		spriteCoord.x = x;
		spriteCoord.y = y;
		spriteBounds = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m = new Matrix();
		sMap = bitmap;
		spriteRot = 0;
	}

	public void draw(Canvas canvas, Paint p) {
		canvas.drawBitmap(sMap, m, p);
	}

	public void translate(float dx, float dy) {
		m.postTranslate(dx, dy);
	}

	public void rotate(float degrees, float px, float py) {
		m.postRotate(degrees, px, py);
	}

	public Rect getSpriteBounds() {
		return spriteBounds;
	}

	public void setSpriteBounds(Rect spriteBounds) {
		this.spriteBounds = spriteBounds;
	}

	public Point getSpriteCoord() {
		return spriteCoord;
	}

	public void setSpriteCoord(Point spriteCoord) {
		this.spriteCoord = spriteCoord;
	}

	public Bitmap getsMap() {
		return sMap;
	}

	public void setsMap(Bitmap sMap) {
		this.sMap = sMap;
	}

	public Matrix getM() {
		return m;
	}

	public void setM(Matrix m) {
		this.m = m;
	}
	
	public void resetMatrix() {
		this.m.reset();
	}

}
