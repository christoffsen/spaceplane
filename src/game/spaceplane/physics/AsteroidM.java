package game.spaceplane.physics;

public class AsteroidM extends Asteroid{

    public AsteroidM(Body b, Body gun, int direction)
    {
        width = 115;
        height = 115;
        mass = 250;
        type = 2;
        
        setNew(true);
        
        if(direction == 1)//left asteroid
        {
            //simple for now, will put in collision dealing in later iteration
            position = new Vector(b.getPosition().getHead().X, b.getPosition().getHead().Y + 50);
            //velocity = new Vector((b.getVelocity().getHead().X + gun.getVelocity().getHead().X) * 5.0
            //		, (b.getVelocity().getHead().Y + gun.getVelocity().getHead().Y) * 5.0);
        }else//right asteroid
        {
          //simple for now, will put in collision dealing in later iteration
            position = new Vector(b.getPosition().getHead().X, b.getPosition().getHead().Y - 50);
           // velocity = new Vector((b.getVelocity().getHead().X - gun.getVelocity().getHead().X
            //		) * 5.0, (b.getVelocity().getHead().Y - gun.getVelocity().getHead().Y) * 5.0);
        }
        
        velocity = new Vector(b.getVelocity().getHead().X*100, b.getVelocity().getHead().Y*100);
        Engine.elasticCollision(gun, this);
       
    }
    
    //FOR DRAWING USE ONLY!
    public AsteroidM(int posX, int posY){
    	position = new Vector(posX, posY);
        width = 115;
        height = 115;
        mass = 250;
        type = 2;
        setNew(true);
        velocity = new Vector(0,0);
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
