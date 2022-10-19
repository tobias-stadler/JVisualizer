package de.tost.jvisualizer.gl.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;


public class Texture2DMultiSample extends Texture {

    public Texture2DMultiSample() {
        super(GL32.GL_TEXTURE_2D_MULTISAMPLE);
    }

    public void setupStorage(int width, int height, int samples, int internalFormatGL, boolean fixedSampleLocations) {
        bind();
        GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samples, internalFormatGL, width, height, fixedSampleLocations);
    }

    public void setupStorage(int width, int height, int samples, int internalFormatGL){
        setupStorage(width, height, samples, internalFormatGL, true);
    }
}
