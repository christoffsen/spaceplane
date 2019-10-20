package game.spaceplane.helpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import game.spaceplane.physics.Body;

public class BoundingBoxHelper {
	public static void drawBoundingBox(Body b, Canvas c, Paint p){
		p.setColor(Color.MAGENTA);
		p.setStyle(Style.STROKE);
		int posX = (int)b.getPosition().getHead().X;
		int posY = (int)b.getPosition().getHead().Y;
		c.drawRect(posX - (int)b.getWidth()/2, posY - (int)b.getHeight() / 2, posX + (int)b.getWidth() / 2, posY + (int)b.getHeight() / 2, p);
		p.reset();
		if(b.isShip())
			p.setColor(Color.YELLOW);
		else
			p.setColor(Color.MAGENTA);
		c.drawCircle(posX, posY, 3, p);
		p.reset();
	}
}
