package com.viktorvilmusenaho.asteroidsgl.input;

public class InputManager {
    public float _horizontalFactor = 0.0f;

    public boolean _pressingB = false;

    public boolean _pressingA = false;
    public boolean _releasedA = false;
    public boolean _justReleasedA = false;
    public boolean _justPressedA;

    public void onStart() {};
    public void onStop() {};
    public void onPause() {};
    public void onResume() {};
}
