package game.spaceplane.physics;
public class Vector {
    PointD head;
    PointD tail;
    PointD vector;
    PointD unitVector;
    double length;
    double angle;
    
    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="head">@param
    /// a Point object representing the head of the vector
    /// <param name="tail">@param
    /// a Point object representing the tail of the vector
    public Vector(PointD head, PointD tail)
    {
        this.head = head;
        this.tail = tail;
        vector = PhysMath.GetVector(tail, head);
        length = PhysMath.Pythagoras(vector);
        unitVector = PhysMath.Normalize(vector);
        angle = 0;
    }

    public Vector(double xComponent, double yComponent)
    {
        head = new PointD(xComponent, yComponent);
        tail = new PointD(0, 0);
        angle = 0;
        update();
    }

    private void update(){
        vector = PhysMath.GetVector(tail, head);
        length = PhysMath.Pythagoras(vector);
        unitVector = PhysMath.Normalize(vector);
    }

    public PointD getUnitVector(){
        return unitVector;
    }

    public PointD getVector()
    {
        return vector;
    }

    public void add(Vector v)
    {
        this.head.X += v.head.X;
        this.head.Y += v.head.Y;

        this.tail.X += v.tail.X;
        this.tail.Y += v.tail.Y;
        update();
    }

    public PointD getHead()
    {
        return head;
    }

    public PointD getTail()
    {
        return tail;
    }
    
    public double getAngle()
    {
        return angle;
    }
    
    
    public void setAngle(double angle)
    {       
       this.angle = angle;   
    }
}
