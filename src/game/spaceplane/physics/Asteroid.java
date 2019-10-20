package game.spaceplane.physics;

public abstract class Asteroid extends Body {
    protected int type;
    protected Vector forwardDirection;
    
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
    
    public void setPosition(Vector position) {
    	this.position = position;
    }
    
    public Vector getPosition() {
    	return position;
    }
    
    
    public void setDirection(Vector direction) {
    	this.forwardDirection = direction;
    }
    
    public Vector getDirection() {
    	return forwardDirection;
    }
    
}
