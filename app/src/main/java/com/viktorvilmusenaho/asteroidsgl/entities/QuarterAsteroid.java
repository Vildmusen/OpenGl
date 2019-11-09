package com.viktorvilmusenaho.asteroidsgl.entities;

import com.viktorvilmusenaho.asteroidsgl.utils.JukeBox;

public class QuarterAsteroid extends HalfAsteroid {

    private static final float POINTS_VALUE = 400;
    private static final float MAX_VEL = 24f;
    private static final float MIN_VEL = -24f;
    private static final float SIZE = 6;

    public QuarterAsteroid(float x, float y, int points, float speedMult) {
        super(x, y, points, speedMult);
        build(SIZE);
        setSpeed(MIN_VEL * speedMult, MAX_VEL * speedMult);
    }

    @Override
    public void onCollision(GLEntity that)
    {
        _game._jukeBox.play(JukeBox.DAMAGE, 0, 2);
        _game._player._playerScore += POINTS_VALUE;
        _isAlive = false;
    }
}
