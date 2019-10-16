package com.viktorvilmusenaho.asteroidsgl.utils;

import android.util.Log;

public abstract class Utils {

    private final static java.util.Random RNG = new java.util.Random();

    public static float wrap(float val, final float min, final float max) {
        if (val < min) {
            val = max;
        } else if (val > max) {
            val = min;
        }
        return val;
    }

    public static float clamp(float val, final float min, final float max) {
        if (val > max) {
            val = max;
        } else if (val < min) {
            val = min;
        }
        return val;
    }

    public static boolean coinFlip() {
        return RNG.nextFloat() > 0.5;
    }

    public static float nextFloat() {
        return RNG.nextFloat();
    }

    public static int nextInt(final int max) {
        return RNG.nextInt(max);
    }

    public static int between(final int min, final int max) {
        return RNG.nextInt(max - min) + min;
    }

    public static float between(final float min, final float max) {
        return min + RNG.nextFloat() * (max - min);
    }

    public static void expect(final boolean condition, final String tag) {
        Utils.expect(condition, tag, "Expectation was broken.");
    }
    public static void expect(final boolean condition, final String tag, final String message) {
        if(!condition) {
            Log.e(tag, message);
        }
    }
    public static void require(final boolean condition) {
        Utils.require(condition, "Assertion failed!");
    }
    public static void require(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static final double TO_DEG = 180.0/Math.PI;
    public static final double TO_RAD = Math.PI/180.0;
}
