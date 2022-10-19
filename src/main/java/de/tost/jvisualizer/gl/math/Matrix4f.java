package de.tost.jvisualizer.gl.math;

public class Matrix4f {
    //Column-Major
    //1, 5, 9,13,   1,5,9,13
    //2, 6,10,14,   2,6,10,14
    //3, 7,11,15,   3,7,11,15
    //4, 8,12,16,   4,8,12,16
    public static final int SIZE = 4 * 4;

    public final float[] elements = new float[SIZE];

    public Matrix4f() {

    }

    public static Matrix4f identity() {
        Matrix4f result = new Matrix4f();
        result.fillZero();

        result.elements[0 + 0 * 4] = 1.0f;
        result.elements[1 + 1 * 4] = 1.0f;
        result.elements[2 + 2 * 4] = 1.0f;
        result.elements[3 + 3 * 4] = 1.0f;

        return result;
    }

    public static Matrix4f translate(Vector3f vec) {
        Matrix4f result = identity();
        result.elements[3 * 4 + 0] = vec.x;
        result.elements[3 * 4 + 1] = vec.y;
        result.elements[3 * 4 + 2] = vec.z;
        return result;
    }

    public static Matrix4f orthgraphicPixelsTopLeftCorner(int width, int height){
        return orthographic(0, width, height, 0, -100f, 100f);
    }

    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f result = identity();

        result.elements[0 * 4 + 0] = 2.0f / (right - left);
        result.elements[1 * 4 + 1] = 2.0f / (top - bottom);
        result.elements[2 * 4 + 2] = 2.0f / (near - far);

        result.elements[3 * 4 + 0] = (left + right) / (left - right);
        result.elements[3 * 4 + 1] = (bottom + top) / (bottom - top);
        result.elements[3 * 4 + 2] = (far + near) / (far - near);

        return result;
    }

    public static Matrix4f scale(Vector3f scale) {
        Matrix4f result = new Matrix4f();
        result.fillZero();

        result.elements[0 * 4 + 0] = scale.x;
        result.elements[1 * 4 + 1] = scale.y;
        result.elements[2 * 4 + 2] = scale.z;
        result.elements[3 * 4 + 3] = 1.0f;

        return result;
    }

    public static Matrix4f rotateZ(float angle) {
        Matrix4f result = identity();

        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        result.elements[0 + 0 * 4] = cos;
        result.elements[1 + 0 * 4] = sin;
        result.elements[0 + 1 * 4] = -sin;
        result.elements[1 + 1 * 4] = cos;
        //cos -sin 0 0
        //sin cos 0 0
        //0 0 1 0
        //0 0 0 1

        return result;
    }

    public void fillZero() {
        for (int i = 0; i < SIZE; i++) {
            elements[i] = 0.0f;
        }
    }

    public Vector3f transform(Vector3f vec) {
        Vector3f transformed = new Vector3f();

        transformed.x = vec.x * elements[0] + vec.y * elements[4] + vec.z * elements[8] + elements[12];
        transformed.y = vec.x * elements[1] + vec.y * elements[5] + vec.z * elements[9] + elements[13];
        transformed.z = vec.x * elements[2] + vec.y * elements[6] + vec.z * elements[10] + elements[14];

        return transformed;
    }

    public Matrix4f multiply(Matrix4f other) {
        Matrix4f result = new Matrix4f();

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                result.elements[x * 4 + y] = other.elements[y + 4 * 0] * this.elements[4 * x + 0]
                        + other.elements[y + 4 * 1] * this.elements[4 * x + 1]
                        + other.elements[y + 4 * 2] * this.elements[4 * x + 2]
                        + other.elements[y + 4 * 3] * this.elements[4 * x + 3];
            }
        }

        return result;
    }

    public void print() {
        System.out.println("4x4 Matrix:");
        for (int y = 0; y < 4; y++) {
            System.out.println("[" + elements[y + 0 * 4] + ", " + elements[y + 1 * 4] + ", " + elements[y + 2 * 4] + ", " + elements[y + 3 * 4] + "]");
        }
    }

    public void printOctave() {
        System.out.println("4x4 Matrix for Octave:");
        System.out.print("[");
        for (int y = 0; y < 4; y++) {
            System.out.print("" + elements[y + 0 * 4] + ", " + elements[y + 1 * 4] + ", " + elements[y + 2 * 4] + ", " + elements[y + 3 * 4] + "");
            if (y < 3) {
                System.out.print("; ");
            }
        }
        System.out.println("]");
    }
}
