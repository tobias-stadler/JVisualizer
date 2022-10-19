package de.tost.jvisualizer.gl.shader;


import de.tost.jvisualizer.gl.GLObject;
import de.tost.jvisualizer.gl.math.*;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

//GL1
//GL2
//GL3
//GL4

public class Shader extends GLObject {

    private static int currentlyBoundID = 0;

    private String vertexSrc;
    private String fragmentSrc;
    private String geometrySrc;

    public Shader() {
    }

    public Shader(String vertexSrc, String fragmentSrc, String geometrySrc) {
        this.vertexSrc = vertexSrc;
        this.fragmentSrc = fragmentSrc;
        this.geometrySrc = geometrySrc;
    }

    public Shader(String vertexSrc, String fragmentSrc) {
        this.vertexSrc = vertexSrc;
        this.fragmentSrc = fragmentSrc;
    }


    public void setVertexSrc(String code) {
        this.vertexSrc = code;
    }

    public void setFragmentSrc(String code) {
        this.fragmentSrc = code;
    }

    public void setGeometrySrc(String code) {
        this.geometrySrc = code;
    }

    public boolean build() {
        if (objectID != 0) {
            System.err.println("Shaders can only be compiled once! You have to create another object");
            return false;
        }
        if (vertexSrc == null || fragmentSrc == null) {
            return false;
        }

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        if (vertexShader == 0) return false;
        glShaderSource(vertexShader, vertexSrc);
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("VertexShader compilation failed:");
            System.err.println(glGetShaderInfoLog(vertexShader));
            return false;
        }

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        if (fragmentShader == 0) return false;
        glShaderSource(fragmentShader, fragmentSrc);
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("FragmentShader compilation failed:");
            System.err.println(glGetShaderInfoLog(fragmentShader));
            return false;
        }

        int geometryShader = 0;
        if (geometrySrc != null) {
            geometryShader = glCreateShader(GL_GEOMETRY_SHADER);
            if (geometryShader == 0) return false;
            glShaderSource(geometryShader, geometrySrc);
            glCompileShader(geometryShader);
            if (glGetShaderi(geometryShader, GL_COMPILE_STATUS) == GL_FALSE) {
                System.err.println("GeometryShader compilation failed:");
                System.err.println(glGetShaderInfoLog(geometryShader));
                return false;
            }
        }

        int shaderProgram = glCreateProgram();
        if (shaderProgram == 0) return false;

        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        if (geometryShader != 0) {
            glAttachShader(shaderProgram, geometryShader);
        }
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("ShaderProgram linking failed:");
            System.err.println(glGetProgramInfoLog(shaderProgram));
            return false;
        }

        this.objectID = shaderProgram;

        glDeleteShader(fragmentShader);
        glDeleteShader(vertexShader);
        if (geometryShader != 0) {
            glDeleteShader(geometryShader);
        }
        return true;
    }

    public int getUniformID(String name) {
        int result = glGetUniformLocation(objectID, name);
        if (result == -1) {
            System.err.println("Couldn't get UniformLocation of '" + name + "'");
        }
        return result;
    }

    public void setInteger(String name, int val) {
        checkBinding();
        glUniform1i(getUniformID(name), val);
    }

    public void setFloat(String name, float val) {
        checkBinding();
        glUniform1f(getUniformID(name), val);
    }

    public void setVec2f(String name, Vector2f vec2) {
        checkBinding();
        glUniform2f(getUniformID(name), vec2.x, vec2.y);
    }

    public void setVec2f(String name, float x, float y) {
        checkBinding();
        glUniform2f(getUniformID(name), x, y);
    }

    public void setVec3f(String name, Vector3f vec3) {
        checkBinding();
        glUniform3f(getUniformID(name), vec3.x, vec3.y, vec3.z);
    }

    public void setVec3f(String name, float x, float y, float z) {
        checkBinding();
        glUniform3f(getUniformID(name), x, y, z);
    }

    public void setVec4f(String name, Vector4f vec4) {
        checkBinding();
        glUniform4f(getUniformID(name), vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void setVec4f(String name, float x, float y, float z, float w) {
        checkBinding();
        glUniform4f(getUniformID(name), x, y, z, w);
    }

    public void setMat3f(String name, Matrix3f mat3) {
        checkBinding();
        glUniformMatrix3fv(getUniformID(name), false, mat3.elements);
    }

    public void setMat4f(String name, Matrix4f mat4) {
        checkBinding();
        glUniformMatrix4fv(getUniformID(name), false, mat4.elements);
    }

    public void bind() {
        currentlyBoundID = objectID;
        glUseProgram(objectID);
    }

    public void unbind() {
        currentlyBoundID = 0;
        glUseProgram(0);
    }

    public void checkBinding() {
        if (currentlyBoundID == objectID) {
            return;
        } else {
            bind();
            System.out.println("WARNING: Shader wasn't bound! It was automatically bound! <- Bad Practice");
        }
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("A Shader can't be allocated! You have to use the build() method");
    }

    @Override
    public void destroy() {
        unbind();
        glDeleteProgram(objectID);
        objectID = 0;
    }
}
