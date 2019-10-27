package com.viktorvilmusenaho.asteroidsgl.entities;

public class QuarterAsteroid extends HalfAsteroid {

    private static final float POINTS_VALUE = 400;
    private static final float MAX_VEL = 24f;
    private static final float MIN_VEL = -24f;
    private static final float SIZE = 6;

    public QuarterAsteroid(float x, float y, int points) {
        super(x, y, points);
        build(SIZE);
        setSpeed(MIN_VEL, MAX_VEL);
    }

    @Override
    public void onCollision(GLEntity that) {
        _isAlive = false;
    }
}
