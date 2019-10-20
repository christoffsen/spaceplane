package game.spaceplane.physics;

import android.graphics.Point;



public class PhysMath {
  //Returns head-tail
    public static PointD GetVector(PointD tail, PointD head)
    {
        return new PointD(head.X - tail.X, head.Y - tail.Y);
    }

    //Calculates length (assumes p represents a vector)
    public static double Pythagoras(PointD pd)
    {
        if (pd.X != 0 || pd.Y != 0)
            return Math.sqrt(Math.pow(pd.X, 2) + Math.pow(pd.Y, 2));
        else
            return 1.0;

    }

    //Computes dot-product of two vectors
    public static double DP(PointD A, PointD B)
    {
        return A.X * B.X + A.Y * B.Y;
    }

    //Gets unit vector of a given vector
    public static PointD Normalize(PointD pd)
    {
        return new PointD(pd.X / Pythagoras(pd), pd.Y / Pythagoras(pd));
    }

    public static PointD ToPointD(Point p)
    {
        return new PointD(p.x, p.y);
    }
}
