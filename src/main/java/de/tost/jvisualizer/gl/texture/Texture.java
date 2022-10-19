package de.tost.jvisualizer.gl.texture;

import de.tost.jvisualizer.gl.GLObject;
import de.tost.jvisualizer.gl.buffer.Sampler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public abstract class Texture extends GLObject {

    private int textureTarget;

    public Texture(int textureTarget){
        this.textureTarget = textureTarget;
        create();
    }

    public void bindTextureUnit(int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
    }

    public void setUpscaleFilter(Sampler.Filter filter){
        bind();
        if(filter == Sampler.Filter.LINEAR || filter == Sampler.Filter.NEAREST){
            GL11.glTexParameteri(textureTarget, GL11.GL_TEXTURE_MAG_FILTER, filter.getGlID());
        } else {
            throw new IllegalArgumentException("Upscale filter has to be NEAREST or LINEAR!");
        }
    }

    public void setDownscaleFilter(Sampler.Filter filter){
        bind();
        GL11.glTexParameteri(textureTarget, GL11.GL_TEXTURE_MIN_FILTER, filter.getGlID());
    }

    public void setWrapModeX(Sampler.WrapMode wrapMode){
        bind();
        GL11.glTexParameteri(textureTarget, GL11.GL_TEXTURE_WRAP_S, wrapMode.getGlID());
    }
    public void setWrapModeY(Sampler.WrapMode wrapMode){
        bind();
        GL11.glTexParameteri(textureTarget, GL11.GL_TEXTURE_WRAP_T, wrapMode.getGlID());
    }

    public void generateMipmap(){
        bind();
        GL30.glGenerateMipmap(textureTarget);
    }

    @Override
    public void create() {
        objectID = GL11.glGenTextures();
    }

    @Override
    public void destroy() {
        GL11.glDeleteTextures(objectID);
        objectID = 0;
    }

    public void bind() {
        GL11.glBindTexture(textureTarget, objectID);
    }

    @Override
    public void unbind() {
        GL11.glBindTexture(textureTarget, 0);
    }

}
