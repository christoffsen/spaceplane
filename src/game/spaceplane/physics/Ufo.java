package game.spaceplane.physics;

public class Ufo extends Body {
    private boolean gameOver = false;
    private Vector velocity;
    private double mass;
    protected int width;
    protected int height;
    protected Vector acceleration;
    protected Vector position;
    protected Vector forwardDirection;
    protected double constantAcceleration;
    protected boolean isUfo = true;

    public Ufo(int X, int Y) {
        position = new Vector(X, Y);
        width = 40;
        height = 40;
        mass = 250;
        velocity = new Vector(0, 0);
        forwardDirection = new Vector(1, 0);
        constantAcceleration = 0.10;
        acceleration = new Vector(0, 0);
    }

    public boolean gameOver() {
        return gameOver;
    }

    public void setGameOver() {
        this.gameOver = true;
    }

    public boolean isUfo() {
        return isUfo;
    }

    public boolean isDestroyed() {
        return nullified;
    }

    public void setDestroyed(boolean value) {
        nullified = value;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getMass() {
        return mass;
    }

    public void setCollided(boolean truth) {
        collidedThisIteration = truth;
    }

    public boolean getCollided() {
        return collidedThisIteration;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = new Vector(velocity.getHead().X, velocity.getHead().Y);
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Vector getAcceleration() {
        return acceleration;
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getDirection() {
        return forwardDirection;
    }

    public void setPosition(Vector point) {
        position = new Vector(point.getVector().X, point.getVector().Y);
    }

    public void setForward(double angleInRadians) {
        double angle = forwardDirection.getAngle();
        double X = Math.cos(angleInRadians);// gives X Velocity
        double Y = Math.sin(angleInRadians);// gives Y Velocity

        forwardDirection = new Vector(X, Y);
        forwardDirection.setAngle(angleInRadians);
    }

    public void moveForward() {
        // multiply velocity by direction X and Y and add to X and Y of position

        double AccXforward = forwardDirection.getUnitVector().X
                * constantAcceleration;
        double AccXforwardY = forwardDirection.getUnitVector().Y
                * constantAcceleration;

        acceleration = new Vector(AccXforward, AccXforwardY);

        velocity.getHead().X += AccXforward;
        velocity.getHead().Y += AccXforwardY;

    }

    public void update() {

        velocity.add(acceleration);
        position.add(velocity);

        acceleration = new Vector(0, 0);
    }

}
