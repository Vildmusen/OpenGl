package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;
import com.viktorvilmusenaho.asteroidsgl.utils.JukeBox;
import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

import java.util.ArrayList;

public class Asteroid extends GLEntity {

    private static final float POINTS_VALUE = 100;
    public static final float SIZE = 12;
    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;
    private static final int NUMBER_OF_CHILDREN = 2;

    public ArrayList<Debris> _debrisPool = null;
    public int _points = 0;
    public float _pointsValue = 0;

    public Asteroid(final float x, final float y, int points, float sizeMultiplier, float speedMultiplier, ArrayList<Debris> debrisPool) {
        _points = points < 3 ? 3 : points;
        _pointsValue = POINTS_VALUE * 2 - (POINTS_VALUE * sizeMultiplier);
        _x = x;
        _y = y;
        build(SIZE * sizeMultiplier);
        setSpeed(MIN_VEL * speedMultiplier, MAX_VEL * speedMultiplier);
        _debrisPool = debrisPool;
    }

    public void build(float size) {
        _width = size;
        _height = _width;
        final double radius = _width * 0.5;
        final float[] vertices = Mesh.generateLinePolygon(_points, radius);
        _mesh = new Mesh(vertices, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }

    public void setSpeed(float min, float max) {
        _velX = Utils.between(min, max);
        _velY = Utils.between(min, max);
        _velR = Utils.between(min * 4, max * 4);
    }

    public void scatterPieces(float size) {
        float count = size;
        for (Debris d : _debrisPool) {
            if (!d.isDead()) {
                continue;
            }
            if (count > 0) {
                d.spawn(_x, _y);
                count--;
            }
        }
    }

    @Override
    public void onCollision(GLEntity that) {
        if(that instanceof Bullet){
            if(!((Bullet) that)._playerFriendly){
                return;
            }
        }
        _isAlive = false;
        _game.spawnAsteroids(NUMBER_OF_CHILDREN, this);
        _game._player._playerScore += _pointsValue;
        _game._jukeBox.play(JukeBox.DAMAGE, 0, 2);
        scatterPieces(SIZE);
    }

    @Override
    public void update(double dt) {
        _rotation++;
        super.update(dt);
    }
}
