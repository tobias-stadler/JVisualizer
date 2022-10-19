package de.tost.jvisualizer.gl.math;

public class Quaternion {

    public float w, x, y, z;

    public Quaternion() {
        set(0, 0, 0, 0);
    }

    public Quaternion(float w, float x, float y, float z) {
        set(w, x, y, z);
    }

    public Quaternion(float angle, Vector3f axis){
        set(0, 0, 0 ,0);

    }

    public void rotate(float angle, Vector3f axis){

    }

    public void rotateX(){

    }

    public void rotateY(){

    }

    public void rotateZ(){

    }

    public float getAngle(){
        return 0;
    }

    public Vector3f getAxis(){
        return null;
    }

    public void set(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
