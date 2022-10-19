package de.tost.jvisualizer.ui;

public class RasterPart {
    private int x, y, width, height;
    private CanvasPart canvasPart;

    public RasterPart(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setCanvasPart(CanvasPart part){
        this.canvasPart = part;
    }

    public CanvasPart getCanvasPart(){
        return canvasPart;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setSize(int w, int h){
        this.width = w;
        this.height = h;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
