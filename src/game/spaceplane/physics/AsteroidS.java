package game.spaceplane.physics;

public class AsteroidS extends Asteroid{

    public AsteroidS(Body b,Body gun, int direction)
    {
        width = 65;
        height = 65;
        mass = 125;
        type = 3;
        
        setNew(true);
        
        collidedThisIteration = true;
        if(direction == 1)//left asteroid
        {
            //simple for now, will put in collision dealing in later iteration
            position = new Vector(b.getPosition().getHead().X, b.getPosition().getHead().Y + 25);
            
        }else//right asteroid
        {
          //simple for now, will put in collision dealing in later iteration
            position = new Vector(b.getPosition().getHead().X, b.getPosition().getHead().Y - 25);
        }
        velocity = new Vector(b.getVelocity().getHead().X*150, b.getVelocity().getHead().Y*150);
        Engine.elasticCollision(gun, this);
    }
    //FOR DRAWING USE ONLY!
    public AsteroidS(int posX, int posY) {
        width = 65;
        height = 65;
        mass = 125;
        type = 3;
        
    	position = new Vector(posX, posY);
        velocity = new Vector(0,0);
        setNew(true);
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
}
