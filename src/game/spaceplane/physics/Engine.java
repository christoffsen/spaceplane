package game.spaceplane.physics;
import java.util.ArrayList;
import java.util.List;
//import java.awt.Panel;
import java.io.Console;
//import javax.swing.JPanel;


/***
 * 
 * @author Alex/Chris
 * 
 *  Porting changes to note:
 *  -All Lists have changed to ArrayLists (there was no other way)
 *  -readonly was changed to final
 *  -Class no longer uses Graphics and cannot draw
 *  -update method takes in a List<Body>, it also returns the list when finished updating.
 *  ------------------------------V2 CHANGES----------------------------------
 *  -Added split asteroid
 *  -Added collision distinction
 */
public class Engine{
    List<Body> objects;
    List<Vector> forces;
    List<Body> temp;
    private int numPlayers = 0;
    private int[] playerScores;
    private final double g = 9.8;
    private static double elasticity = 0.0275;
    public boolean split = false;

    public Engine(List<Body> bodies, int numPlayers)
    {
    	this.numPlayers = numPlayers;
        objects = bodies;
        forces = new ArrayList<Vector>();
        playerScores = new int[numPlayers];
        for(int i = 0; i < numPlayers; i++) {
            playerScores[i] = 0;
        }
    }
    
    public void addVectorToThrust(Vector add){
        
    }

    public void addBody(Body b)
    {
        objects.add(b);
    }

    private PointD CheckBounds(Body b)
    {
        PointD v = b.getVelocity().getHead();
        PointD d = b.getPosition().getHead();
        double width = b.getWidth();
        double height = b.getHeight();

        PointD result = new PointD(1,1);
        if (d.X + width >= width || d.X - width / 2.0 <= 0)
            result.X = -1.0;
        if (d.Y + height >= height || d.Y - height / 2.0 <= 0)
            result.Y = -1.0;
        return result;
    }

    public List<Body> update(List<Body> bodies){

        objects = bodies;
        detectCollisions();
        for(Body body : objects){

            body.update();
            body.setCollided(false);
        }
        return objects;
    }

    public void detectCollisions()
    {
        for (Body b : objects)
        {
            if (!b.getCollided())
            {
                for (Body b2 : objects)
                {
                    if (b.equals(b2))
                        continue;
                    if (b.contacts(b2))
                    {
                    	/**
                    	 * ship and asteroid
                    	 */
                    	//If body one is a ship and body 2 is not a ship or a gun, or, body two is a ship and body one is not a ship or a gun
                    	if((b.isShip() && !(b2.isShip() || b2.isGun())) || (b2.isShip() && !(b.isShip() || b.isGun()))) {
                            //game-over
                            if(b2.isShip())
                            {
                               b2.setGameOver(true);
                            }else
                            {
                               b.setGameOver(true);
                            }
                    	}      
                    	
                    	
                    	/**
                    	 * Gun and asteroid
                    	 */
                    	//if body one is a gun and body two is not a ship or a gun, or, body two is a gun and body two is not a ship or a gun
                    	//		  false=ship	   false
                        else if ((b.isGun() && !(b2.isShip() || b2.isGun())) || (b2.isGun() && !(b.isShip() || b.isGun())))//Gun object, but not ship object = Gun and Asteroid collision
                        {
                            if(b.isGun())
                                playerScores[((GunAttack) b).getOwner()] += 10;
                            if(b2.isGun())
                                playerScores[((GunAttack) b2).getOwner()] += 10;
                            splitAsteroid(b, b2);
                        	//For now, destroy both the asteroid and bullet
                        	b.setDestroyed(true);
                        	b2.setDestroyed(true);
                        }    
                    	
                    	
                    	/**
                    	 * asteroid and asteroid
                    	 */
                    	//if body one is not a ship or a gun and body two is not a ship or a gun
                        else if(!(b.isShip() || b.isGun()) && !(b2.isShip() || b2.isGun()))//No Gun object and no Ship object = Asteroid & Asteroid collision
                        {
                            elasticCollision(b, b2);
                        }
                    }
                }
            }
        }
            
    }
    
    public int getScore(int playerNum) {
    	return playerScores[playerNum];
    }
    
    public int[] getScores() {
    	return playerScores;
    }

