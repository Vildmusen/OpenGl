package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;
import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

public class Player extends GLEntity {

    public static final float TIME_BETWEEN_SHOTS = 0.1f;
    private static final int GRACE_PERIOD_LENGTH = 60;
    private static final String TAG = "Player";
    static final float ROTATION_VELOCITY = 270f; //TODO: game play values!
    static final float MAX_VEL = 100f;
    static final float THRUST = 8f;
    static final float DRAG = 0.99f;

    private float _bulletCooldown = 0;
    private float _gracePeriod = 0;

    public Player(float x, float y) {
        super();
        _x = x;
        _y = y;
        _width = 8f; //TODO: gAmEPlaY VaLuES
        _height = 12f;
        float vertices[] = { // in counterclockwise order:
                0.0f, 0.5f, 0.0f,    // top
                -0.5f, -0.5f, 0.0f,    // bottom left
                0.5f, -0.5f, 0.0f    // bottom right
        };
        _mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();
    }

    @Override
    public void update(double dt) {
        _bulletCooldown -= dt;
        _gracePeriod -= dt;
        if (_game._inputs._pressingA && _bulletCooldown <= 0) {
            setColors(1, 0, 1, 1);
            if (_game.maybeFireBullet(this)) {
                _bulletCooldown = TIME_BETWEEN_SHOTS;
            }
        } else {
            setColors(1.0f, 1, 1, 1);
        }
        _rotation += (dt * ROTATION_VELOCITY) * _game._inputs._horizontalFactor;
        if (_game._inputs._pressingB) {
            final float theta = _rotation * (float) Utils.TO_RAD;
            _velX += (float) Math.sin(theta) * THRUST;
            _velY -= (float) Math.cos(theta) * THRUST;
        }
        _velX *= DRAG;
        _velY *= DRAG;
        _velX = Utils.clamp(_velX, -MAX_VEL, MAX_VEL);
        _velY = Utils.clamp(_velY, -MAX_VEL, MAX_VEL);
        super.update(dt);
    }

    @Override
    public void onCollision(GLEntity that) {
        _gracePeriod = GRACE_PERIOD_LENGTH;
    }

    @Override
    public boolean isColliding(final GLEntity that) {
        return _gracePeriod > 0 && areBoundingSpheresOverlapping(this, that);
    }

    @Override
    public void render(float[] viewportMatrix) {
        super.render(viewportMatrix);
    }
}
