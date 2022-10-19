package de.tost.jvisualizer.ui;

import de.tost.jvisualizer.gl.app.EventListener;
import de.tost.jvisualizer.gl.io.ButtonAction;
import de.tost.jvisualizer.gl.io.KeyModifier;
import de.tost.jvisualizer.gl.io.MouseButton;
import de.tost.jvisualizer.gl.texture.Texture;

public class CanvasPart implements EventListener {

    private int width, height, xPosition, yPosition;

    private CanvasAdapter renderAdapter;
    private boolean needsResize = false;

    public CanvasPart() {
        this.width = 400;
        this.height = 400;
        this.xPosition = 0;
        this.yPosition = 0;

    }

    public CanvasPart(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        this.xPosition = x;
        this.yPosition = y;
    }

    public void setRenderAdapter(CanvasAdapter newRenderAdapter) {
        if (this.renderAdapter != null) {
            renderAdapter.destroy();
        }
        this.renderAdapter = newRenderAdapter;
        if (newRenderAdapter == null) {
            return;
        }
        renderAdapter.init(width, height);
    }

    public void update() {
        if (renderAdapter == null) {
            needsResize = false;
            return;
        }
        if (needsResize) {
            needsResize = false;
            renderAdapter.updateSize(width, height);
        }
        renderAdapter.update();
    }

    public void render() {
        if (renderAdapter == null) {
            return;
        }
        renderAdapter.render();
    }

    public void setWidth(int newWidth) {
        this.width = newWidth;
        if (renderAdapter != null) {
            needsResize = true;
        }
    }

    public void setHeight(int newHeight) {
        this.height = newHeight;
        if (renderAdapter != null) {
            needsResize = true;
        }
    }

    public void setSize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        if (renderAdapter != null) {
            needsResize = true;
        }
    }

    public void setPosition(int newXPosition, int newYPosition) {
        this.xPosition = newXPosition;
        this.yPosition = newYPosition;
    }

    public void setXPosition(int newXPosition) {
        this.xPosition = newXPosition;
    }

    public void setYPosition(int newYPosition) {
        this.yPosition = newYPosition;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    @Override
    public void onScrollEvent(double xOffset, double yOffset, boolean local) {
        if (renderAdapter != null) {
            renderAdapter.onScrollEvent(xOffset, yOffset, local);
        }
    }

    @Override
    public void onKeyEvent(int glfwKeyCode, int scanCode, ButtonAction action, KeyModifier modifier, boolean local) {
        if (renderAdapter != null) {
            renderAdapter.onKeyEvent(glfwKeyCode, scanCode, action, modifier, local);
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButton button, ButtonAction action, KeyModifier modifier, boolean local) {
        if (renderAdapter != null) {
            renderAdapter.onMouseButtonEvent(button, action, modifier, local);
        }
    }

    @Override
    public void onCursorPositionEvent(double xPos, double yPos, boolean local) {
        if (renderAdapter != null) {
            renderAdapter.onCursorPositionEvent(xPos, yPos, local);
        }
    }

    @Override
    public void onFileDropEvent(String[] filePaths, boolean local) {
        if (renderAdapter != null) {
            renderAdapter.onFileDropEvent(filePaths, local);
        }
    }

    @Override
    public void onWindowPositionEvent(int xPos, int yPos) {
        if (renderAdapter != null) {
            renderAdapter.onWindowPositionEvent(xPos, yPos);
        }
    }

    public Texture getRenderedTexture() {
        if (renderAdapter == null) {
            return null;
        }
        return renderAdapter.getTexture();
    }

    public void destroy(){
        setRenderAdapter(null);
    }
}
