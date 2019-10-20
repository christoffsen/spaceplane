package game.spaceplane.drawing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class StarField {

	public List<Point> stars;
	public int starAlpha;
	public int starFade;
	public int numStars;

	public StarField(int alpha, int fade, int num) {
		stars = null;
		starAlpha = alpha;
		starFade = fade;
		numStars = num;
	}

	public void initStars(int maxX, int maxY) {
		stars = new ArrayList<Point>();
		for (int i = 0; i < numStars; i++) {
			Random r = new Random();
			int x = r.nextInt(maxX - 5 + 1) + 5;
			int y = r.nextInt(maxY - 5 + 1) + 5;
			stars.add(new Point(x, y));
		}
	}

	public void draw(Canvas canvas, Paint p) {
		// create a black canvas
		p.setColor(Color.BLACK);
		p.setAlpha(255);
		p.setStrokeWidth(1);
		//canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), p);
		// initialize the starfield if needed
		if (stars == null) {
			initStars(canvas.getWidth(), canvas.getHeight());
		}
		// draw the stars
		p.setColor(Color.CYAN);
		p.setAlpha(starAlpha += starFade);
		// fade them in and out
		if (starAlpha >= 252 || starAlpha <= 80)
			starFade = starFade * -1;
		p.setStrokeWidth(5);
		for (int i = 0; i < numStars; i++) {
			canvas.drawCircle(stars.get(i).x, stars.get(i).y, 3, p);
		    //canvas.drawPoint(stars.get(i).x, stars.get(i).y, p);
		}

	}

}
