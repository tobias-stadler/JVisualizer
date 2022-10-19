package de.tost.jvisualizer.io;

import de.tost.jvisualizer.gl.buffer.Sampler;
import de.tost.jvisualizer.gl.texture.Texture;
import de.tost.jvisualizer.gl.texture.Texture2D;
import de.tost.jvisualizer.gl.texture.TextureHelper;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;

public class ImageHelper {

    public static Texture2D loadTextureFromTextureFolder(String name, int slot) {
        BufferedImage image;

        try {
            image = ImageIO.read(ImageHelper.class.getResourceAsStream("/textures/" + name));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Texture2D texture2D = TextureHelper.createFrom(image, 0, Sampler.Filter.NEAREST, Sampler.Filter.NEAREST, TextureHelper.Channel.RGBA);

        return texture2D;
    }

}
