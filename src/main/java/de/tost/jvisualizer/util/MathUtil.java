package de.tost.jvisualizer.util;

public class MathUtil {

    public static int findClosestInt(int[] vals, double target) {

        if (vals == null || vals.length == 0) {
            throw new IllegalArgumentException("Values can't be null");
        }

        int closest = 0;
        if (vals.length > 1) {
            for (int i = 1; i < vals.length; i++) {
                    double distanceToVal = absDistance(vals[i], target);
                    double distanceToOld = absDistance(vals[closest], target);

                    if(distanceToVal < distanceToOld){
                        closest = i;
                    }
            }
        }

        return vals[closest];
    }

    public static double absDistance(double val1, double val2){
        return Math.abs(val1 - val2);
    }

    public static boolean isPointInRectangle(int pX, int pY, int recX, int recY, int width, int height){
        int rBeginX = recX;
        int rBeginY = recY;
        int rEndX = recX+width;
        int rEndY = recY+height;
        if(pX < rEndX && pX > rBeginX && pY > rBeginY && pY < rEndY)
            return true;

        return false;
    }

}
