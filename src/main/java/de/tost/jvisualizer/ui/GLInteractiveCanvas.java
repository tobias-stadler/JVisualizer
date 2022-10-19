package de.tost.jvisualizer.ui;

import de.tost.jvisualizer.gl.app.EventListener;
import de.tost.jvisualizer.gl.io.ButtonAction;
import de.tost.jvisualizer.gl.io.KeyModifier;
import de.tost.jvisualizer.gl.io.MouseButton;
import de.tost.jvisualizer.gl.math.Matrix4f;
import de.tost.jvisualizer.gl.math.Vector3f;
import de.tost.jvisualizer.gl.render.LineRenderer;
import de.tost.jvisualizer.gl.render.QuadRenderer;
import de.tost.jvisualizer.util.MathUtil;
import de.tost.jvisualizer.util.Pair;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

public class GLInteractiveCanvas implements EventListener, AddWidget.AddWidgetListener, GLCanvas.CanvasListener {

    private static final Color COLOR_ADD_VALID = new Color(0.7f, 0.7f, 0.7f, 1.0f), COLOR_ADD_INVALID = new Color(0.9f, 0.2f, 0.2f, 1.0f),
            COLOR_GRID_PASSIVE = new Color(0.3f, 0.3f, 0.3f, 1.0f), COLOR_GRID_ACTIVE = new Color(1f, 1f, 1f, 1.0f);

    private GLCanvas canvas;

    private int rasterWidth, rasterHeight;

    private boolean needsResize = false;
    private boolean needsLayout = false;
    private boolean needsGridRender = false;

    private int w, h;

    private double cursorX, cursorY;
    private int cursorXRaster, cursorYRaster;
    private double startX, startY;
    private double endX, endY;
    private int startXRaster, startYRaster;
    private int endXRaster, endYRaster;
    private int windowPosX, windowPosY;
    private boolean addValid = true;
    private boolean pressed = false;
    private boolean addingPart = false;
    private boolean waitForAdd = false;
    private AddWidget widgetWindow = new AddWidget();
    private ArrayList<Pair<RasterPart, Class<? extends CanvasAdapter>>> partsToAdd = new ArrayList<>();
    private LineRenderer crossRenderer;
    private Matrix4f gridTransform;

    private boolean drawCross = false;
    private long cursorMovedLast = System.currentTimeMillis();

    private RasterPart hoveredPart;

    private QuadRenderer quadRenderer;
    private LineRenderer lineRenderer;

    private ArrayList<RasterPart> widgets = new ArrayList<>();

    public GLInteractiveCanvas(int w, int h, int rasterW, int rasterH) {
        this.w = w;
        this.h = h;
        this.rasterHeight = rasterH;
        this.rasterWidth = rasterW;
        needsGridRender = true;

        quadRenderer = new QuadRenderer();
        lineRenderer = new LineRenderer();
        crossRenderer = new LineRenderer();
        canvas = new GLCanvas(w, h);
        canvas.setCanvasListener(this);
        widgetWindow.setListener(this);
        calculateTransformations();
    }

    public void update() {
        if (needsResize) {
            calculateTransformations();
            needsGridRender = true;
            canvas.setSize(w, h);
        }

        if (partsToAdd.size() > 0) {
            for (Pair<RasterPart, Class<? extends CanvasAdapter>> pair : partsToAdd) {
                try {
                    Constructor<? extends CanvasAdapter> constructor = pair.getV2().getConstructor();
                    CanvasAdapter newAdapter = constructor.newInstance();
                    CanvasPart newPart = new CanvasPart();
                    pair.getV1().setCanvasPart(newPart);
                    layoutRasterPart(pair.getV1());
                    newPart.setRenderAdapter(newAdapter);
                    widgets.add(pair.getV1());
                    canvas.add(pair.getV1().getCanvasPart());
                } catch (Exception e) {
                    System.err.println("Couldn't instantiate " + pair.getV2().getSimpleName());
                }
            }
            partsToAdd.clear();
        }

        if (needsLayout || needsResize) {
            layoutCanvas();
        }
        needsResize = false;
        needsLayout = false;

        if (addingPart) {
            addValid = isAreaFree(startXRaster, startYRaster, cursorXRaster, cursorYRaster);
        }

        hoveredPart = null;
        for(RasterPart rpart : widgets){
            CanvasPart part = rpart.getCanvasPart();
            if(canvas.isHovered(part)){
                hoveredPart = rpart;
                break;
            }
        }

        if(System.currentTimeMillis()-cursorMovedLast >= 1000){
            drawCross = false;
        }

        canvas.update();
    }

    public void render() {
        canvas.render();
        needsGridRender = false;
    }

