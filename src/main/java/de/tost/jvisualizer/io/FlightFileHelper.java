package de.tost.jvisualizer.io;

import de.tost.jvisualizer.data.Flight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FlightFileHelper {

    private static final String META_INDICATOR = "META: ";


    public static boolean parseLog(Flight flight, Path flightFile) throws FileNotFoundException {
        if (!Files.exists(flightFile)) {
            throw new FileNotFoundException();
        }
        try (BufferedReader reader = Files.newBufferedReader(flightFile)) {

            List<String> parsed = new ArrayList<>();

            flight.clear();

            boolean isMeta = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(META_INDICATOR)) {
                    isMeta = true;
                    line = line.substring(META_INDICATOR.length());
                } else {
                    isMeta = false;
                }

                if (isMeta) {
                    String metaString;
                    for (int i = 0; i < Flight.Sample.values().length; i++) {
                        String metaStart = Flight.Sample.values()[i].getName() + ": ";
                        if (line.startsWith(metaStart)) {
                            metaString = line.substring(metaStart.length());
                            try {
                                int metaNum = Integer.parseInt(metaString);
                                flight.setSpecialSample(Flight.Sample.values()[i], metaNum);
                            } catch (NumberFormatException e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    }
                    for (int i = 0; i < Flight.Meta.values().length; i++) {
                        String metaStart = Flight.Meta.values()[i].getName() + ": ";
                        if (line.startsWith(metaStart)) {
                            metaString = line.substring(metaStart.length());
                            flight.setMetaData(Flight.Meta.values()[i], metaString);
                        }
                    }
                } else {
                    parsed.clear();
                    CSVHelper.parse(parsed, line);
                    if (parsed.size() >= 3) {
                        try {
                            long time = Long.parseLong(parsed.get(0));
                            float rawAlt = Float.parseFloat(parsed.get(1));
                            float filteredAlt = Float.parseFloat(parsed.get(2));

                            flight.addSample(time, rawAlt, filteredAlt);

                            if (parsed.size() >= 4) {
                                for (int i = 3; i < parsed.size(); i++) {
                                    String parsedString = parsed.get(i);
                                    int sampleNum = flight.getTimeData().getSize() - 1;
                                    if (sampleNum >= 0) {
                                        if (parsedString.equalsIgnoreCase("L")) {
                                            flight.setSpecialSample(Flight.Sample.LAUNCH_DETECT, sampleNum);
                                        } else if (parsedString.equalsIgnoreCase("A")) {
                                            flight.setSpecialSample(Flight.Sample.APOGEE_DETECT, sampleNum);
                                        } else if (parsedString.equalsIgnoreCase("P")) {
                                            flight.setSpecialSample(Flight.Sample.PARACHUTE_DEPLOY, sampleNum);
                                        } else {
                                            try {
                                                float temperature = Float.parseFloat(parsedString);
                                                flight.addTemperature(sampleNum, temperature);
                                            } catch (NumberFormatException e) {
                                                System.err.println(e.getMessage());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.err.println(e.getMessage());
                        }
                    } else {
                        System.err.println("Corrupted sample!");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeLog(Path flightFile, Flight flight) {

        if (flightFile == null) {
            return false;
        }
        if (!flightFile.toFile().exists()) {
            try {
                flightFile.toFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(flightFile)) {
            for (Flight.Sample sample : Flight.Sample.values()) {
                Integer sampleNum = flight.getSpecialSample(sample);
                if (sampleNum != null) {
                    writer.write(META_INDICATOR + sample.getName() + ": " + sampleNum);
                    writer.newLine();
                }
            }
            for (Flight.Meta meta : Flight.Meta.values()) {
                String metaString = flight.getMetaData(meta);
                if (metaString != null) {
                    writer.write(META_INDICATOR + meta.getName() + ": " + metaString);
                    writer.newLine();
                }
            }

            for (int i = 0; i < flight.getTimeData().getSize(); i++) {
                long time = flight.getTimeData().get(i);
                float rawAlt = flight.getRawAltitudeData().get(i);
                float filteredAlt = flight.getFilteredAltitudeData().get(i);

                Float temperature = flight.getTemperature(i);
                String out;
                if (temperature == null) {
                    out = CSVHelper.format(new String[]{String.valueOf(time), String.valueOf(rawAlt), String.valueOf(filteredAlt)});
                } else {
                    out = CSVHelper.format(new String[]{String.valueOf(time), String.valueOf(rawAlt), String.valueOf(filteredAlt), String.valueOf(temperature)});
                }

                writer.write(out);
                writer.newLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
