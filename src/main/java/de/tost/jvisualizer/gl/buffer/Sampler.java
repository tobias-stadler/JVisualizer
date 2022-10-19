package de.tost.jvisualizer.gl.buffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

public class Sampler {

    public enum WrapMode {
        CLAMP_EDGE(GL_CLAMP_TO_EDGE),
        CLAMP_BORDER(GL_CLAMP_TO_BORDER),
        REPEAT(GL_REPEAT),
        REPEAT_MIRROR(GL_MIRRORED_REPEAT);

        private int glID;

        WrapMode(int glID) {
            this.glID = glID;
        }

        public int getGlID() {
            return glID;
        }

    }

    public enum Filter {

        NEAREST(GL_NEAREST),
        NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST),
        NEAREST_MIPMAP_LINEAR(GL_NEAREST_MIPMAP_LINEAR),
        LINEAR(GL_LINEAR),
        LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST),
        LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR);

        private int glID;

        Filter(int glID) {
            this.glID = glID;
        }

        public int getGlID() {
            return glID;
        }
    }

}
