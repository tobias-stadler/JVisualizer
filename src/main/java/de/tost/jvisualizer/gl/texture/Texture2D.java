package de.tost.jvisualizer.gl.texture;


import org.lwjgl.opengl.GL11;


public class Texture2D extends Texture {

    public Texture2D() {
        super(GL11.GL_TEXTURE_2D);
    }

    public void setupStorage(int level, int width, int height, int internalFormatGL, int dataFormatGL, int typeGL, int[] data) {
        bind();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, internalFormatGL, width, height, 0, dataFormatGL, typeGL, data);
    }

    public void setupStorage(int width, int height, int internalFormatGL) {
        setupStorage(0, width, height, internalFormatGL, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);
    }

    public void storeImageSection(int level, int xOffset, int yOffset, int width, int height, int dataFormatGL, int typeGL, int[] data) {
        bind();
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, dataFormatGL, typeGL, data);
    }
}
