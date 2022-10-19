package de.tost.jvisualizer.gl.app;

import de.tost.jvisualizer.gl.io.ButtonAction;
import de.tost.jvisualizer.gl.io.KeyModifier;
import de.tost.jvisualizer.gl.io.MouseButton;

public abstract class VariableEventListener implements EventListener {

    public void onScrollEvent(double xOffset, double yOffset, boolean local){}
    public void onKeyEvent(int glfwKeyCode, int scanCode, ButtonAction action, KeyModifier modifier, boolean local){}
    public void onMouseButtonEvent(MouseButton button, ButtonAction action, KeyModifier modifier, boolean local){}
    public void onCursorPositionEvent(double xPos, double yPos, boolean local){}
    public void onFileDropEvent(String[] filePaths, boolean local){}
    public void onWindowPositionEvent(int xPos, int yPos) {}
}
