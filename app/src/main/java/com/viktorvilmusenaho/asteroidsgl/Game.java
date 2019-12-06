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
import com.viktorvilmusenaho.asteroidsgl.entities.Debris;
import com.viktorvilmusenaho.asteroidsgl.entities.EnemyShip;
import com.viktorvilmusenaho.asteroidsgl.entities.GLEntity;
import com.viktorvilmusenaho.asteroidsgl.entities.Player;
import com.viktorvilmusenaho.asteroidsgl.entities.Star;
import com.viktorvilmusenaho.asteroidsgl.entities.Text;
import com.viktorvilmusenaho.asteroidsgl.input.InputManager;
import com.viktorvilmusenaho.asteroidsgl.utils.JukeBox;
import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

import java.io.BufferedReader;
import java.io.UTFDataFormatException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final String TAG = "GAME";
    public static final float WORLD_WIDTH = 160f;
    public static final float WORLD_HEIGHT = 90f;
    private static final float SHIP_SPAWN_CHANCE = 0.002f;
    public static float METERS_TO_SHOW_X = 160f;
    public static float METERS_TO_SHOW_Y = 90f;
    private static final int BG_COLOR = Color.rgb(100, 100, 100);
    private static final int DEBRIS_COUNT = 100;
    private static int STAR_COUNT = 100;
    private static int ASTEROID_COUNT = 3;
    public static long SECOND_IN_NANOSECONDS = 1000000000;
    public static long MILLISECOND_IN_NANOSECONDS = 1000000;
    public static float NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    public static float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;
    private static final int BULLET_COUNT = (int) (Bullet.TIME_TO_LIVE / Player.TIME_BETWEEN_SHOTS) + 1;
    private static final int FPS_CALC_INTERVAL = 10;
    private static final int NEXT_LEVEL_MESSAGE_DURATION = 150;
    private static float HALF_ASTEROID_SIZE = 0.75f;
    private static float QUARTER_ASTEROID_SIZE = 0.5f;

    final double dt = 0.01;
    double accumulator = 0.0;
    double currentTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;

    private ArrayList<Star> _stars = new ArrayList<>();
    private ArrayList<Asteroid> _asteroids = new ArrayList<>();
    private ArrayList<Asteroid> _asteroidsToAdd = new ArrayList<>();
    private Bullet[] _bullets = new Bullet[BULLET_COUNT];
    private float _speedMultiplier = 1f;
    private int _additionalAsteroids = 0;
    private EnemyShip _enemyship = null;
    public Player _player = null;
    private Border _border = null;
    private float[] _viewportMatrix = new float[4 * 4]; //In essence, it is our our Camera
    public InputManager _inputs = new InputManager(); //empty but valid default

    private double _framesPerSecond = 0;
    private double _totalFrames = 0;
    private double _lastTick = 0;
    private Text _frameCountText = null;
    private Text _levelText = null;
    private Text _playerHealthText = null;
    private Text _gameOverText = null;
    private Text _gameOverMessage = null;
    private Text _scoreText = null;
    private Text _newLevelText = null;
    private Text _newLevelInfo = null;
    private boolean _gameOver;
    private int _currentLevel = 1;

    public JukeBox _jukeBox = null;
    private int _messageCounter = 0;
    private ArrayList<Debris> _debrisPool = new ArrayList<>();
    public boolean _paused = false;

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
        resetGame();
        final String gameOverText = "GAME OVER";
        final String gameOverMessage = "Press B to play again";
        float textWidth = gameOverText.length() * (Text.GLYPH_WIDTH + Text.GLYPH_SPACING);
        _gameOverText = new Text(gameOverText, WORLD_WIDTH / 2 - (textWidth / 4), WORLD_HEIGHT * 0.4f); // I have no idea why "textWidth / 4" gives the right offset...
        textWidth = gameOverMessage.length() * (Text.GLYPH_WIDTH + Text.GLYPH_SPACING);
        _gameOverMessage = new Text(gameOverMessage, WORLD_WIDTH / 2 - (textWidth / 4), WORLD_HEIGHT * 0.5f);
        _jukeBox = new JukeBox(this.getContext());
        setRenderer(this);
    }

    // Extracted initialization to be run again at game restart
    private void resetGame() {
        for (int i = 0; i < BULLET_COUNT; i++) {
            _bullets[i] = new Bullet();
            _bullets[i].killBullet();
        }
        _additionalAsteroids = 0;
        _currentLevel = 1;
        _lastTick = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        _playerHealthText = new Text("Health 0", 10, 20);
        _scoreText = new Text("Score 0", 10, 10);
        _levelText = new Text("Level 1", WORLD_WIDTH - 32, 10);
        _frameCountText = new Text("0", WORLD_WIDTH - 28, 20);
        try {
            _asteroids.clear();
            _stars.clear();
        } catch (Exception e) {
            Log.d(TAG, "could not clear entity lists: " + e.getMessage());
        }
    }

    private void initiateEntities() {
        _player = new Player(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        _border = new Border(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, WORLD_WIDTH, WORLD_HEIGHT);
        _enemyship = new EnemyShip(_debrisPool);
        for (int i = 0; i < STAR_COUNT; i++) {
            _stars.add(new Star(Utils.nextInt((int) WORLD_WIDTH), Utils.nextInt((int) WORLD_HEIGHT)));
        }
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            _asteroids.add(new Asteroid(i * 20 * Utils.nextFloat(), WORLD_HEIGHT * Utils.nextFloat(), (i % 2) + 5, 1, 1, _debrisPool));
        }
        for (int i = 0; i < DEBRIS_COUNT; i++) {
            _debrisPool.add(new Debris());
        }
    }

    private void newAsteroids(final int additionalAsteroids, final float speedMultiplier) {
        for (int i = 0; i < ASTEROID_COUNT + additionalAsteroids; i++) {
            _asteroids.add(new Asteroid(i * 20 * Utils.nextFloat(), WORLD_HEIGHT * Utils.nextFloat(), (i % 2) + 5, 1, speedMultiplier, _debrisPool));
        }
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
        GLES20.glClearColor(red, green, blue, alpha);
        initiateEntities();
        _jukeBox.play(JukeBox.BACKGROUND, -1, 3);
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
        if (!_gameOver) {
            final double newTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
            final double frameTime = newTime - currentTime;
            currentTime = newTime;
            accumulator += frameTime;
            while (accumulator >= dt) {
                for (final Asteroid a : _asteroids) {
                    a.update(dt);
                }
                if(_enemyship._isAlive){
                    _enemyship.update(dt);
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
            for (final Debris d : _debrisPool) {
                if (d.isDead()) {
                    continue;
                }
                d.update(dt);
            }
            maybeSpawnShip();
            collisionDetection();
            removeDeadEntities();
            addNewAsteroids();
            checkGameOver();
            checkLevelCleared();
        } else {
            if (_inputs._pressingA) {
                resetGame();
                initiateEntities();
                _gameOver = false;
            }
        }
    }

    private void maybeSpawnShip() {
        if(Utils.nextFloat() < SHIP_SPAWN_CHANCE && _enemyship.isDead()) {
            _enemyship.spawn();
        }
    }

    private void checkGameOver() {
        if (_player._health <= 0) {
            _gameOver = true;
        }
    }

    private void checkLevelCleared() {
        if (_asteroids.size() == 0) {
            _jukeBox.play(JukeBox.START, 0, 3);
            _additionalAsteroids++;
            _currentLevel++;
            _speedMultiplier += 0.1f;
            generateNextLevelText();
            newAsteroids(_additionalAsteroids, _speedMultiplier);
            killAllBullets();
            _player.setGracePeriod();
        }
    }

    private void generateNextLevelText() {
        String nextLevelText = String.format("Level %s", _additionalAsteroids + 1);
        String nextLevelInfo = String.format("%s Asteroids at %sz speed", ASTEROID_COUNT + _additionalAsteroids, (int) (_speedMultiplier * 100));
        float textWidth = nextLevelText.length() * (Text.GLYPH_WIDTH + Text.GLYPH_SPACING);
        _newLevelText = new Text(nextLevelText, WORLD_WIDTH / 2 - (textWidth / 4), WORLD_HEIGHT * 0.4f); // I have no idea why "textWidth / 4" gives the right offset... Shouldn't it be /2?
        textWidth = nextLevelInfo.length() * (Text.GLYPH_WIDTH + Text.GLYPH_SPACING);
        _newLevelInfo = new Text(nextLevelInfo, WORLD_WIDTH / 2 - (textWidth / 4), WORLD_HEIGHT * 0.5f);
        _messageCounter = NEXT_LEVEL_MESSAGE_DURATION;
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
        if (!_gameOver) {
            for (final Asteroid a : _asteroids) {
                if (a.isDead()) {
                    continue;
                } //skip
                a.render(_viewportMatrix);
            }
            _player.render(_viewportMatrix);
            for (final Bullet b : _bullets) {
                if (!b.isAlive()) {
                    continue;
                } //skip
                b.render(_viewportMatrix);
            }
            for (final Debris d : _debrisPool) {
                if (d.isDead()) {
                    continue;
                }
                d.render(_viewportMatrix);
            }
            if(_enemyship._isAlive){
                _enemyship.render(_viewportMatrix);
            }
            _border.render(_viewportMatrix);
            renderHUD();
        } else {
            renderGameOver();
        }
    }

    private void renderHUD() {
        renderFPS();
        renderLevelText();
        renderScoreText();
        renderPlayerHealth();
        if (_messageCounter > 0) {
            renderNextLevel();
            _messageCounter--;
        }
    }

    private void renderNextLevel() {
        _newLevelText.render(_viewportMatrix);
        _newLevelInfo.render(_viewportMatrix);
    }

    private void renderScoreText() {
        _scoreText.setString(String.format("Score %s", _player._playerScore));
        _scoreText.render(_viewportMatrix);
    }

    private void renderPlayerHealth() {
        if (_player._health >= 0) {
            _playerHealthText.setString(String.format("Health %s", _player._health));
        }
        _playerHealthText.render(_viewportMatrix);
    }

    private void renderLevelText() {
        _levelText.setString(String.format("Level %s", _currentLevel));
        _levelText.render(_viewportMatrix);
    }

    private void renderFPS() {
        if (_totalFrames % FPS_CALC_INTERVAL == 0) {
            _framesPerSecond = calculateFPS(System.nanoTime() * NANOSECONDS_TO_SECONDS);
            _frameCountText.setString(String.format("%d FPS", (int) _framesPerSecond));
        }
        _totalFrames++;
        _frameCountText.render(_viewportMatrix);
    }

    private void renderGameOver() {
        _gameOverText.render(_viewportMatrix);
        _gameOverMessage.render(_viewportMatrix);
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
                    b.onCollision(a);
                    a.onCollision(b);
                }
            }
            if (!b._playerFriendly && b.isColliding(_player)) {
                _player.onCollision(b);
                b.onCollision(_player);
            }
            if(_enemyship != null && b._playerFriendly && _enemyship.isColliding(b)){
                _enemyship.onCollision(b);
                b.onCollision(_enemyship);
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
        if(_enemyship != null && _player.isColliding(_enemyship)){
            _player.onCollision(_enemyship);
            _enemyship.onCollision(_player);
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
                _jukeBox.play(JukeBox.SHOOT, 0, 1);
                return true;
            }
        }
        return false;
    }

    private void killAllBullets() {
        for (final Bullet b : _bullets) {
            b.killBullet();
        }
    }

    public void spawnAsteroids(int count, Asteroid asteroid) {
        for (int i = 0; i < count; i++) {
            if (asteroid._width == Asteroid.SIZE) {
                _asteroidsToAdd.add(new Asteroid(asteroid.centerX(), asteroid.centerY(), asteroid._points, HALF_ASTEROID_SIZE, _speedMultiplier, _debrisPool));
            } else if (asteroid._width == Asteroid.SIZE * 0.75) {
                _asteroidsToAdd.add(new Asteroid(asteroid.centerX(), asteroid.centerY(), asteroid._points, QUARTER_ASTEROID_SIZE, _speedMultiplier, _debrisPool));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        _jukeBox.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        _jukeBox.onResume();
    }


}
