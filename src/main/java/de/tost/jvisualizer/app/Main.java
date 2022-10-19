package de.tost.jvisualizer.app;

import de.tost.jvisualizer.gl.app.GLWindow;
import de.tost.jvisualizer.ui.AddWidget;
import de.tost.jvisualizer.ui.TimeDomainFlightChart;

public class Main {

    public static void main(String[] args) {
        GLWindow window = new GLWindow(new FlightViewer());
        window.start();
    }
}
