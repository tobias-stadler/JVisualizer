package de.tost.jvisualizer.data;

import de.tost.jvisualizer.gl.buffer.BufferUsage;
import de.tost.jvisualizer.gl.buffer.VertexBuffer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Flight {

    public enum Sample {
        LAUNCH_DETECT("lauchdetectsample"),
        APOGEE_DETECT("apogeedetectsample"),
        PARACHUTE_DEPLOY("parachutedeploysample");

        private String name;

        Sample(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Meta {
        LOCATION("location"),
        DATE("date"),
        ROCKET("rocket");

        private String name;

        Meta(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private DatasetLong timeData = new DatasetLong();
    private DatasetFloat rawAltitudeData = new DatasetFloat();
    private DatasetFloat filteredAltitudeData = new DatasetFloat();
    private Map<Integer, Float> temperatureList = new HashMap<>();

    private String[] metaData = new String[Meta.values().length];
    private int[] specialSamples = new int[Sample.values().length];

    public Flight() {

    }

    public void addSample(long time, float rawAlt, float filteredAlt) {
        timeData.add(time);
        rawAltitudeData.add(rawAlt);
        filteredAltitudeData.add(filteredAlt);
    }

    public void addTemperature(int sample, float temperature) {
        temperatureList.put(sample, temperature);
    }

    public void setSpecialSample(Sample type, int sample) {
        if (sample < 0) {
            throw new IllegalArgumentException("Sample values have to be >= 0");
        }
        specialSamples[type.ordinal()] = sample;
    }

    public void setMetaData(Meta type, String data) {
        metaData[type.ordinal()] = data;
    }

    public DatasetLong getTimeData() {
        return timeData;
    }

    public DatasetFloat getFilteredAltitudeData() {
        return filteredAltitudeData;
    }

    public DatasetFloat getRawAltitudeData() {
        return rawAltitudeData;
    }

    public Map<Integer, Float> getTemperatureList() {
        return temperatureList;
    }

    public Float getTemperature(int sample) {
        return temperatureList.getOrDefault(sample, null);
    }

    public Integer getSpecialSample(Sample type) {
        return specialSamples[type.ordinal()] < 0 ? null : specialSamples[type.ordinal()];
    }

    public String getMetaData(Meta type) {
        return metaData[type.ordinal()];
    }

    public void clear() {
        timeData.clear();
        rawAltitudeData.clear();
        filteredAltitudeData.clear();
        temperatureList.clear();
        for (int i = 0; i < specialSamples.length; i++) {
            specialSamples[i] = -1;
        }
        for (int i = 0; i < metaData.length; i++) {
            metaData[i] = null;
        }
    }

    public boolean hasSamples() {
        if (timeData.getSize() == 0 || rawAltitudeData.getSize() == 0 || filteredAltitudeData.getSize() == 0) {
            return false;
        }
        return true;
    }

    public boolean hasSpecialSamples() {
        for (int i = 0; i < specialSamples.length; i++) {
            if (specialSamples[i] < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean hasTemperature() {
        return !temperatureList.isEmpty();
    }

    public boolean isCorrect(){
        if(this.timeData.getSize() == this.rawAltitudeData.getSize() && this.timeData.getSize() == this.filteredAltitudeData.getSize()){
            return true;
        } else {
            return false;
        }
    }
}
