package game.spaceplane.physics;

public class Spaceship extends Body{
	//Engine engine1;
	//Engine engine2;
    private boolean gameOver = false;
    private Vector velocity;
    private double mass;
    protected int width;
    protected int height;
    protected Vector acceleration;
    protected Vector position;
    protected Vector forwardDirection;
    protected double constantAcceleration;
    protected int playerNum;
	
	public Spaceship(int posX, int posY, int num){
	    position = new Vector(posX,posY);
	    width = 40;
	    height = 40;
	    mass = 250;
	    velocity = new Vector(0,0);
	    forwardDirection = new Vector(1,0);
	    constantAcceleration = 0.25;
	    acceleration = new Vector(0,0);
	    this.playerNum = num;
		//parent();
		//engine1 = new Engine();
		//engine2 = new Engine();
	}
	
	public boolean gameOver()
	{
	    return gameOver;
	}
	
	public void setGameOver(boolean value)
	{
	    gameOver = value;
	}
    
    public boolean isShip()
    {
        return true;
    }
    

    public boolean isDestroyed()
    {
        return nullified;
    }

    public void setDestroyed(boolean value)
    {
        nullified = value;
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


    public void setCollided(boolean truth)
    {
        collidedThisIteration = truth;
    }

    public boolean getCollided()
    {
        return collidedThisIteration;
    }

    public void setVelocity(Vector velocity)
    {
        this.velocity = new Vector(velocity.getHead().X, velocity.getHead().Y);
    }

    public Vector getVelocity()
    {
        return velocity;
    }

    public Vector getAcceleration()
    {
        return acceleration;
    }
    
    public Vector getPosition(){
        return position;
    }
    
    public Vector getDirection()
    {
        return forwardDirection;
    }
    

    public void setPosition(Vector point){
        position = new Vector(point.getVector().X, point.getVector().Y);
    }
    
    
    public void setForward(double angleInRadians)
    {
        double X = Math.cos(angleInRadians);//gives X Velocity
        double Y = Math.sin(angleInRadians);//gives Y Velocity
        
        forwardDirection = new Vector(X, Y);
        forwardDirection.setAngle(angleInRadians);
    }
    
    public void setForward(double x, double y){
    	forwardDirection = new Vector(x, y);
    	double Y = Math.asin(y);
    	//double X = Math.acos(x);
    	//forwardDirection.setAngle(Math.cosh(x));
    	forwardDirection.setAngle(Y);
    }
    
    public void moveForward()
    {
        //multiply velocity by direction X and Y and add to X and Y of position
      
            double AccXforward = forwardDirection.getUnitVector().X * constantAcceleration;
            double AccXforwardY = forwardDirection.getUnitVector().Y * constantAcceleration;
        
//            if((velocity.getHead().X <= 10 && velocity.getHead().Y <= 10))
//            {
//                
//                if((velocity.getHead().X == 10 || velocity.getHead().Y == 10) && (AccXforward < 0 && AccXforwardY < 0))
//                {
//                    acceleration = new Vector(AccXforward, AccXforwardY);
//                    
//                    velocity.getHead().X += AccXforward;
//                    velocity.getHead().Y += AccXforwardY;
//                    
//                }else if(velocity.getHead().X != 10 || velocity.getHead().Y != 10)
//                {
//                    acceleration = new Vector(AccXforward, AccXforwardY);
//                    
//                    velocity.getHead().X += AccXforward;
//                    velocity.getHead().Y += AccXforwardY;
//                }
//                
//            }else if((velocity.getHead().X <= -10 && velocity.getHead().Y <= -10))
//            {
//                if((velocity.getHead().X == -10 || velocity.getHead().Y == -10) && (AccXforward > 0 && AccXforwardY > 0))
//                {
//                    acceleration = new Vector(AccXforward, AccXforwardY);
//                    
//                    velocity.getHead().X += AccXforward;
//                    velocity.getHead().Y += AccXforwardY;
//                    
//                }else if(velocity.getHead().X != -10 || velocity.getHead().Y != -10)
//                {
//                    acceleration = new Vector(AccXforward, AccXforwardY);
//                    
//                    velocity.getHead().X += AccXforward;
//                    velocity.getHead().Y += AccXforwardY;
//                }
//            }
//            
            acceleration = new Vector(AccXforward, AccXforwardY);
            
            velocity.getHead().X += AccXforward;
            velocity.getHead().Y += AccXforwardY;
       
    }
    
    public void update()
    {
        
          velocity.add(acceleration);
          position.add(velocity);
          
          acceleration = new Vector(0,0);
    }
    
    public int getPlayerNum() {
        return playerNum;
    }
    
    public void setPlayerNum(int num) {
        this.playerNum = num;
    }
    
}