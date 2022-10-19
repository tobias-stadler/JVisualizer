package de.tost.jvisualizer.io;

import java.util.List;

public class CSVHelper {

    private static final char SEPARATOR = ',';
    private static final char QUOTE = '"';
    private static final char SPACE = ' ';

    //   asdf   ,  "a,ds ""   , as"df" ,
    public static void parse(List<String> out, String line) {
        if (out == null || line == null || line.isEmpty()) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        boolean started = false;
        boolean inQuotes = false;
        boolean hasQuotes = false;
        boolean wasQuote = false;
        int removeLater = 0;

        char[] c = line.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == SEPARATOR) {
                if (started) {
                    if (inQuotes) {
                        builder.append(c[i]);
                    } else {
                        started = false;
                        inQuotes = false;
                        hasQuotes = false;
                        out.add(builder.substring(0, builder.length() - removeLater));
                        builder.setLength(0);
                        removeLater = 0;
                    }
                }
                wasQuote = false;
            } else if (c[i] == QUOTE) {
                if (started) {
                    if (wasQuote) {
                        builder.append(c[i]);
                    }
                    removeLater = 0;
                    if (hasQuotes) {
                        inQuotes = !inQuotes;
                    }
                    wasQuote = true;
                } else {
                    started = true;
                    hasQuotes = true;
                    inQuotes = true;
                }

            } else if (c[i] == SPACE) {
                if (started) {
                    builder.append(c[i]);
                    removeLater++;
                }
                wasQuote = false;
            } else {
                if (started) {
                    if (inQuotes || !hasQuotes) {
                        builder.append(c[i]);
                        removeLater = 0;
                    }
                } else {
                    started = true;
                    hasQuotes = false;
                    inQuotes = false;
                    builder.append(c[i]);
                }
                wasQuote = false;
            }
        }
        out.add(builder.substring(0, builder.length() - removeLater));
    }

    public static String format(String[] values) {

        StringBuilder builder = new StringBuilder();
        char c;
        for (int i = 0; i < values.length; i++) {
            if (i != 0) {
                builder.append(SEPARATOR);
            }
            builder.append(QUOTE);
            for (int k = 0; k < values[i].length(); k++) {
                c = values[i].charAt(k);
                if (c == QUOTE) {
                    builder.append(QUOTE);
                }
                builder.append(c);
            }
            builder.append(QUOTE);
        }

        return builder.toString();
    }

}
