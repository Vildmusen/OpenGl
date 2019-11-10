package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;

public class Flame extends GLEntity {

    static final float ROTATION_VELOCITY = 330f; //TODO: game play values!

    public Flame(float x, float y){
        super();
        _x = x;
        _y = y;
        _width = 5f; //TODO: gAmEPlaY VaLuES
        _height = 3f;
        float vertices[] = { // in counterclockwise order:
                0.0f, 0.2f, 0.0f,    // top
                -0.2f, -0.2f, 0.0f,    // bottom left
                0.2f, -0.2f, 0.0f    // bottom right
        };
        _mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        _mesh.setWidthHeight(_width, _height);
    }

    public void update(double dt, float x, float y) {
        _rotation += (dt * ROTATION_VELOCITY) * _game._inputs._horizontalFactor;
        _x = x;
        _y = y + 8;
        super.update(dt);
    }
}
