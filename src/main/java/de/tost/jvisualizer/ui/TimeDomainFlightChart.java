package de.tost.jvisualizer.ui;

import de.tost.jvisualizer.data.Flight;
import de.tost.jvisualizer.gl.buffer.FrameBuffer;
import de.tost.jvisualizer.gl.buffer.Sampler;
import de.tost.jvisualizer.gl.math.Matrix4f;
import de.tost.jvisualizer.gl.math.Vector3f;
import de.tost.jvisualizer.gl.render.LineRenderer;
import de.tost.jvisualizer.gl.shader.Shader;
import de.tost.jvisualizer.gl.texture.Texture2D;
import de.tost.jvisualizer.io.FlightFileHelper;
import de.tost.jvisualizer.io.ShaderHelper;
import de.tost.jvisualizer.util.MathUtil;
import de.tost.jvisualizer.util.StopWatch;
import org.lwjgl.opengl.GL11;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import static java.lang.Math.log10;
import static java.lang.Math.pow;

public class TimeDomainFlightChart extends CanvasAdapter {

    private int w, h;

    private static Color COLOR_GRID = new Color(0.2f, 0.2f, 0.2f, 1.0f),
            COLOR_COORD = new Color(0.5f, 0.5f, 0.5f, 1.0f),
            COLOR_BACKGROUND = new Color(0.16f, 0.16f, 0.16f, 1.0f),
            COLOR_FLIGHT = new Color(0.18f, 0.59f, 1.0f, 1.0f),
            COLOR_COMPARE = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    private Shader chartShader;

    private static final int[] lineCount = {2, 5, 10, 20, 50};

    private FrameBuffer frameBuffer;
    private Texture2D frameTexture;

    private Matrix4f shaderMat;
    private Matrix4f autoscaleMat;
    private Matrix4f scaleMat;
    private Matrix4f pixelMat;

    private Vector3f xAxisMargin;
    private Vector3f yAxisMargin;
    private Vector3f tickMarkOffsetXAxis;
    private Vector3f tickMarkOffsetYAxis;

    private Vector3f coordXLeft;
    private Vector3f coordXRight;
    private Vector3f coordYUp;
    private Vector3f coordYDown;

    private FlightGL flight;
    private FlightGL compareFlight;

    private LineRenderer gridLines;
    private LineRenderer coordinateLines;

    private StopWatch renderWatch = new StopWatch();

    private boolean needsRender;

    public TimeDomainFlightChart() {
    }

    @Override
    public void init(int width, int height) {
        frameTexture = new Texture2D();
        frameTexture.setWrapModeX(Sampler.WrapMode.CLAMP_BORDER);
        frameTexture.setWrapModeY(Sampler.WrapMode.CLAMP_BORDER);
        frameTexture.setUpscaleFilter(Sampler.Filter.LINEAR);
        frameTexture.setDownscaleFilter(Sampler.Filter.LINEAR);
        changeSize(width, height);
        frameBuffer = new FrameBuffer();
        frameBuffer.attach(FrameBuffer.Attachment.COLOR, frameTexture);
        if (!frameBuffer.isComplete()) {
            System.out.println("[TimeDomainFlightChart] Framebuffer is not complete!");
        }
        frameBuffer.unbind();
        chartShader = ShaderHelper.createShaderFromShaderfolder("timedomainflightchart");
        gridLines = new LineRenderer();
        coordinateLines = new LineRenderer();
        if (chartShader == null) {
            throw new IllegalStateException("TimeDomainFlightShader couldn't be compiled! We have problem, u know!");
        }
        needsRender = true;
    }

    @Override
    public void update() {

    }

