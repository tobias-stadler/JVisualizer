package de.tost.jvisualizer.ui;

import de.tost.jvisualizer.gl.buffer.VertexArray;
import de.tost.jvisualizer.gl.buffer.VertexBuffer;

public class FlightGL {


    private final ColoredFlight flight;

    private VertexBuffer timeVB;
    private VertexBuffer altitudeVB;
    private VertexBuffer filteredAltitudeVB;

    private VertexArray altitudeVA;
    private VertexArray filteredAltitudeVA;

    public FlightGL(ColoredFlight flight){
        this.flight = flight;
    }

    public void prepareAltitude(){
        prepareTime();
        if(altitudeVB != null || altitudeVA != null)
            return;
        altitudeVB = new VertexBuffer();
        flight.getFlight().getRawAltitudeData().calculateMinMax();
        flight.getFlight().getRawAltitudeData().fillVertexBuffer(altitudeVB);

        altitudeVA = new VertexArray();
        altitudeVA.addBuffer(timeVB);
        altitudeVA.addBuffer(altitudeVB);
    }

    public void prepareFilteredAltitude(){
        prepareTime();
        if(filteredAltitudeVB != null || filteredAltitudeVA != null)
            return;
        filteredAltitudeVB = new VertexBuffer();
        flight.getFlight().getFilteredAltitudeData().calculateMinMax();
        flight.getFlight().getFilteredAltitudeData().fillVertexBuffer(filteredAltitudeVB);

        filteredAltitudeVA = new VertexArray();
        filteredAltitudeVA.addBuffer(timeVB);
        filteredAltitudeVA.addBuffer(filteredAltitudeVB);
    }

    public VertexArray getFilteredAltitudeVA(){
        return filteredAltitudeVA;
    }

    public VertexArray getAltitudeVA() {
        return altitudeVA;
    }

    private void prepareTime(){
        if(timeVB != null)
            return;

        timeVB = new VertexBuffer();
        flight.getFlight().getTimeData().calculateMinMax();
        flight.getFlight().getTimeData().fillVertexBuffer(timeVB, 1);
    }

    public void destroy(){
        if(timeVB != null){
            timeVB.destroy();
            timeVB = null;
        }
        if(altitudeVB != null){
            altitudeVB.destroy();
            altitudeVB = null;
        }
        if(filteredAltitudeVB != null){
            filteredAltitudeVB.destroy();
            filteredAltitudeVB = null;
        }
        if(filteredAltitudeVA != null){
            filteredAltitudeVA.destroy();
            filteredAltitudeVA = null;
        }
        if(altitudeVA != null){
            altitudeVA.destroy();
            altitudeVA = null;
        }
    }

    public ColoredFlight getColoredFlight() {
        return flight;
    }

    public float getXMax(){
            return getColoredFlight().getFlight().getTimeData().getCalculatedMax();
    }

    public float getYMax(){
        float filteredMax = getColoredFlight().getFlight().getFilteredAltitudeData().getCalculatedMax();
        float rawMax = getColoredFlight().getFlight().getRawAltitudeData().getCalculatedMax();

        if(filteredMax > rawMax){
            return filteredMax;
        } else {
            return rawMax;
        }
    }

    public float getXMin(){
        return getColoredFlight().getFlight().getTimeData().getCalculatedMin();
    }

    public float getYMin(){

        float filteredMin = getColoredFlight().getFlight().getFilteredAltitudeData().getCalculatedMin();
        float rawMin = getColoredFlight().getFlight().getRawAltitudeData().getCalculatedMin();

        if(filteredMin < rawMin){
            return filteredMin;
        } else {
            return rawMin;
        }

    }
}
