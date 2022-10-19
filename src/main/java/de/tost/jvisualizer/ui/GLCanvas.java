package de.tost.jvisualizer.ui;

import de.tost.jvisualizer.gl.app.EventListener;
import de.tost.jvisualizer.gl.buffer.*;
import de.tost.jvisualizer.gl.io.ButtonAction;
import de.tost.jvisualizer.gl.io.KeyModifier;
import de.tost.jvisualizer.gl.io.MouseButton;
import de.tost.jvisualizer.gl.math.Matrix4f;
import de.tost.jvisualizer.gl.math.Vector3f;
import de.tost.jvisualizer.gl.shader.Shader;
import de.tost.jvisualizer.io.ShaderHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GLCanvas implements EventListener {

    public interface CanvasListener {
        void preRender();

        void postRender();
    }

    private static final float[] TEXTURE_VBO = {1f, 1f, 0.0f, 1.0f, 0.0f, //top right
            1f, 0f, 0.0f, 1.0f, 1.0f, //bottom right
            0f, 1f, 0.0f, 0.0f, 0.0f, //top left
            0f, 0f, 0.0f, 0.0f, 1.0f}; //bottom left
    private static final int[] TEXTURE_EBO = {0, 2, 3, 0, 3, 1};

    private Color backgroundColor;

    private VertexBuffer vbo;
    private ElementBuffer ebo;
    private VertexArray vao;
    private Shader shader;

    private CanvasListener listener;

    private boolean needsResize;

    private Matrix4f projectionMat;

    private ArrayList<CanvasPart> canvasParts = new ArrayList<>();

    private int width, height;
    private int requestedWidth, requestedHeight;

    private double cursorX = 0, cursorY = 0;

    public GLCanvas(int width, int height) {
        this.requestedWidth = width;
        this.requestedHeight = height;
        needsResize = true;
        initGL();
    }

    public void add(CanvasPart part) {
        this.canvasParts.add(part);
    }

    public void removeAll() {
        canvasParts.clear();
    }

    private void initGL() {
        vbo = new VertexBuffer();
        vbo.putData(TEXTURE_VBO, BufferUsage.STATIC);
        VertexBufferLayout layout = new VertexBufferLayout();
        layout.add(VertexBufferLayout.Type.FLOAT, 3, false);
        layout.add(VertexBufferLayout.Type.FLOAT, 2, false);
        vbo.setMemoryLayout(layout);

        ebo = new ElementBuffer();
        ebo.putData(TEXTURE_EBO, BufferUsage.STATIC);

        vao = new VertexArray();
        vao.addBuffer(vbo);
        vao.addBuffer(ebo);

        shader = ShaderHelper.createShaderFromShaderfolder("canvas");
        if (shader == null) {
            throw new IllegalStateException("Shader compilation failed!");
        }
        setBackgroundColor(new Color(0.16f, 0.16f, 0.16f, 1f));
    }

    public void update() {
        if (needsResize) {
            needsResize = false;
            this.width = requestedWidth;
            this.height = requestedHeight;
            calculateTransformations();
            System.out.println("[GLCanvas] Width: " + width + " Height: " + height);
        }
        for (CanvasPart canvasPart : canvasParts) {
            canvasPart.update();
        }
    }

    public void render() {
        GL11.glViewport(0, 0, width, height);
        GL11.glClearColor(backgroundColor.R, backgroundColor.G, backgroundColor.B, backgroundColor.A);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        if (listener != null) {
            listener.preRender();
        }
        GL11.glViewport(0, 0, width, height);
        vao.bind();
        shader.bind();
        shader.setMat4f("uProjection", projectionMat);
        for (CanvasPart canvasPart : canvasParts) {
            canvasPart.render();
            canvasPart.getRenderedTexture().bindTextureUnit(0);
            canvasPart.getRenderedTexture().bind();
            GL11.glViewport(0, 0, width, height);
            shader.bind();
            vao.bind();
            shader.setMat4f("uModel", Matrix4f.scale(new Vector3f(canvasPart.getWidth(), canvasPart.getHeight(), 1.0f)).multiply(Matrix4f.translate(new Vector3f(canvasPart.getXPosition(), canvasPart.getYPosition(), 0.0f))));
            shader.setInteger("uTexture", 0);
            GL11.glDrawElements(GL11.GL_TRIANGLES, TEXTURE_EBO.length, GL11.GL_UNSIGNED_INT, 0);
        }
        if (listener != null) {
            listener.postRender();
        }
    }

    public void setSize(int newWidth, int newHeight) {
        this.requestedWidth = newWidth;
        this.requestedHeight = newHeight;
        needsResize = true;
    }

    public void setBackgroundColor(Color col) {
        this.backgroundColor = col;
    }

    public void clear() {
        canvasParts.clear();
    }

    private void calculateTransformations() {
        projectionMat = Matrix4f.orthgraphicPixelsTopLeftCorner(width, height);
    }

    public void setCanvasListener(CanvasListener listener){
        this.listener = listener;
    }


    @Override
    public void onScrollEvent(double xOffset, double yOffset, boolean local) {
        for(CanvasPart part : canvasParts){
            part.onScrollEvent(xOffset, yOffset, false);
        }
        for(CanvasPart part : canvasParts){
            if(isHovered(part))
                part.onScrollEvent(xOffset, yOffset, true);
        }
    }

    @Override
    public void onKeyEvent(int glfwKeyCode, int scanCode, ButtonAction action, KeyModifier modifier, boolean local) {
        for(CanvasPart part : canvasParts){
            part.onKeyEvent(glfwKeyCode, scanCode, action, modifier, false);
        }
        for(CanvasPart part : canvasParts){
            if(isHovered(part))
                part.onKeyEvent(glfwKeyCode, scanCode, action, modifier, true);
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButton button, ButtonAction action, KeyModifier modifier, boolean local) {
        for(CanvasPart part : canvasParts){
            part.onMouseButtonEvent(button, action, modifier, false);
        }
        for(CanvasPart part : canvasParts){
            if(isHovered(part)){
                part.onMouseButtonEvent(button, action, modifier, true);
            }
        }
        System.out.println(button.toString().toLowerCase() + " " + action.toString().toLowerCase() + " at " + cursorX + ", " + cursorY);
    }

    @Override
    public void onCursorPositionEvent(double xPos, double yPos, boolean local) {
        cursorX = xPos;
        cursorY = yPos;

        for(CanvasPart part : canvasParts){
            part.onCursorPositionEvent(xPos, yPos, false);
        }
        for(CanvasPart part : canvasParts){
            if(isHovered(part)){
                part.onCursorPositionEvent(xPos-part.getWidth(), yPos-part.getHeight(), true);
            }
        }
    }

    @Override
    public void onFileDropEvent(String[] filePaths, boolean local) {
        for(CanvasPart part : canvasParts){
            part.onFileDropEvent(filePaths, false);
        }
        for(CanvasPart part : canvasParts){
            if(isHovered(part))
                part.onFileDropEvent(filePaths, true);
        }
    }

    @Override
    public void onWindowPositionEvent(int xPos, int yPos) {
        for(CanvasPart part : canvasParts){
            part.onWindowPositionEvent(xPos, yPos);
        }
    }

    public boolean isHovered(CanvasPart part){

        int partBeginX = part.getXPosition();
        int partBeginY = part.getYPosition();
        int partEndX = part.getXPosition() + part.getWidth();
        int partEndY = part.getYPosition() + part.getHeight();


        if(cursorX < partEndX && cursorX > partBeginX && cursorY > partBeginY && cursorY < partEndY)
            return true;
        return false;
    }

    public Matrix4f getProjectionMatrix(){
        return projectionMat;
    }
}
