package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;
import com.viktorvilmusenaho.asteroidsgl.Game;
import com.viktorvilmusenaho.asteroidsgl.utils.JukeBox;
import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

import java.util.ArrayList;

public class EnemyShip extends GLEntity {

    private static final float SHOT_COOLDOWN = 1.0f;
    private static final float POINTS_VALUE = 1000;
    public static final float SIZE = 12;
    private static final float VEL = -40f;

    public ArrayList<Debris> _debrisPool = null;
    private float _shootCounter = 0f;
    private int _direction = 0;

    public EnemyShip(ArrayList<Debris> debrisPool) {
        super();
        _isAlive = false;
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

    public void spawn(){
        _isAlive = true;
        float randY = Utils.nextFloat() * Utils.between(-1, 1);
        _direction = randY < 0 ? 1 : -1;
        _x = randY < 0 ? Game.WORLD_WIDTH + _width * 2 : 0 - _width * 2;
        _velX = randY < 0 ? VEL : VEL * -1;
        _y = (_height * 1.5f) + (Game.WORLD_HEIGHT - _height * 3f) * Math.abs(randY); // _height equations added to nicely fit on screen, and not hover at the edges.
    }

    private void deSpawn(){
        _isAlive = false;
        _x = Game.WORLD_WIDTH + _width;
        _y = Game.WORLD_HEIGHT + _height;
        _game._jukeBox.play(JukeBox.DAMAGE, 0, 2);
    }

    @Override
    public void render(float[] viewportMatrix) {
        if(_isAlive){
            super.render(viewportMatrix);
        }
    }

    @Override
    public void onCollision(GLEntity that) {
        scatterPieces(SIZE);
        _game._player._playerScore += POINTS_VALUE;
        deSpawn();
    }

    @Override
    public void update(double dt) {
        if(_isAlive){
            _shootCounter = _shootCounter > 0 ? _shootCounter -= dt : 0;
            if(_shootCounter == 0){
                _game.maybeFireBullet(this);
                _shootCounter = SHOT_COOLDOWN;
            }
            _x += _velX * dt;

            if (left() > Game.WORLD_WIDTH && _direction == -1) {
                deSpawn();
            } else if (right() < 0  && _direction == 1) {
                deSpawn();
            }
        }
    }
}
