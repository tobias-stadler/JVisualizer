package de.tost.jvisualizer.gl;

public abstract class GLObject {

    protected int objectID = 0;

    public abstract void create();

    public abstract void destroy();

    public abstract void bind();

    public abstract void unbind();

    public int getID() {
        return objectID;
    }

    public boolean isValid() {
        return objectID != 0;
    }

}
