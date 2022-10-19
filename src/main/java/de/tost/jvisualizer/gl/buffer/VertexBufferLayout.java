package de.tost.jvisualizer.gl.buffer;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class VertexBufferLayout {

    public class Element {
        final Type type;
        final int count;
        final boolean normalized;
        final int perInstance;

        public Element(Type type, int count, boolean normalized, int perInstance) {
            this.type = type;
            this.count = count;
            this.normalized = normalized;
            this.perInstance = perInstance;
        }

        public int getSize() {
            return type.typeSize * count;
        }
    }

    public enum Type {

        FLOAT(GL11.GL_FLOAT, 4);

        final int type;
        final int typeSize;

        Type(int type, int typeSize) {
            this.type = type;
            this.typeSize = typeSize;
        }
    }

    private ArrayList<Element> elements = new ArrayList<>();

    public void add(Type type, int count, boolean normalized, int perInstance) {
        elements.add(new Element(type, count, normalized, perInstance));
    }

    public void add(Type type, int count, boolean normalized){
        add(type, count, normalized, 0);
    }

    public List<Element> getElements() {
        return elements;
    }

    public int calculateStride() {
        int stride = 0;
        for (Element el : elements) {
            stride += el.getSize();
        }
        return stride;
    }
}