    private void loadFlightFile(String path) {
        Flight f = new Flight();
        try {
            FlightFileHelper.parseLog(f, Paths.get(path));
            setFlight(new ColoredFlight(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void calculateTransformations() {
        pixelMat = Matrix4f.orthographic(-((float) w / 2.0f), ((float) w / 2.0f), -((float) h / 2.0f), ((float) h / 2.0f), 1.0f, -1.0f);

        xAxisMargin = pixelMat.transform(new Vector3f(20, 0, 0));
        yAxisMargin = pixelMat.transform(new Vector3f(0, 20, 0));
        tickMarkOffsetXAxis = pixelMat.transform(new Vector3f(0, 8, 0));
        tickMarkOffsetYAxis = pixelMat.transform(new Vector3f(8, 0, 0));
        Vector3f xMargin = xAxisMargin.add(new Vector3f(0.1f, 0f, 0f));
        Vector3f yMargin = yAxisMargin.add(new Vector3f(0f, 0.1f, 0f));
        if (flight != null) {
            autoscaleMat = Matrix4f.orthographic(flight.getXMin(), flight.getXMax(), flight.getYMin(), flight.getYMax(), 1.0f, -1.0f);
            scaleMat = Matrix4f.orthographic(-1.0f - xMargin.x, 1.0f + xMargin.x, -1.0f - yMargin.y, 1.0f + yMargin.y, 1.0f, -1.0f);
            shaderMat = autoscaleMat.multiply(scaleMat);

            coordXLeft = shaderMat.transform(new Vector3f(flight.getXMin(), 0, 0)).subtract(xAxisMargin);
            coordXRight = shaderMat.transform(new Vector3f(flight.getXMax(), 0, 0)).add(xAxisMargin);
            coordYUp = shaderMat.transform(new Vector3f(0, flight.getYMax(), 0)).add(yAxisMargin);
            coordYDown = shaderMat.transform(new Vector3f(0, flight.getYMin(), 0)).subtract(yAxisMargin);
        } else {
            autoscaleMat = Matrix4f.orthographic(0, 10, 0, 10, 1.0f, -1.0f);
            scaleMat = Matrix4f.orthographic(-1.0f - xMargin.x, 1.0f + xMargin.x, -1.0f - yMargin.y, 1.0f + yMargin.y, 1.0f, -1.0f);
            shaderMat = autoscaleMat.multiply(scaleMat);

            coordXLeft = shaderMat.transform(new Vector3f(0, 0, 0)).subtract(xAxisMargin);
            coordXRight = shaderMat.transform(new Vector3f(10, 0, 0)).add(xAxisMargin);
            coordYUp = shaderMat.transform(new Vector3f(0, 10, 0)).add(yAxisMargin);
            coordYDown = shaderMat.transform(new Vector3f(0, 0, 0)).subtract(yAxisMargin);
        }
    }

    @Override
    public void render() {
        if (!needsRender) {
            return;
        }
        needsRender = false;
        frameBuffer.bind();
        GL11.glViewport(0, 0, w, h);

        renderWatch.start();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        calculateTransformations();
        renderGrid();

        if (flight != null) {
            GL11.glLineWidth(2);
            chartShader.bind();
            chartShader.setVec4f("traceColor", COLOR_FLIGHT.toVec4());
            chartShader.setMat4f("transformMat", shaderMat);
            flight.getAltitudeVA().bind();
            GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, flight.getColoredFlight().getFlight().getTimeData().getSize());
            flight.getAltitudeVA().unbind();
        }

        frameBuffer.unbind();

        renderWatch.stop();
        System.out.println("[TimeDomainFlightChart] Render Took: " + renderWatch.getElapsed() + "ms");
    }

    @Override
    public void destroy() {
        System.out.println("[TimeDomainFlightChart] Destroyed");
        if (flight != null) {
            flight.destroy();
        }
        if (compareFlight != null) {
            compareFlight.destroy();
        }
        frameTexture.destroy();
        frameBuffer.destroy();
        coordinateLines.destroy();
        coordinateLines.destroy();
        chartShader.destroy();
    }

    @Override
    public void updateSize(int newWidth, int newHeight) {
        changeSize(newWidth, newHeight);
    }


    private void renderGrid() {
        gridLines.setColor(COLOR_GRID);
        coordinateLines.setColor(COLOR_COORD);
        gridLines.deleteLines();
        coordinateLines.deleteLines();
        gridLines.setLineWidth(1);
        coordinateLines.setLineWidth(2);

        //Axes
        coordinateLines.addLine(coordXLeft, coordXRight);
        coordinateLines.addLine(coordYDown, coordYUp);
        //Arrows
        coordinateLines.addLine(coordXRight, coordXRight.add(pixelMat.transform(new Vector3f(-10, 10, 0))));
        coordinateLines.addLine(coordXRight, coordXRight.add(pixelMat.transform(new Vector3f(-10, -10, 0))));

        coordinateLines.addLine(coordYUp, coordYUp.add(pixelMat.transform(new Vector3f(10, -10, 0))));
        coordinateLines.addLine(coordYUp, coordYUp.add(pixelMat.transform(new Vector3f(-10, -10, 0))));

        if (flight != null) {
            double xStep = pow(10, (int) (log10((flight.getXMax() + flight.getXMin())))) / MathUtil.findClosestInt(lineCount, (double) w / 300.0d);
            double yStep = pow(10, (int) (log10(flight.getYMax() + flight.getYMin()))) / MathUtil.findClosestInt(lineCount, (double) h / 300.0d);

            //System.out.println("[TimeDomainFlightChart] ms/Division: " + xStep);
            //System.out.println("[TimeDomainFlightChart] m/Division: " + yStep);

            if (xStep != 0) {
                for (float x = 0; x < flight.getXMax(); x += xStep) {
                    if (x == 0) continue;
                    gridLines.addLine(shaderMat.transform(new Vector3f(x, flight.getYMin(), 0.0f)).subtract(yAxisMargin), shaderMat.transform(new Vector3f(x, flight.getYMax(), 0.0f)).add(yAxisMargin));
                    Vector3f centerCoord = shaderMat.transform(new Vector3f(x, 0, 0));
                    coordinateLines.addLine(centerCoord.add(tickMarkOffsetXAxis), centerCoord.subtract(tickMarkOffsetXAxis));
                }

                for (float x = 0; x > flight.getXMin(); x -= xStep) {
                    if (x == 0) continue;
                    gridLines.addLine(shaderMat.transform(new Vector3f(x, flight.getYMin(), 0.0f)).subtract(yAxisMargin), shaderMat.transform(new Vector3f(x, flight.getYMax(), 0.0f)).add(yAxisMargin));
                    Vector3f centerCoord = shaderMat.transform(new Vector3f(x, 0, 0));
                    coordinateLines.addLine(centerCoord.add(tickMarkOffsetXAxis), centerCoord.subtract(tickMarkOffsetXAxis));
                }
            }

            if (yStep != 0) {
                for (float y = 0; y < flight.getYMax(); y += yStep) {
                    if (y == 0) continue;
                    gridLines.addLine(shaderMat.transform(new Vector3f(flight.getXMin(), y, 0.0f)).subtract(xAxisMargin), shaderMat.transform(new Vector3f(flight.getXMax(), y, 0.0f)).add(xAxisMargin));
                    Vector3f centerCoord = shaderMat.transform(new Vector3f(0, y, 0));
                    coordinateLines.addLine(centerCoord.add(tickMarkOffsetYAxis), centerCoord.subtract(tickMarkOffsetYAxis));
                }

                for (float y = 0; y > flight.getYMin(); y -= yStep) {
                    if (y == 0) continue;
                    Vector3f centerCoord = shaderMat.transform(new Vector3f(0, y, 0));
                    gridLines.addLine(shaderMat.transform(new Vector3f(flight.getXMin(), y, 0.0f)).subtract(xAxisMargin), shaderMat.transform(new Vector3f(flight.getXMax(), y, 0.0f)).add(xAxisMargin));
                    coordinateLines.addLine(centerCoord.add(tickMarkOffsetYAxis), centerCoord.subtract(tickMarkOffsetYAxis));
                }
            }
        }

        gridLines.submitLinesToGL();
        coordinateLines.submitLinesToGL();
        gridLines.render();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        coordinateLines.render();
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }


    private void changeSize(int newWidth, int newHeight) {
        System.out.println("[TimeDomainFlightChart] New Framebuffer Texture: width: " + newWidth + " height: " + newHeight);
        frameTexture.setupStorage(newWidth, newHeight, GL11.GL_RGBA);
        this.w = newWidth;
        this.h = newHeight;
        this.needsRender = true;
    }

    public void setFlight(ColoredFlight newFlight) {
        if (flight != null) {
            flight.destroy();
            flight = null;
        }
        if (newFlight == null) {
            return;
        }
        flight = new FlightGL(newFlight);
        flight.prepareAltitude();
        flight.prepareFilteredAltitude();
        needsRender = true;
    }

    public void setCompareFlight(ColoredFlight newFlight) {
        if (compareFlight != null) {
            compareFlight.destroy();
            compareFlight = null;
        }
        if (newFlight == null) {
            return;
        }
        compareFlight = new FlightGL(newFlight);
        compareFlight.prepareAltitude();
        compareFlight.prepareFilteredAltitude();
        needsRender = true;
    }

    public Texture2D getTexture() {
        return frameTexture;
    }

    @Override
    public void onFileDropEvent(String[] filePaths, boolean local) {
        if(!local) return;
        if(filePaths.length == 1){
            loadFlightFile(filePaths[0]);
        }
    }

    public static String getName() {
        return "FlightChart(TimeDomain)";
    }
}