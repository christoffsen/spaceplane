package game.spaceplane.physics;

/**
 * 
 * @author Alexandra Kabak The spaceships attack. Takes in the spaceship to
 *         utilise it's position and velocity to make sure it's leaving the ship
 *         in the correct direction.
 * 
 */
public class GunAttack extends Body {

	/**
	 * Should take in Spaceship. Needs the position and vector.
	 */
	
	Vector direction;
	private int owner;
	
	public GunAttack(Spaceship s) {
		direction = s.getDirection();
		mass = 5;
		position = new Vector(s.getPosition().getHead().X, s.getPosition()
				.getHead().Y);
		
		double X = s.getVelocity().getHead().X;
		double Y = s.getVelocity().getHead().Y;
		owner = s.playerNum;
		
//		double Xfactor = X / Math.abs(X);
//		double Yfactor = Y / Math.abs(Y);
		
		velocity = new Vector(direction.getHead().X * 4, direction.getHead().Y * 4);
		
		//velocity = new Vector(X, Y);
		
		width = 25;
		height = 25;

		name = "GunAttack";
	}

	public GunAttack(int posX, int posY){
		mass = 5;
		velocity = new Vector(0,0);
		width = 25;
		height = 25;

		name = "GunAttack";
		
		position = new Vector(posX, posY);
		direction = new Vector(0, 0);
	}

	public boolean isGun() {
		return true;
	}

	public boolean isDestroyed() {
		return nullified;
	}

	public void setDestroyed(boolean value) {
		nullified = value;
	}


	public Vector getDirection() {
		return direction;
	}


    public int getOwner() {
        return owner;
    }


    public void setOwner(int owner) {
        this.owner = owner;
    }
	
	


}
