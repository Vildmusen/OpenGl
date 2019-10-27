package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;
import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

import java.nio.charset.MalformedInputException;

public class Asteroid extends GLEntity {

    private static final float POINTS_VALUE = 100;
    private static final float SIZE = 12;
    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;
    private static final int NUMBER_OF_CHILDREN = 2;
    public int _points = 0;

    public Asteroid(final float x, final float y, int points) {
        _points = points < 3 ? 3 : points;
        _x = x;
        _y = y;
        build(SIZE);
        setSpeed(MIN_VEL, MAX_VEL);
    }

    public void build(float size) {
        _width = size;
        _height = _width;
        final double radius = _width*0.5;
        final float[] vertices = Mesh.generateLinePolygon(_points, radius);
        _mesh = new Mesh(vertices, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }

    public void setSpeed(float min, float max){
        _velX = Utils.between(min, max);
        _velY = Utils.between(min, max);
        _velR = Utils.between(min*4, max*4);
    }

    @Override
    public void onCollision(GLEntity that) {
        _isAlive = false;
        _game.spawnAsteroids(NUMBER_OF_CHILDREN, this);
    }

    @Override
    public void update(double dt) {
        _rotation++;
        super.update(dt);
    }
}
