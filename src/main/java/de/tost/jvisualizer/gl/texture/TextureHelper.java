package de.tost.jvisualizer.gl.texture;

import de.tost.jvisualizer.gl.buffer.Sampler;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGBA;

//GL1
//GL2
//GL3
//GL4

public class TextureHelper {

    public enum Channel {
        RGB, RGBA, GRAY
    }

    public static Texture2D createFrom(BufferedImage image, int textureSlot, Sampler.Filter upscaleFilter, Sampler.Filter downscaleFilter, Channel channel) {
        if (image == null) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        int dataFormat = GL_RGBA, internalFormat = 0;

        switch (channel) {
            case RGB:
                internalFormat = GL11.GL_RGB;
                break;
            case GRAY:
                internalFormat = GL11.GL_RED;
                break;
            case RGBA:
                internalFormat = GL11.GL_RGBA;
                break;
        }

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        int[] data = new int[width * height];

        for (int i = 0; i < width * height; i++) {
            int col;

            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            if (channel == Channel.RGB) {
                a = 0xFF;
            } else if (channel == Channel.RGBA) {
            } else if (channel == Channel.GRAY) {
                a = 0xFF;
            }

            col = a << 24 | b << 16 | g << 8 | r;

            int line = i / width;
            int column = i % width;

            //flip because opengl uses different texture coordinatesystem
            data[column + (height - line - 1) * width] = col;
        }

        Texture2D texture2D = new Texture2D();
        texture2D.bindTextureUnit(textureSlot);
        texture2D.bind();
        texture2D.setUpscaleFilter(upscaleFilter);
        texture2D.setDownscaleFilter(downscaleFilter);
        texture2D.setupStorage(0, width, height, internalFormat, dataFormat, GL11.GL_UNSIGNED_BYTE, data);
        return texture2D;
    }

}
