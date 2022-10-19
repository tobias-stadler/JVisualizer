package de.tost.jvisualizer.ui;

import de.tost.jvisualizer.data.Flight;

import java.util.HashMap;
import java.util.Map;

public class ColoredFlight {

    private static final Color DEFAULT_COLOR = Color.BLACK;

    private final Flight flight;

    private Map<Integer, Color> colors = new HashMap<>();

    public ColoredFlight(Flight flight) {
        if (flight == null)
            throw new IllegalArgumentException("Flight can't be null!");
        this.flight = flight;
    }

    public void addColor(int sample, Color color) {
        colors.put(sample, color);
    }

    public Color calcColorInterpolated(int sample) {

        int nearestSample = -1;

        for (Map.Entry<Integer, Color> colorEntry : colors.entrySet()) {
            if (colorEntry.getKey() < sample && colorEntry.getKey() > nearestSample) {
                nearestSample = colorEntry.getKey();
            }
        }

        if (nearestSample < 0) {
            return DEFAULT_COLOR;
        } else {
            return colors.getOrDefault(nearestSample, DEFAULT_COLOR);
        }
    }

    public Color getColor(int sample) {
        return colors.getOrDefault(sample, null);
    }

    public Color getColorOrDefault(int sample) {
        return colors.getOrDefault(sample, DEFAULT_COLOR);
    }

    public Flight getFlight(){
        return flight;
    }

}
