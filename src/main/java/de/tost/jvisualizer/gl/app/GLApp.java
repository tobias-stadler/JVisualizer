package de.tost.jvisualizer.gl.app;

import de.tost.jvisualizer.gl.io.ButtonAction;
import de.tost.jvisualizer.gl.io.KeyModifier;
import de.tost.jvisualizer.gl.io.MouseButton;

public abstract class GLApp extends VariableEventListener {

    private String title;
    protected int width, height;
    private boolean resizeable;

    public GLApp(String title, int width, int height, boolean resizeable){
        this.title = title;
        this.width = width;
        this.height = height;
        this.resizeable = resizeable;
    }

    public abstract void init();
    public abstract void glInit();
    public abstract void glUpdate();
    public abstract void glRender();
    public abstract void cleanUp();

    public void onFramebufferSizeEvent(int width, int height){}

    public String getTitle(){
        return title;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public boolean isResizeable(){
        return resizeable;
    }

}
