package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;
import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

public class Asteroid extends GLEntity {

    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;

    public Asteroid(final float x, final float y, int points) {
        if (points < 3) {
            points = 3;
        } //triangles or more, please. :)
        _x = x;
        _y = y;
        _width = 12;
        _height = _width;
        _velX = Utils.between(MIN_VEL, MAX_VEL);
        _velY = Utils.between(MIN_VEL, MAX_VEL);
        _velR = Utils.between(MIN_VEL*4, MAX_VEL*4);
        final double radius = _width*0.5;
        final float[] vertices = Mesh.generateLinePolygon(points, radius);
        _mesh = new Mesh(vertices, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }



    @Override
    public void update(double dt) {
        _rotation++;
        super.update(dt);
    }
}
