package de.tost.jvisualizer.data;

import de.tost.jvisualizer.gl.buffer.BufferUsage;
import de.tost.jvisualizer.gl.buffer.VertexBuffer;
import de.tost.jvisualizer.gl.buffer.VertexBufferLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DatasetFloat {

    private static final int DEFAULT_SLOTSIZE = (int) Math.pow(2, 15);

    private final List<float[]> slots;
    private final AtomicInteger size;
    private final int slotSize;

    private float calculatedMax, calculatedMin;

    public DatasetFloat(int slotSize) {
        size = new AtomicInteger(0);
        slots = new ArrayList<>();
        this.slotSize = slotSize;
    }

    public DatasetFloat() {
        this(DEFAULT_SLOTSIZE);
    }

    private void ensureSlotCount(int slotCount) {
        if (slotCount > slots.size()) {
            slots.add(new float[slotSize]);
            ensureSlotCount(slotCount);
        }
    }

    public void add(float element) {
        int index, slot, position;
        position = size.getAndIncrement();
        slot = position / slotSize;
        index = position % slotSize;
        ensureSlotCount(slot + 1);
        slots.get(slot)[index] = element;
    }

    public float get(int elementIndex) {

        if (elementIndex >= size.get()) {
            throw new IndexOutOfBoundsException("Index " + elementIndex + " was too high for size " + size.get());
        }

        int index, slot;
        slot = elementIndex / slotSize;
        index = elementIndex % slotSize;

        return slots.get(slot)[index];
    }

    public void calculateMinMax() {
        float highest = 0;
        float lowest = 0;

        for (int i = 0; i < getSize(); i++) {
            float val = get(i);
            if(i == 0){
                highest = val;
                lowest = val;
            } else {
                if(val > highest){
                    highest = val;
                }
                if(val < lowest){
                    lowest = val;
                }
            }
        }
        calculatedMax = highest;
        calculatedMin = lowest;
    }

    public float getCalculatedMin(){
        return calculatedMin;
    }

    public float getCalculatedMax(){
        return calculatedMax;
    }

    public float[] getArray(){
        float[] out = new float[getSize()];
        for(int i = 0; i < getSize(); i++){
            out[i] = get(i);
        }
        return out;
    }

    public float[] getArray(int firstIndex, int lastIndex) {

        if (firstIndex > lastIndex) {
            throw new IndexOutOfBoundsException("First index is bigger than last index!");
        }

        if (lastIndex >= size.get()) {
            throw new IndexOutOfBoundsException("Index " + lastIndex + " was too high for size " + size.get());
        }

        int outSize = lastIndex - firstIndex + 1;
        float[] out = new float[outSize];

        for (int i = 0; i < outSize; i++) {
            out[i] = get(firstIndex + i);
        }
        return out;
    }


    public void printAllStructured() {
        for (int i = 0; i < slots.size(); i++) {
            System.out.println("Slot " + i + ": " + Arrays.toString(slots.get(i)));
        }
    }

    public void printAll() {
        System.out.println(Arrays.toString(getArray(0, size.get() - 1)));
    }


    public void clear() {
        slots.clear();
        size.set(0);
    }

    public int getSize() {
        return size.get();
    }

    public int getSlotSize() {
        return slotSize;
    }

    public int getSlotCount() {
        return slots.size();
    }

    public void fillVertexBuffer(VertexBuffer buffer){
        float[] data = getArray();
        buffer.putData(data, BufferUsage.STATIC);
        VertexBufferLayout layout = new VertexBufferLayout();
        layout.add(VertexBufferLayout.Type.FLOAT, 1, false);
        buffer.setMemoryLayout(layout);
    }
}
