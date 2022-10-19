package de.tost.jvisualizer.gl.render;

import de.tost.jvisualizer.gl.buffer.BufferUsage;
import de.tost.jvisualizer.gl.buffer.VertexArray;
import de.tost.jvisualizer.gl.buffer.VertexBuffer;
import de.tost.jvisualizer.gl.buffer.VertexBufferLayout;
import de.tost.jvisualizer.gl.math.Matrix4f;
import de.tost.jvisualizer.gl.math.Vector3f;
import de.tost.jvisualizer.gl.shader.Shader;
import de.tost.jvisualizer.io.ShaderHelper;
import de.tost.jvisualizer.ui.Color;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

//GL1
//GL2
//GL3
//GL4

public class LineRenderer {
    private static Shader shader;

    private VertexBuffer vertexBuffer;
    private int vertexBufferSampleCount;
    private VertexArray vertexArray;

    private ArrayList<Vector3f> positions;
    private Matrix4f transformationMatrix = Matrix4f.identity();

    private float lineWidth = 1f;

    private Color color = Color.BLACK;
    private boolean renderable = false;

    public LineRenderer() {
        positions = new ArrayList<>();
        if (shader == null) {
            shader = ShaderHelper.createShaderFromShaderfolder("simpleline");
        }
        if (shader == null) {
            throw new IllegalStateException("LineRenderer shader couldn't be compiled!");
        }
    }

    public void render() {
        if (renderable) {
            shader.bind();
            shader.setVec4f("uLineColor", color.toVec4());
            shader.setMat4f("uTransformMatrix", transformationMatrix);
            vertexArray.bind();
            glLineWidth(lineWidth);
            glDrawArrays(GL_LINES, 0, vertexBufferSampleCount);
        }
    }

    public void deleteLines() {
        positions.clear();
    }

    public void addLine(float startX, float startY, float endX, float endY) {
        positions.add(new Vector3f(startX, startY, 0.0f));
        positions.add(new Vector3f(endX, endY, 0.0f));
    }

    public void addLine(Vector3f start, Vector3f end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Linevectors can't be null");
        }
        positions.add(start);
        positions.add(end);
    }

    public void submitLinesToGL() {
        renderable = false;
        if (vertexBuffer == null) {
            vertexBuffer = new VertexBuffer();
            VertexBufferLayout layout = new VertexBufferLayout();
            layout.add(VertexBufferLayout.Type.FLOAT, 3, false);
            vertexBuffer.setMemoryLayout(layout);
        }
        if (vertexArray == null) {
            vertexArray = new VertexArray();
            vertexArray.addBuffer(vertexBuffer);
        }
        if (positions.size() == 0) {
            return;
        }
        float[] floatPositions = new float[positions.size() * 3];
        for (int i = 0; i < positions.size(); i++) {
            Vector3f vec = positions.get(i);
            floatPositions[0 + i * 3] = vec.x;
            floatPositions[1 + i * 3] = vec.y;
            floatPositions[2 + i * 3] = vec.z;
        }

        vertexBufferSampleCount = floatPositions.length;
        vertexBuffer.putData(floatPositions, BufferUsage.STATIC);
        renderable = true;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTransformationMatrix(Matrix4f matrix) {
        if (matrix == null) {
            this.transformationMatrix = Matrix4f.identity();
        } else {
            this.transformationMatrix = matrix;
        }
    }

    public void destroy() {
        vertexBuffer.destroy();
        vertexArray.destroy();
        renderable = false;
    }

    public void setLineWidth(float width) {
        this.lineWidth = width;
    }

}
