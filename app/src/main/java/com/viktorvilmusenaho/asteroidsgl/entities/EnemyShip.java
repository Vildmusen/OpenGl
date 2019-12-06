package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;
import com.viktorvilmusenaho.asteroidsgl.utils.JukeBox;

import java.util.ArrayList;

public class EnemyShip extends GLEntity {

    private static final float SHOT_COOLDOWN = 1.0f;
    private static final float POINTS_VALUE = 1000;
    public static final float SIZE = 12;
    private static final float VEL = -10f;

    public ArrayList<Debris> _debrisPool = null;
    private float _shootCounter = 0f;

    public EnemyShip(final float x, final float y, ArrayList<Debris> debrisPool) {
        super();
        _x = x;
        _y = y;
        _width = 12f;
        _height = 10f;
        _shootCounter = SHOT_COOLDOWN;
        float vertices[] = {
                // Bottom part
                1.5f, 0.0f, 0.0f,
                -1.5f, 0.0f, 0.0f,

                -1.5f, 0.0f, 0.0f,
                -0.5f, -0.25f, 0.0f,

                -0.5f, -0.25f, 0.0f,
                0.5f, -0.25f, 0.0f,

                0.5f, -0.25f, 0.0f,
                1.5f, 0.0f, 0.0f,

                // Top part
                0.5f, 0.0f, 0.0f,
                0.5f, 0.25f, 0.0f,

                0.5f, 0.25f, 0.0f,
                0.25f, 0.4f, 0.0f,

                0.25f, 0.4f, 0.0f,
                -0.25f, 0.4f, 0.0f,

                -0.25f, 0.4f, 0.0f,
                -0.5f, 0.25f, 0.0f,

                -0.5f, 0.25f, 0.0f,
                -0.5f, 0.0f, 0.0f
        };
        _mesh = new Mesh(vertices, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();
        _debrisPool = debrisPool;
        _velX = VEL;
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
        _isAlive = false;
        _game._player._playerScore += POINTS_VALUE;
        _game._jukeBox.play(JukeBox.DAMAGE, 0, 2);
        scatterPieces(SIZE);
    }

    @Override
    public void update(double dt) {
        _shootCounter = _shootCounter > 0 ? _shootCounter -= dt : 0;
        if(_shootCounter == 0){
            _game.maybeFireBullet(this);
            _shootCounter = SHOT_COOLDOWN;
        }
        super.update(dt);
    }
}
