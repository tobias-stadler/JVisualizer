package de.tost.jvisualizer.io;

import de.tost.jvisualizer.gl.shader.Shader;

public class ShaderHelper {

    public static Shader createShaderFromShaderfolder(String name) {

        String vertexSource = FileUtils.loadTextFromClasspath("shaders/" + name + ".vs");
        String fragmentSource = FileUtils.loadTextFromClasspath("shaders/" + name + ".fs");
        String geometrySource = FileUtils.loadTextFromClasspath("shaders/" + name + ".gs");

        if (vertexSource == null || fragmentSource == null) {
            return null;
        }

        if (vertexSource.equals("") || fragmentSource.equals("")) {
            return null;
        }

        Shader shader;
        if (geometrySource == null) {
            shader = new Shader(vertexSource, fragmentSource);
        } else {
            shader = new Shader(vertexSource, fragmentSource, geometrySource);
        }

        if (!shader.build()) {
            return null;
        }

        return shader;
    }

}
