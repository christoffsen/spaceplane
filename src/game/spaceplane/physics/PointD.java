package game.spaceplane.physics;

import android.graphics.Point;


public class PointD {

    public double X;       //X - coordinate of the point
    public double Y;       //Y - coordinate of the point

    //Basic constructor that takes in X and Y and stores them.
    public PointD(double x, double y)
    {
        X = x;
        Y = y;
    }

    public PointD(PointD pd)
    {
        X = pd.X;
        Y = pd.Y;
    }

    public void multiplyByDouble(double d)
    {
        X *= d;
        Y *= d;
    }

    //Convenience method that truncates to form a(n) (int) point.
    public Point toPoint()
    {
        return new Point((int)X, (int)Y);
    }
}
