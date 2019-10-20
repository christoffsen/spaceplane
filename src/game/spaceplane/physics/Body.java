package game.spaceplane.physics;

//not that methods in Java are implicitly virtual already(so the word 'virtual was removed)
public abstract class Body {
    
    private boolean gameOver = false;
    private boolean isNew;
    protected Vector velocity;
    protected Vector momentum;
    protected Vector acceleration;
    protected Vector position;

    protected double mass;
    protected int width;
    protected int height;
    protected boolean collidedThisIteration;
    protected int type;

    protected String name;
    
    protected boolean nullified = false;

    public Vector getPosition(){
        return position;
    }
    
    public void setNew(boolean isNew){
    	this.isNew = isNew;
    }
    
    public boolean getNew(){
    	return isNew;
    }
    
    public void setPosition(Vector point){
        position = point;
    }
	
    //public abstract void draw(Canvas g);

    public void setVelocity(Vector velocity)
    {
        this.velocity = velocity;
    }

    public Vector getVelocity()
    {
        return velocity;
    }

    public Vector getAcceleration()
    {
        return acceleration;
    }

    public void update(){
        if(acceleration != null)
            velocity.add(acceleration);
        if(velocity != null)
            position.add(velocity);
    }

    public void update(PointD netForce)
    {
        acceleration = new Vector(netForce.X / (double)mass, netForce.Y / (double)mass);
        velocity.add(acceleration);
        position.add(velocity);
    } 

    public boolean contacts(Body b)
    {
    	if(b.getNew() || isNew)
    		return false;
        double A = this.getPosition().getHead().X - b.getPosition().getHead().X;
        double B = this.getPosition().getHead().Y - b.getPosition().getHead().Y;

        double distance = Math.sqrt((Math.pow(A,2)+ Math.pow(B,2)));

        if (distance < ((this.width/2.0) + (b.width/2.0)))
        {
            return true;
        }

        return false;

    }

    public String getName()
    {
        return name;
    }

    public void setCollided(boolean truth)
    {
        collidedThisIteration = truth;
    }

    public boolean getCollided()
    {
        return collidedThisIteration;
    }

    private boolean withinRadius(double num, double radius, double tolerance)
    {
        if (Math.abs(num - radius) <= tolerance)
        {
            return true;
        }
        return false;
    }

    public double getWidth()
    {
        return width;
    }

    public double getHeight()
    {
        return height;
    }

    public double getMass()
    {
        return mass;
    }
    
    
    public boolean isGun()
    {
        return false;
    }

    public boolean isDestroyed()
    {
        return nullified;
    }

    public void setDestroyed(boolean value)
    {
        nullified = value;
    }
    
    public int GetAsteroidType()
    {
        return type;        
    }
    
    public boolean isShip()
    {
        return false;
    }
    
    public boolean gameOver()
    {
        return gameOver;
    }
    
    public void setGameOver(boolean value)
    {
        gameOver = value;
    }
}
