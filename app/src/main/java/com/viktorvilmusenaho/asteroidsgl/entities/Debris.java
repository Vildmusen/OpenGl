package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;
import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

public class Debris extends GLEntity {

    private static final float TIME_TO_LIVE = 0.6f; //seconds
    private static final float SPEED = 18;
    private static final float SIZE = 0.45f;
    public float _ttl = TIME_TO_LIVE;

    public Debris(){
        build();
        _isAlive = false;
    }

    private void build() {
        _width = SIZE;
        _height = _width;
        final double radius = _width*0.5;
        final float[] vertices = Mesh.generateLinePolygon(6, radius);
        _mesh = new Mesh(vertices, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }

    public void spawn(float x, float y) {
        _x = x;
        _y = y;
        _ttl = TIME_TO_LIVE;
        _isAlive = true;
        setSpeed();
    }

    private void setSpeed(){
        _velX = Utils.between(-1 * SPEED, 1 * SPEED);
        _velY = Utils.between(-1 * SPEED, 1 * SPEED);
        _velR = Utils.between(-4 * SPEED, 4 * SPEED);
    }

    @Override
    public void update(double dt) {
        if (_ttl > 0) {
            _ttl -= dt;
            setColors(1,1,1,_ttl / TIME_TO_LIVE); // Fade out
            super.update(dt);
        } else {
            _isAlive = false;
        }
    }

    @Override
    public void render(final float[] viewportMatrix) {
        if (_ttl > 0) {
            super.render(viewportMatrix);
        }
    }

}