    static void elasticCollision(Body body1, Body body2)
    {

        //trying to implement the billards elastic collision; link below
        //http://archive.ncsa.illinois.edu/Classes/MATH198/townsend/math.html

        Vector v1 = body1.getVelocity();
        Vector v2 = body2.getVelocity();

        double m1 = body1.getMass();
        double m2 = body2.getMass();

        //center points of body 1 and 2...hopefully
        PointD b1p = new PointD(body1.getPosition().getHead());
        PointD b2p = new PointD(body2.getPosition().getHead());

        //vectors between the two bodies
        Vector b1n = new Vector(b2p, b1p);
        Vector b2n = new Vector(b1p, b2p);


        //PointD b1MINUSb2 = new PointD(b1p.X - b2p.X, b1p.Y - b2p.Y);
        PointD b1MINUSb2 = new PointD(body1.getPosition().getVector().X - body2.getPosition().getVector().X, body1.getPosition().getVector().Y - body2.getPosition().getVector().Y);
        
        //getting the normal to the collision plane
        PointD N = new PointD(b1MINUSb2.X / PhysMath.Pythagoras(new PointD(b1MINUSb2.X, b1MINUSb2.Y)), b1MINUSb2.Y / PhysMath.Pythagoras(new PointD(b1MINUSb2.X, b1MINUSb2.Y)));
        PointD negN = new PointD(N.X * -1, N.Y * -1);

        //derive velocity vectors
        double velBP1 = PhysMath.DP(b1n.getVector(), negN);
        PointD velB1 = new PointD(velBP1 * negN.X, velBP1 * negN.Y);

        double velBP2 = PhysMath.DP(b2n.getVector(), N);
        PointD velB2 = new PointD(velBP2 * N.X, velBP2 * N.Y);

        //GET TANGENTIAL COMPONENTS
        PointD Vt1 = new PointD(velB1.X - b1n.getVector().X, velB1.Y - b1n.getVector().Y);
        PointD Vt2 = new PointD(velB2.X - b2n.getVector().X, velB2.Y - b2n.getVector().Y);

        //(m1/m2) *
        //(m2/m1) *
        //Velocity vectors AFTER impact

        //PointD Vf1 = new PointD(Vt1.X + b2n.getVector().X, Vt1.Y + b2n.getVector().Y);
        //PointD Vf2 = new PointD(Vt2.X + b1n.getVector().X, Vt2.Y + b1n.getVector().Y);
        //PointD Vf1 = new PointD(Vt1.X + velB2.X, Vt1.Y + velB2.Y);
        //PointD Vf2 = new PointD(Vt2.X + velB1.X, Vt2.Y + velB1.Y);

        //PointD Vf1 = new PointD(Vt1.X + b2n.getVector().X, Vt1.Y + b2n.getVector().Y);
        //PointD Vf2 = new PointD(Vt2.X + b1n.getVector().X, Vt2.Y + b1n.getVector().Y);


        PointD Vf1 = new PointD(Vt1.X + velB2.X, Vt1.Y + velB2.Y);
        PointD Vf2 = new PointD(Vt2.X + velB1.X, Vt2.Y + velB1.Y);


        Vf1.multiplyByDouble(m2 / (double)m1);
        Vf2.multiplyByDouble(m1 / (double)m2);

        if(!(body1.isGun() || body2.isGun())){
	        Vf1.multiplyByDouble(elasticity);
	        Vf2.multiplyByDouble(elasticity);
        }
        body1.setVelocity(new Vector(Vf1.X, Vf1.Y));
        body2.setVelocity(new Vector(Vf2.X, Vf2.Y));

       // v1.getHead().multiplyByDouble(m1 / (double)m2);
       // v2.getHead().multiplyByDouble(m2 / (double)m1);

        body1.setVelocity(new Vector(Vf1, v1.getHead()));
        body2.setVelocity(new Vector(Vf2, v2.getHead()));

        body1.setCollided(true);
        body2.setCollided(true);
        //Console.WriteLine("Collision between " + body1.getName() + " and " + body2.getName());
    }

    private void collide(Body body1, Body body2)
    {
        //m1v1 + m2v2 = m1v1' + m2v2'
        //p1 + p2 = p1' + p2'
        //p1' = p1 + p2 - p2'= m1v1 + m2(v2 - v2')
        //v1' = v1 + (m2(v2 - v2') / m1)
        //Ek = 1/2m(v^2)
        Vector v1 = body1.getVelocity();
        Vector v2 = body2.getVelocity();

        double m1 = body1.getMass();
        double m2 = body2.getMass();

        PointD head1 = new PointD((v2.getVector().X * m2 / m1) + v2.getTail().X, (v2.getVector().X * m2 / m1) + v2.getTail().Y);

        PointD head2 = new PointD((v1.getVector().X * m1 / m2) + v1.getTail().X, (v1.getVector().X * m1 / m2) + v1.getTail().Y);

        body1.setVelocity(new Vector(head1, v1.getHead()));
        body2.setVelocity(new Vector(head2, v2.getHead()));

        body1.setCollided(true);
        body2.setCollided(true);
        //Console.WriteLine("Collision between " + body1.getName() + " and " + body2.getName());
    }

    private void splitAsteroid(Body b1, Body b2)
    {
        split = true;
        
        
        if (!b1.isGun())//b2 is Gun, b1 is Asteroid
        {
            switch(b1.GetAsteroidType())
            {
                case 1:
                {
                    objects.add(new AsteroidM(b1,b2, 1));//left asteroid
                    objects.add(new AsteroidM(b1,b2, 2));//right asteroid  
                    break;
                }
                
                case 2:
                {
                    objects.add(new AsteroidS(b1,b2, 1));//left asteroid
                    objects.add(new AsteroidS(b1,b2, 2));//right asteroid
                    break;
                }
                
                case 3:
                {
                    b1.setDestroyed(true);//destroy the AsteroidS object
                    break;
                }
            
            }           

            b2.setDestroyed(true);//destroy the GunAttack object
        }
        else//b1 is Gun, b2 is Asteroid
        { 
            
            switch(b2.GetAsteroidType())
            {
                case 1:
                {
                    objects.add(new AsteroidM(b2,b1, 1));//left asteroid
                    objects.add(new AsteroidM(b2,b1, 2));//right asteroid     
                    break;
                }
                
                case 2:
                {
                    objects.add(new AsteroidS(b2,b1, 1));//left asteroid
                    objects.add(new AsteroidS(b2,b1, 2));//right asteroid
                    break;
                }
                
                case 3:
                {
                    b2.setDestroyed(true);//destroy the AsteroidS object
                    break;
                }
            
            }
            b1.setDestroyed(true);//destroy the GunAttack object
        }
            
//        Body [] temp2 = (Body[]) objects.toArray();
//        for (int i = 0; i < temp2.length; i++)
//        {
//            if (!temp2[i].isDestroyed())
//            {
//                temp.add(temp2[i]);
//            }
//        }
          
    }

}
