package com.viktorvilmusenaho.asteroidsgl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.viktorvilmusenaho.asteroidsgl.GL.GLManager;
import com.viktorvilmusenaho.asteroidsgl.entities.Asteroid;
import com.viktorvilmusenaho.asteroidsgl.entities.Border;
import com.viktorvilmusenaho.asteroidsgl.entities.Bullet;
import com.viktorvilmusenaho.asteroidsgl.entities.GLEntity;
import com.viktorvilmusenaho.asteroidsgl.entities.HalfAsteroid;
import com.viktorvilmusenaho.asteroidsgl.entities.Player;
import com.viktorvilmusenaho.asteroidsgl.entities.QuarterAsteroid;
import com.viktorvilmusenaho.asteroidsgl.entities.Star;
import com.viktorvilmusenaho.asteroidsgl.entities.Text;
import com.viktorvilmusenaho.asteroidsgl.input.InputManager;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final String TAG = "GAME";
    public static final float WORLD_WIDTH = 160f;
    public static final float WORLD_HEIGHT = 90f;
    public static float METERS_TO_SHOW_X = 160f; //160m x 90m, the entire game world in view
    public static float METERS_TO_SHOW_Y = 90f; //TODO: calculate to match screen aspect ratio
    private static final int BG_COLOR = Color.rgb(100, 100, 100);
    private static int STAR_COUNT = 100;
    private static int ASTEROID_COUNT = 8;
    public static long SECOND_IN_NANOSECONDS = 1000000000;
    public static long MILLISECOND_IN_NANOSECONDS = 1000000;
    public static float NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    public static float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;
    private static final int BULLET_COUNT = (int) (Bullet.TIME_TO_LIVE / Player.TIME_BETWEEN_SHOTS) + 1;
    private static final int FPS_CALC_INTERVAL = 10;

    final double dt = 0.01;
    double accumulator = 0.0;
    double currentTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;

    private ArrayList<Star> _stars = new ArrayList<>();
    private ArrayList<Asteroid> _asteroids = new ArrayList<>();
    private ArrayList<Asteroid> _asteroidsToAdd = new ArrayList<>();
    private ArrayList<Text> _texts = new ArrayList<>();
    private Text _frameCountText = null;
    Bullet[] _bullets = new Bullet[BULLET_COUNT];
    private Player _player = null;
    private Border _border = null;
    private float[] _viewportMatrix = new float[4 * 4]; //In essence, it is our our Camera
    public InputManager _inputs = new InputManager(); //empty but valid default

    private double _framesPerSecond = 0;
    private double _totalFrames = 0;
    private double _lastTick = 0;

    public Game(Context context) {
        super(context);
        init();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        GLEntity._game = this;
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true); //context *may* be preserved and thus *may* avoid slow reloads when switching apps.
        // we always re-create the OpenGL context in onSurfaceCreated, so we're safe either way.

        for (int i = 0; i < BULLET_COUNT; i++) {
            _bullets[i] = new Bullet();
            _bullets[i].killBullet();
        }
        _lastTick = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        _frameCountText = new Text("0", WORLD_WIDTH - 28, 10);
        setRenderer(this);
    }

    public void setControls(final InputManager input) {
        _inputs = input;
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig eglConfig) {
        GLManager.buildProgram();
        float red = Color.red(BG_COLOR) / 255f;
        float green = Color.green(BG_COLOR) / 255f;
        float blue = Color.blue(BG_COLOR) / 255f;
        float alpha = 1f;
        _player = new Player(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        _border = new Border(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, WORLD_WIDTH, WORLD_HEIGHT);
        GLES20.glClearColor(red, green, blue, alpha);
        Random r = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            _stars.add(new Star(r.nextInt((int) WORLD_WIDTH), r.nextInt((int) WORLD_HEIGHT)));
        }
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            _asteroids.add(new Asteroid(i * 20, WORLD_HEIGHT / 2, (i % 2) + 5));
        }
    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(final GL10 unused) {
        update();
        render();
    }

    //trying a fixed time-step with accumulator, courtesy of
    //   https://gafferongames.com/post/fix_your_timestep/
    private void update() {
        final double newTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        final double frameTime = newTime - currentTime;
        currentTime = newTime;
        accumulator += frameTime;
        while (accumulator >= dt) {
            for (final Asteroid a : _asteroids) {
                a.update(dt);
            }
            _player.update(dt);
            accumulator -= dt;
        }
        for (final Bullet b : _bullets) {
            if (!b.isAlive()) {
                continue;
            }
            b.update(dt);
        }
        collisionDetection();
        removeDeadEntities();
        addNewAsteroids();
    }

    private void addNewAsteroids() {
        for (Asteroid a : _asteroidsToAdd) {
            if (_asteroids.size() < 20) {
                _asteroids.add(a);
            }
        }
        _asteroidsToAdd.clear();
    }

    private void render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        final int offset = 0;
        final float left = 0;
        final float right = METERS_TO_SHOW_X;
        final float bottom = METERS_TO_SHOW_Y;
        final float top = 0;
        final float near = 0f;
        final float far = 1f;
        Matrix.orthoM(_viewportMatrix, offset, left, right, bottom, top, near, far);
        for (final Star s : _stars) {
            s.render(_viewportMatrix);
        }
        for (final Asteroid a : _asteroids) {
            if (a.isDead()) {
                continue;
            } //skip
            a.render(_viewportMatrix);
        }
        _player.render(_viewportMatrix);
        for (final Text t : _texts) {

            t.render(_viewportMatrix);
        }
        for (final Bullet b : _bullets) {
            if (!b.isAlive()) {
                continue;
            } //skip
            b.render(_viewportMatrix);
        }
        _border.render(_viewportMatrix);
        renderFPS();
    }

    private void renderFPS() {
        if (_totalFrames % FPS_CALC_INTERVAL == 0) {
            _framesPerSecond = calculateFPS(System.nanoTime() * NANOSECONDS_TO_SECONDS);
            _frameCountText.setString(String.format("%d FPS", (int) _framesPerSecond));
        }
        _totalFrames++;
        _frameCountText.render(_viewportMatrix);
    }

    private double calculateFPS(float now) {
        double difference = now - _lastTick;
        _lastTick = now;
        return FPS_CALC_INTERVAL / difference;
    }

    private void collisionDetection() {
        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                continue;
            } //skip dead bullets
            for (final Asteroid a : _asteroids) {
                if (b.isColliding(a)) {
                    if (a.isDead()) {
                        continue;
                    }
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                }
            }
        }
        for (final Asteroid a : _asteroids) {
            if (a.isDead()) {
                continue;
            }
            if (_player.isColliding(a)) {
                _player.onCollision(a);
                a.onCollision(_player);
            }
        }
    }

    public void removeDeadEntities() {
        Asteroid temp;
        final int count = _asteroids.size();
        for (int i = count - 1; i >= 0; i--) {
            temp = _asteroids.get(i);
            if (temp.isDead()) {
                _asteroids.remove(i);
            }
        }
    }

    public boolean maybeFireBullet(final GLEntity source) {
        for (final Bullet b : _bullets) {
            if (!b.isAlive()) {
                b.fireFrom(source);
                return true;
            }
        }
        return false;
    }

    public void spawnAsteroids(int count, GLEntity asteroid) {
        for (int i = 0; i < count; i++) {
            if (asteroid instanceof HalfAsteroid) {
                _asteroidsToAdd.add(new QuarterAsteroid(asteroid.centerX(), asteroid.centerY(), ((Asteroid) asteroid)._points));
            }
            else {
                _asteroidsToAdd.add(new HalfAsteroid(asteroid.centerX(), asteroid.centerY(), ((Asteroid) asteroid)._points));
            }
        }
    }
}
