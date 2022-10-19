package de.tost.jvisualizer.gl.render;

import de.tost.jvisualizer.gl.buffer.*;
import de.tost.jvisualizer.gl.math.Matrix4f;
import de.tost.jvisualizer.gl.shader.Shader;
import de.tost.jvisualizer.io.ShaderHelper;
import de.tost.jvisualizer.ui.Color;
import org.lwjgl.opengl.GL11;

public class QuadRenderer {

    private static final float[] QUAD_VBO = {1f, 1f, 0.0f, //top right
            1f, 0f, 0.0f, //bottom right
            0f, 1f, 0.0f, //top left
            0f, 0f, 0.0f,}; //bottom left
    private static final int[] QUAD_EBO = {0, 2, 3, 0, 3, 1};

    private static Shader shader;
    private VertexBuffer vbo;
    private ElementBuffer ebo;
    private VertexArray vao;

    public QuadRenderer() {
        if (shader == null) {
            shader = ShaderHelper.createShaderFromShaderfolder("quad");
        }
        if (shader == null) {
            throw new IllegalStateException("QuadRenderer shader couldn't be compiled!");
        }

        ebo = new ElementBuffer();
        ebo.putData(QUAD_EBO, BufferUsage.STATIC);

        vbo = new VertexBuffer();
        VertexBufferLayout layout = new VertexBufferLayout();
        layout.add(VertexBufferLayout.Type.FLOAT, 3, false);
        vbo.setMemoryLayout(layout);
        vbo.putData(QUAD_VBO, BufferUsage.STATIC);

        vao = new VertexArray();
        vao.addBuffer(vbo);
        vao.addBuffer(ebo);
    }

    public void drawRectangle(Matrix4f transform, Color color){
        vao.bind();
        shader.bind();
        shader.setMat4f("uTransformMat", transform);
        shader.setVec4f("uColor", color.toVec4());
        GL11.glDrawElements(GL11.GL_TRIANGLES, QUAD_EBO.length, GL11.GL_UNSIGNED_INT, 0);
    }
}