    private void layoutCanvas() {
        canvas.clear();
        System.out.println("Layouting Canvas");
        for (RasterPart part : widgets) {
            layoutRasterPart(part);
            canvas.add(part.getCanvasPart());
        }
    }

    private void layoutRasterPart(RasterPart part) {
        CanvasPart cPart = part.getCanvasPart();
        int xPos = (part.getX() * w) / rasterWidth;
        int yPos = (part.getY() * h) / rasterHeight;
        int width = (part.getWidth() * w) / rasterWidth;
        int height = (part.getHeight() * h) / rasterHeight;
        cPart.setPosition(xPos, yPos);
        cPart.setSize(width, height);
    }

    private void calculateTransformations() {
        gridTransform = Matrix4f.orthographic(0, rasterWidth, rasterHeight, 0, 1.0f, -1.0f);
    }

    private boolean isAreaFree(int x1, int y1, int x2, int y2) {
        int beginX = Math.min(x1, x2);
        int beginY = Math.min(y1, y2);
        int endX = Math.max(x1, x2);
        int endY = Math.max(y1, y2);

        for (RasterPart part : widgets) {
            if (part.getWidth() < 1 || part.getHeight() < 1) {
                continue;
            }
            int partBeginX = part.getX();
            int partBeginY = part.getY();
            int partEndX = partBeginX + part.getWidth() - 1;
            int partEndY = partBeginY + part.getHeight() - 1;

            if (beginX <= partEndX && endX >= partBeginX &&
                    beginY <= partEndY && endY >= partBeginY) {
                return false;
            }
        }
        return true;
    }

    public void setRasterSize(int width, int height) {
        this.rasterWidth = width;
        this.rasterHeight = height;
        needsResize = true;
    }

    public void setSize(int width, int height) {
        this.w = width;
        this.h = height;
        needsResize = true;
    }

    public void setRasterWidth(int width) {
        this.rasterWidth = width;
        needsResize = true;
    }

    public void setRasterHeight(int height) {
        this.rasterHeight = height;
        needsResize = true;
    }

    public void setWidth(int width) {
        this.w = width;
        needsResize = true;
    }

    public void setHeight(int height) {
        this.h = height;
        needsResize = true;
    }

    public void addWidgetType(Class<? extends CanvasAdapter> clazz) {
        widgetWindow.addWidgetType(clazz);
    }

    @Override
    public void onScrollEvent(double xOffset, double yOffset,boolean local) {
        canvas.onScrollEvent(xOffset, yOffset, local);
    }

    @Override
    public void onKeyEvent(int glfwKeyCode, int scanCode, ButtonAction action, KeyModifier modifier, boolean local) {
        canvas.onKeyEvent(glfwKeyCode, scanCode, action, modifier, local);
    }

    @Override
    public void onMouseButtonEvent(MouseButton button, ButtonAction action, KeyModifier modifier, boolean local) {
        if (waitForAdd) return;

        boolean passOn = true;

        if (button == MouseButton.LEFT) {
            if (action == ButtonAction.PRESSED) {
                pressed = true;
                startX = cursorX;
                startY = cursorY;
                startXRaster = cursorXRaster;
                startYRaster = cursorYRaster;

                if(hoveredPart == null) {
                    addingPart = true;
                    passOn = false;
                }
                if(drawCross && hoveredPart != null){
                    CanvasPart part = hoveredPart.getCanvasPart();
                    if(MathUtil.isPointInRectangle((int)endX, (int)endY, part.getXPosition()+part.getWidth()-20, part.getYPosition(), 20, 20)){
                        passOn = false;
                    }
                }
            } else if (action == ButtonAction.RELEASED) {
                if (pressed) {
                    endX = cursorX;
                    endY = cursorY;
                    endXRaster = cursorXRaster;
                    endYRaster = cursorYRaster;

                    if(drawCross && hoveredPart != null){
                        CanvasPart part = hoveredPart.getCanvasPart();
                        if(MathUtil.isPointInRectangle((int)endX, (int)endY, part.getXPosition()+part.getWidth()-20, part.getYPosition(), 20, 20)){
                            passOn = false;
                            hoveredPart.getCanvasPart().destroy();
                            hoveredPart.setCanvasPart(null);
                            widgets.remove(hoveredPart);
                            needsLayout = true;
                        }
                    }
                }
                if(addingPart && isAreaFree(startXRaster, startYRaster, cursorXRaster, cursorYRaster)) {
                    waitForAdd = true;
                    widgetWindow.chooseWidget(windowPosX + (int) endX - 100, windowPosY + (int) endY - 50);
                }
                if(addingPart){
                    passOn = false;
                    addingPart = false;
                }
                pressed = false;
            }
        }

        if(passOn && !addingPart){
            canvas.onMouseButtonEvent(button, action, modifier, local);
        }
    }

