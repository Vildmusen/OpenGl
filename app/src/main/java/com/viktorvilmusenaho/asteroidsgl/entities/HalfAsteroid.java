package com.viktorvilmusenaho.asteroidsgl.entities;

public class HalfAsteroid extends Asteroid {

    private static final float POINTS_VALUE = 200;
    private static final float MAX_VEL = 20f;
    private static final float MIN_VEL = -20f;
    private static final float SIZE = 9;
    private static final int NUMBER_OF_CHILDREN = 2;

    public HalfAsteroid(float x, float y, int points) {
        super(x, y, points);
        build(SIZE);
        setSpeed(MIN_VEL, MAX_VEL);
    }

    @Override
    public void onCollision(GLEntity that) {
        _isAlive = false;
        _game.spawnAsteroids(NUMBER_OF_CHILDREN, this);
    }
}
