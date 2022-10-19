package de.tost.jvisualizer.gl.buffer;

import de.tost.jvisualizer.gl.GLObject;
import de.tost.jvisualizer.gl.texture.Texture2D;
import de.tost.jvisualizer.gl.texture.Texture2DMultiSample;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FrameBuffer extends GLObject {

    public enum Attachment {

        COLOR(GL30.GL_COLOR_ATTACHMENT0), DEPTH(GL30.GL_DEPTH_ATTACHMENT), STENCIL(GL30.GL_STENCIL_ATTACHMENT), DEPTH_STENCIL(GL30.GL_DEPTH_STENCIL_ATTACHMENT);

        private int glID;

        Attachment(int glID) {
            this.glID = glID;
        }

        public int getGLID() {
            return glID;
        }

    }

    private static int currentReadBufferID = 0;
    private static int currentDrawBufferID = 0;

    private int colorAttachment = 0;

    public FrameBuffer() {
        create();
    }

    public void attach(Attachment attachment, Texture2D texture) {
        attach(attachment, texture, 0);
    }

    public void attach(Attachment attachment, Texture2DMultiSample texture) {
        attach(attachment, texture, 0);
    }


    public int attach(Attachment attachment, Texture2D texture, int level) {
        bind();
        if (attachment == Attachment.COLOR) {
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, Attachment.COLOR.getGLID() + colorAttachment, GL11.GL_TEXTURE_2D, texture.getID(), level);
            return colorAttachment++;
        } else {
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment.getGLID(), GL11.GL_TEXTURE_2D, texture.getID(), level);
        }
        return -1;
    }

    public int attach(Attachment attachment, Texture2DMultiSample texture, int level) {
        bind();
        if (attachment == Attachment.COLOR) {
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, Attachment.COLOR.getGLID() + colorAttachment, GL32.GL_TEXTURE_2D_MULTISAMPLE, texture.getID(), 0);
            return colorAttachment++;
        } else {
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment.getGLID(), GL32.GL_TEXTURE_2D_MULTISAMPLE, texture.getID(), level);
        }
        return -1;
    }

    public int attach(Attachment attachment, RenderBuffer renderBuffer) {
        bind();
        if (attachment == Attachment.COLOR) {
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, Attachment.COLOR.getGLID() + colorAttachment, GL30.GL_RENDERBUFFER, renderBuffer.getID());
            return colorAttachment++;
        } else {
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment.getGLID(), GL30.GL_RENDERBUFFER, renderBuffer.getID());
        }
        return -1;
    }

    public void setDrawColorAttachments(int... attachments) {
        checkBinding();
        int[] intermediate = new int[attachments.length];
        int count = 0;
        for (int i = 0; i < attachments.length; i++) {
            if (attachments[i] >= 0 && attachments[i] < colorAttachment) {
                intermediate[count] = attachments[i];
                count++;
            } else {
                System.err.println("COLOR_ATTACHMENT" + attachments[i] + " is not a valid color attachment!");
            }
        }
        int[] buffs = new int[count];
        for (int i = 0; i < buffs.length; i++) {
            buffs[i] = intermediate[i];
        }
        GL20.glDrawBuffers(buffs);
    }

    @Override
    public void create() {
        colorAttachment = 0;
        objectID = GL30.glGenFramebuffers();
    }

    @Override
    public void destroy() {
        if (currentDrawBufferID == objectID)
            unbindDraw();
        if (currentReadBufferID == objectID)
            unbindRead();
        GL30.glDeleteFramebuffers(objectID);
        objectID = 0;
    }

    @Override
    public void bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, objectID);
        currentDrawBufferID = objectID;
        currentReadBufferID = objectID;
    }

    public void bindRead() {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, objectID);
        currentReadBufferID = objectID;
    }

    public void bindDraw() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, objectID);
        currentDrawBufferID = objectID;
    }

    @Override
    public void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        currentDrawBufferID = 0;
        currentReadBufferID = 0;
    }

    public void unbindRead() {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
        currentReadBufferID = 0;
    }

    public void unbindDraw() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        currentDrawBufferID = 0;
    }

    public void checkBinding() {
        if (currentReadBufferID == objectID && currentDrawBufferID == objectID) {
        } else {
            bind();
            System.out.println("FrameBuffer wasn't bound! It was bound automatically <- Bad Practice");
        }
    }

    public void checkBindingRead() {
        if (currentReadBufferID == objectID) {
        } else {
            bindRead();
            System.out.println("ReadFrameBuffer wasn't bound! It was bound automatically <- Bad Practice");
        }
    }

    public void checkBindingDraw() {
        if (currentDrawBufferID == objectID) {
        } else {
            bindDraw();
            System.out.println("DrawFrameBuffer wasn't bound! It was bound automatically <- Bad Practice");
        }
    }

    public boolean isComplete() {
        checkBinding();
        return (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE);
    }
}
