package de.tost.jvisualizer.ui;

import de.tost.jvisualizer.gl.math.Vector3f;
import de.tost.jvisualizer.gl.math.Vector4f;

public final class Color {

    public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);

    public final float R, G, B, A;

    public Color(float r, float g, float b, float a) {
        this.R = r;
        this.G = g;
        this.B = b;
        this.A = a;
    }

    public Vector4f toVec4() {
        return new Vector4f(R, G, B, A);
    }

    public Vector3f toVec3() {
        return new Vector3f(R, G, B);
    }
}
