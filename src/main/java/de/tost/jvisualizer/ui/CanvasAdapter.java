package de.tost.jvisualizer.ui;

import de.tost.jvisualizer.gl.app.EventListener;
import de.tost.jvisualizer.gl.app.VariableEventListener;
import de.tost.jvisualizer.gl.io.ButtonAction;
import de.tost.jvisualizer.gl.io.KeyModifier;
import de.tost.jvisualizer.gl.io.MouseButton;
import de.tost.jvisualizer.gl.texture.Texture;

public abstract class CanvasAdapter extends VariableEventListener {

    public abstract void init(int width, int height);
    public abstract void update();
    public abstract void render();
    public abstract void destroy();
    public abstract void updateSize(int newWidth, int newHeight);
    public abstract Texture getTexture();

    public String saveState(){ return null; }
    public void loadState(String state){}

}
