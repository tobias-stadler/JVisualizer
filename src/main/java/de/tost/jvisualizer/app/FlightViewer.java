package de.tost.jvisualizer.app;

import de.tost.jvisualizer.data.Flight;
import de.tost.jvisualizer.gl.app.GLApp;
import de.tost.jvisualizer.gl.io.ButtonAction;
import de.tost.jvisualizer.gl.io.KeyModifier;
import de.tost.jvisualizer.gl.io.MouseButton;
import de.tost.jvisualizer.io.FlightFileHelper;
import de.tost.jvisualizer.ui.CanvasPart;
import de.tost.jvisualizer.ui.ColoredFlight;
import de.tost.jvisualizer.ui.GLInteractiveCanvas;
import de.tost.jvisualizer.ui.TimeDomainFlightChart;
import org.lwjgl.opengl.GL11;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class FlightViewer extends GLApp {

    boolean firstResize = true;

    private int windowPosX, windowPosY;

    public FlightViewer() {
        super("FlightViewer", 1200, 500, true);
    }

    private ColoredFlight flight;

    @Override
    public void init() {


    }

    private GLInteractiveCanvas canvas;

    @Override
    public void glInit() {
        GL11.glEnable(GL11.GL_BLEND);
        canvas = new GLInteractiveCanvas(width, height, 10, 10 );
        canvas.onWindowPositionEvent(windowPosX, windowPosY);
        canvas.addWidgetType(TimeDomainFlightChart.class);
    }

    @Override
    public void glUpdate() {
        canvas.update();
    }


    @Override
    public void glRender() {
        canvas.render();
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public void onFramebufferSizeEvent(int w, int h) {
        this.width = w;
        this.height = h;
        if (firstResize) {
            firstResize = false;
        } else {
            canvas.setSize(w, h);
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButton button, ButtonAction action, KeyModifier modifier, boolean local) {
        canvas.onMouseButtonEvent(button, action, modifier, local);
    }

    @Override
    public void onKeyEvent(int glfwKeyCode, int scanCode, ButtonAction action, KeyModifier modifier, boolean local) {
        canvas.onKeyEvent(glfwKeyCode, scanCode, action, modifier, local);
    }

    @Override
    public void onCursorPositionEvent(double xPos, double yPos, boolean local) {
        canvas.onCursorPositionEvent(xPos, yPos, local);
    }

    @Override
    public void onFileDropEvent(String[] filePaths, boolean local) {
        canvas.onFileDropEvent(filePaths, local);
    }

    @Override
    public void onScrollEvent(double xOffset, double yOffset, boolean local) {
        canvas.onScrollEvent(xOffset, yOffset, local);
    }

    @Override
    public void onWindowPositionEvent(int xPos, int yPos) {
        windowPosX = xPos;
        windowPosY = yPos;
        if(canvas != null){
            canvas.onWindowPositionEvent(xPos, yPos);
        }
    }
}
