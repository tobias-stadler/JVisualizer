package de.tost.jvisualizer.gl.buffer;

import org.lwjgl.opengl.GL15;

public enum BufferUsage {

    STATIC(GL15.GL_STATIC_DRAW), DYNAMIC(GL15.GL_DYNAMIC_DRAW), STREAM(GL15.GL_STREAM_DRAW);

    int num;

    BufferUsage(int glNum) {
        this.num = glNum;
    }

    public int getGLCode() {
        return num;
    }


}
