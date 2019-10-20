package game.spaceplane.physics;

import android.graphics.*;


public class Wall extends Body {
    PointD top;
    PointD bottom;

    public Wall(PointD top, PointD bottom, int width)
    {
        this.top = top;
        this.bottom = bottom;
        this.width = width;
        position = new Vector((top.X - bottom.X)/ 2, (top.Y - bottom.Y)/ 2);
        velocity = new Vector(0, 0);
        //Colour = Pens.Red;
    }

    public boolean contacts(Body b)
    {
        boolean result = false;

        PointD pos = b.getPosition().getHead();

        if(pos.Y < bottom.Y && pos.Y > top.Y
            && pos.X > bottom.X && pos.X < top.X)
        {
                result = true;
        }

        return result;
    }

    public void update(PointD netForce)
    {}

    public void draw(Canvas c)
    {
        for (int i = 0; i < width; i++)
        {
        	//the paint part of this may be incorrect
            c.drawLine((float)(top.X + i), (float)top.Y, (float)(bottom.X + i), (float)bottom.Y, new Paint());
        }
    }

    public void setVelocity(Vector velocity)
    {}
}
