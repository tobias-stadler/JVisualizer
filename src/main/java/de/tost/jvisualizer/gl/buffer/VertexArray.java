package de.tost.jvisualizer.gl.buffer;

import de.tost.jvisualizer.gl.GLObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class VertexArray extends GLObject {

    private static int currentlyBoundID = 0;

    private int attribNumber = 0;

    public VertexArray() {
        create();
    }

    public void addBuffer(VertexBuffer buffer) {

        if (buffer == null || buffer.getMemoryLayout() == null) {
            throw new IllegalArgumentException("A VertexBuffer can't be null and needs a VertexBufferLayout to be added to a VertexArray!");
        }
        bind();
        buffer.bind();

        VertexBufferLayout layout = buffer.getMemoryLayout();

        int stride = layout.calculateStride();
        int offset = 0;

        for (int i = 0; i < layout.getElements().size(); i++) {
            VertexBufferLayout.Element element = layout.getElements().get(i);
            GL20.glVertexAttribPointer(attribNumber, element.count, element.type.type, element.normalized, stride, offset);
            GL20.glEnableVertexAttribArray(attribNumber);
            GL33.glVertexAttribDivisor(attribNumber, element.perInstance);
            attribNumber++;
            offset += element.getSize();
        }
        unbind();
    }

    public void addBuffer(ElementBuffer buffer) {
        bind();
        buffer.bind();
        unbind();
    }

    @Override
    public void create() {
        if (objectID != 0) throw new IllegalStateException("Can't create GLObject twice!");
        objectID = GL30.glGenVertexArrays();
        attribNumber = 0;
    }

    public void resetAttribPointers(){
        attribNumber = 0;
    }

    @Override
    public void destroy() {
        if (currentlyBoundID == objectID)
            unbind();
        GL30.glDeleteVertexArrays(objectID);
        objectID = 0;
    }

    @Override
    public void bind() {
        currentlyBoundID = objectID;
        GL30.glBindVertexArray(objectID);
    }

    @Override
    public void unbind() {
        currentlyBoundID = 0;
        GL30.glBindVertexArray(0);
    }

    public void checkBinding() {
        if (currentlyBoundID == objectID) {
            return;
        } else {
            bind();
            System.out.println("VertexArray wasn't bound! It was bound automatically! <- Bad Practice");
        }
    }
}
