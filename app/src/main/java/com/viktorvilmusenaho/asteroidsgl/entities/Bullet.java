package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;

public class Bullet extends GLEntity {
    private static Mesh BULLET_MESH = new Mesh(Mesh.POINT, GLES20.GL_POINTS); //Q&D pool, Mesh.POINT is just [0,0,0] float array
    private static final float TO_RADIANS = (float) Math.PI / 180.0f;
    private static final float PLAYER_SHOT_SPEED = 120f;
    private static final float ENEMY_SHOT_SPEED = 40f;
    public static final float TIME_TO_LIVE = 2.0f; //seconds

    public float _ttl = TIME_TO_LIVE;
    public boolean _playerFriendly;

    public Bullet() {
        setColors(1, 0, 1, 1);
        _mesh = BULLET_MESH; //all bullets use the exact same mesh
    }

    public void fireFrom(GLEntity source) {
        double theta;
        float speed;
        if(source instanceof EnemyShip) {
            theta = Math.atan2((_game._player._x - source._x), (source._y - (_game._player._y + (_game._player._height / 2)))); // thanks to user Jim Lewis on https://stackoverflow.com/questions/2676719/calculating-the-angle-between-the-line-defined-by-two-points
            _playerFriendly = false;
            speed = ENEMY_SHOT_SPEED;
            _velX = 0;
        } else {
            theta = source._rotation * TO_RADIANS;
            _playerFriendly = true;
            speed = PLAYER_SHOT_SPEED;
            _velX = source._velX;
        }
        _velY = source._velY;
        _velX += (float) Math.sin(theta) * speed;
        _velY -= (float) Math.cos(theta) * speed;
        _x = source._x + (float) Math.sin(theta) * (source._width * 0.5f);
        _y = source._y - (float) Math.cos(theta) * (source._height * 0.5f);
        _ttl = TIME_TO_LIVE;

    }

    @Override
    public boolean isDead(){
        return _ttl < 1;
    }

    public boolean isAlive() {
        return _ttl > 0;
    }

    public void killBullet() {
        _ttl = 0;
    }

    @Override
    public void update(double dt) {
        if (_ttl > 0) {
            _ttl -= dt;
            super.update(dt);
        }
    }

    @Override
    public void onCollision(GLEntity that) {
        super.onCollision(that);
        _ttl = 0;
    }

    @Override
    public void render(final float[] viewportMatrix) {
        if (_ttl > 0) {
            if(!_playerFriendly){
                setColors(1f,0f,0f,1f);
            } else {
                setColors(1, 0, 1, 1);
            }
            super.render(viewportMatrix);
        }
    }
}
