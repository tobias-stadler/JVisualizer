package de.tost.jvisualizer.gl.buffer;

import de.tost.jvisualizer.gl.GLObject;
import org.lwjgl.opengl.GL15;

public class VertexBuffer extends GLObject {

    private static int currentlyBoundID = 0;

    private VertexBufferLayout layout;

    public VertexBuffer() {
        create();
    }

    public void putData(float[] data, BufferUsage usage) {
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage.getGLCode());
        unbind();
    }



    @Override
    public void create() {
        if (objectID != 0) throw new IllegalStateException("Can't create GLObject twice!");
        objectID = GL15.glGenBuffers();
    }

    @Override
    public void destroy() {
        if (currentlyBoundID == objectID)
            unbind();
        GL15.glDeleteBuffers(objectID);
        objectID = 0;
    }

    @Override
    public void bind() {
        currentlyBoundID = objectID;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, objectID);
    }

    @Override
    public void unbind() {
        currentlyBoundID = 0;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void checkBinding() {
        if (currentlyBoundID == objectID) {
        } else {
            bind();
            System.out.println("WARNING: VertexBuffer wasn't bound! It was bound automatically! <- Bad Practice");
        }
    }

    public void setMemoryLayout(VertexBufferLayout layout) {
        this.layout = layout;
    }

    public VertexBufferLayout getMemoryLayout() {
        return layout;
    }
}
