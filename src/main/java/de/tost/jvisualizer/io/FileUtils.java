package de.tost.jvisualizer.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static String loadTextFromClasspath(String resName) {

        InputStream inputStream = FileUtils.class.getResourceAsStream("/" + resName);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + '\n');
            }

        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }

        return builder.toString();
    }

}
