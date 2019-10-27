package com.viktorvilmusenaho.asteroidsgl.entities;

import android.opengl.GLES20;

import com.viktorvilmusenaho.asteroidsgl.GL.Mesh;
import com.viktorvilmusenaho.asteroidsgl.utils.Utils;

public class Border extends GLEntity {
    public Border(final float x, final float y, final float worldWidth, final float worldHeight) {
        super();
        _x = x;
        _y = y;
        _width = worldWidth + 1.0f;
        _height = worldHeight + 1.0f;
        // shortening the variable names to keep the vertex array readable
        final float w = worldWidth;
        final float h = worldHeight;

        // do the mesh
        _mesh = new Mesh(Mesh.generateLinePolygon(4, 10.0), GLES20.GL_LINES);
        _mesh.rotateZ(45* Utils.TO_RAD);
        _mesh.setWidthHeight(_width, _height); //will automatically normalize the mesh!
    }
}
