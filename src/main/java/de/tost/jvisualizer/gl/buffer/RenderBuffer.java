package de.tost.jvisualizer.gl.buffer;

import de.tost.jvisualizer.gl.GLObject;
import org.lwjgl.opengl.GL30;

public class RenderBuffer extends GLObject {

    private static int currentlyBoundID = 0;

    public RenderBuffer() {
        create();
    }

    @Override
    public void create() {
        objectID = GL30.glGenRenderbuffers();
    }

    public void setupStorage(int width, int height, int internalFormatGL) {
        bind();
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, internalFormatGL, width, height);
        unbind();
    }

    public void setupStorage(int width, int height, int internalFormatGL, int samples) {
        bind();
        GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, internalFormatGL, width, height);
        unbind();
    }

    @Override
    public void destroy() {
        GL30.glDeleteRenderbuffers(objectID);
    }

    @Override
    public void bind() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, objectID);
    }

    @Override
    public void unbind() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
    }
}
