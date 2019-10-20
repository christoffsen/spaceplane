package game.spaceplane.drawing;

import java.util.ArrayList;
import java.util.List;

import game.spaceplane.asteroids.R;
import game.spaceplane.activity.SettingsActivity;
import game.spaceplane.activity.TestActivity;

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

public class SettingsTest extends View {
	private Paint p;
	Point p1;
	private int pitch, roll;

	public SettingsTest(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
	}

	@Override
	synchronized public void onDraw(Canvas canvas) {
		p.setColor(Color.BLACK);
		p.setAlpha(255);
		p.setStrokeWidth(1);
		canvas.drawRect(0, 0, getWidth(), getHeight(), p);

		p1 = new Point(getWidth() / 2, getHeight() / 2);

		p.setColor(Color.WHITE);
		canvas.drawText("Drawing a line from (" + p1.x + ", " + p1.y + ") to ("
				+ (p1.x + roll + 10) + ", " + (p1.y + pitch + 10) + ")", 0, 50,
				p);
		canvas.drawLine(p1.x, p1.y, p1.x + roll, p1.y + pitch, p);
	}
	
	synchronized public void setPitch(int x) {
		pitch = x;
	}
	
	synchronized public void setRoll(int x) {
		roll = x;
	}
}
