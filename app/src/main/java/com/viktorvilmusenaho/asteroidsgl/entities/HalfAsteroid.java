package com.viktorvilmusenaho.asteroidsgl.entities;

import com.viktorvilmusenaho.asteroidsgl.utils.JukeBox;

public class HalfAsteroid extends Asteroid {

    private static final float POINTS_VALUE = 200;
    private static final float MAX_VEL = 20f;
    private static final float MIN_VEL = -20f;
    private static final float SIZE = 9;
    private static final int NUMBER_OF_CHILDREN = 2;

    public HalfAsteroid(float x, float y, int points, float speedMult) {
        super(x, y, points, speedMult);
        build(SIZE);
        setSpeed(MIN_VEL * speedMult, MAX_VEL * speedMult);
    }

    @Override
    public void onCollision(GLEntity that) {
        _isAlive = false;
        _game._player._playerScore += POINTS_VALUE;
        _game.spawnAsteroids(NUMBER_OF_CHILDREN, this);
        _game._jukeBox.play(JukeBox.DAMAGE, 0, 2);
    }
}