    private int calcRasterX(double x) {
        return (int) ((x * rasterWidth) / w);
    }

    private int calcRasterY(double y) {
        return (int) ((y * rasterHeight) / h);
    }

    @Override
    public void onCursorPositionEvent(double xPos, double yPos, boolean local) {
        if(xPos >= w || yPos >= h || xPos <= 0 || yPos <= 0) return;

        canvas.onCursorPositionEvent(xPos, yPos, local);
        cursorX = xPos;
        cursorY = yPos;
        cursorXRaster = calcRasterX(xPos);
        cursorYRaster = calcRasterY(yPos);

        cursorMovedLast = System.currentTimeMillis();
        drawCross = true;
    }

    @Override
    public void onFileDropEvent(String[] filePaths, boolean local) {
        canvas.onFileDropEvent(filePaths, local);

        System.out.println("Dropped: " + Arrays.toString(filePaths));
    }

    @Override
    public void  onWindowPositionEvent(int xPos, int yPos) {
        canvas.onWindowPositionEvent(xPos, yPos);
        windowPosX = xPos;
        windowPosY = yPos;
    }

    @Override
    public void widgetAdded(Class<? extends CanvasAdapter> adapterClazz) {
        waitForAdd = false;
        if (adapterClazz != null && addValid) {
            System.out.println("Added " + adapterClazz.getSimpleName());
            int beginX = Math.min(startXRaster, endXRaster);
            int beginY = Math.min(startYRaster, endYRaster);
            int width = Math.max(startXRaster, endXRaster) - beginX + 1;
            int height = Math.max(startYRaster, endYRaster) - beginY + 1;
            System.out.println("W: " + width + ", H: " + height);
            RasterPart newRasterPart = new RasterPart(beginX, beginY, width, height);
            partsToAdd.add(new Pair<>(newRasterPart, adapterClazz));
        }
    }

    @Override
    public void preRender() {
        if (needsGridRender) {
            lineRenderer.deleteLines();
            lineRenderer.setTransformationMatrix(gridTransform);

            for (int x = 0; x < rasterWidth; x++) {
                lineRenderer.addLine(x, 0, x, rasterHeight);
            }

            for (int y = 0; y < rasterHeight; y++) {
                lineRenderer.addLine(0, y, rasterWidth, y);
            }

            lineRenderer.submitLinesToGL();
        }

        if (!addingPart) {
            lineRenderer.setColor(COLOR_GRID_PASSIVE);
            lineRenderer.render();
        }
    }

    @Override
    public void postRender() {
        if (addingPart) {
            int beginX = Math.min(startXRaster, cursorXRaster);
            int beginY = Math.min(startYRaster, cursorYRaster);
            int width = Math.max(startXRaster, cursorXRaster) - beginX + 1;
            int height = Math.max(startYRaster, cursorYRaster) - beginY + 1;
            Matrix4f transformMat = Matrix4f.scale(new Vector3f(width, height, 1.0f)).multiply(Matrix4f.translate(new Vector3f(beginX, beginY, 0.0f))).multiply(gridTransform);
            if (addValid) {
                quadRenderer.drawRectangle(transformMat, COLOR_ADD_VALID);
            } else {
                quadRenderer.drawRectangle(transformMat, COLOR_ADD_INVALID);
            }
            lineRenderer.setColor(COLOR_GRID_ACTIVE);
            lineRenderer.render();
        } else {
            if(hoveredPart != null && drawCross){
                CanvasPart part = hoveredPart.getCanvasPart();
                quadRenderer.drawRectangle(Matrix4f.scale(new Vector3f(20, 20, 1.0f)).multiply(Matrix4f.translate(new Vector3f(part.getXPosition()+part.getWidth()-20, part.getYPosition(), 0.0f))).multiply(canvas.getProjectionMatrix()),COLOR_GRID_PASSIVE);

                crossRenderer.deleteLines();
                crossRenderer.addLine(part.getXPosition()+part.getWidth()-20, part.getYPosition(), part.getXPosition()+part.getWidth(), part.getYPosition()+20);
                crossRenderer.addLine(part.getXPosition()+part.getWidth(), part.getYPosition(), part.getXPosition()+part.getWidth()-20, part.getYPosition()+20);
                crossRenderer.setLineWidth(2f);
                crossRenderer.submitLinesToGL();

                crossRenderer.setTransformationMatrix(canvas.getProjectionMatrix());
                crossRenderer.setColor(COLOR_GRID_ACTIVE);
                crossRenderer.render();
            }
        }
    }
}
