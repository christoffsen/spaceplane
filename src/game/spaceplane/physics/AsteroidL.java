package game.spaceplane.physics;

public class AsteroidL extends Asteroid {

	static int spawn = 0;
	public double acceleration;

	public AsteroidL(int posX, int posY) {
		width = 130;
		height = 130;
		mass = 500;
		type = 1;
		name = "Asteroid";
		
		setNew(true);
		
		setPosition(new Vector(posX, posY));
		//setVelocity(new Vector(5,5));
		
//		Random generator = new Random((long) 2.0);
//		velocity = new Vector(generator.nextDouble() - 1,
//				generator.nextDouble() - 1);

		//generator = new Random(500);
		// position = new Vector(generator.nextDouble(),generator.nextDouble());

//		switch (spawn) {
//		case 0: {
//			position = new Vector(200, 200);
//			spawn++;
//			break;
//		}
//
//		case 1: {
//			position = new Vector(400, 400);
//			spawn++;
//			break;
//		}
//
//		case 2: {
//			position = new Vector(200, 500);
//			spawn = 0;
//			break;
//		}
		}


	// public AsteroidL(Body b, Body gun, int direction)
	// {
	// width = 50;
	// height = 50;
	// mass = 500;
	// type = 1;
	//
	// collidedThisIteration = true;
	// if(direction == 1)//left asteroid
	// {
	// //simple for now, will put in collision dealing in later iteration
	// position = new Vector(b.getPosition().getHead().X,
	// b.getPosition().getHead().Y + 20);
	// velocity = new Vector(b.getVelocity().getHead().X/2,
	// b.getVelocity().getHead().Y/2);
	// Engine.elasticCollision(gun, this);
	//
	// }else//right asteroid
	// {
	// //simple for now, will put in collision dealing in later iteration
	// position = new Vector(b.getPosition().getHead().X,
	// b.getPosition().getHead().Y - 20);
	// velocity = new Vector(b.getVelocity().getHead().X/2,
	// b.getVelocity().getHead().Y/2);
	// Engine.elasticCollision(gun, this);
	// }

	// }
	
	public boolean isDestroyed() {
		return nullified;
	}

	public void setDestroyed(boolean value) {
		nullified = value;
	}

	public int GetAsteroidType() {
		return type;
	}
	
}
