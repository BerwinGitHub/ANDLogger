package com.berwin.logger.entity;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LogType {
    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;
    public static final int FATAL = 5;
    public static final int SILENT = 6;

    private static Map<String, Integer> typeStringMapping = new HashMap() {{
        put("V", VERBOSE);
        put("D", DEBUG);
        put("I", INFO);
        put("W", WARN);
        put("E", ERROR);
        put("F", FATAL);
        put("S", SILENT);
    }};

    private static Map<Integer, Color> typeColorMapping = new HashMap() {{
        put(VERBOSE, Color.DARK_GRAY);
        put(DEBUG, new Color(124, 174, 252));
        put(INFO, new Color(110, 195, 46));
        put(WARN, new Color(189, 140, 23));
        put(ERROR, new Color(200, 50, 0));
        put(FATAL, new Color(200, 0, 0));
        put(SILENT, Color.DARK_GRAY);
    }};

    public static Color getColor(int logType) {
        if (typeColorMapping.containsKey(logType)) {
            return typeColorMapping.get(logType);
        }
        return Color.DARK_GRAY;
    }

    public static Color getColor(String logType) {
        if (typeStringMapping.containsKey(logType)) {
            int type = typeStringMapping.get(logType);
            if (typeColorMapping.containsKey(type)) {
                return typeColorMapping.get(type);
            }
        }
        return Color.DARK_GRAY;
    }

    public static int getType(String logType) {
        if (typeStringMapping.containsKey(logType)) {
            return typeStringMapping.get(logType);
        }
        return VERBOSE;
    }

    public static String[] getLogTypes() {
        return new String[]{"Verbose", "Debug", "Info", "Warn", "Error", "Fatal", "Silent"};
    }

    private static Color fromStrToARGB(String str) {
        str = str.replace("#", "");
        String str1 = str.substring(0, 2);
        String str2 = str.substring(2, 4);
        String str3 = str.substring(4, 6);
        String str4 = str.substring(6, 8);
        int alpha = Integer.parseInt(str1, 16);
        int red = Integer.parseInt(str2, 16);
        int green = Integer.parseInt(str3, 16);
        int blue = Integer.parseInt(str4, 16);
        Color color = new Color(red, green, blue, alpha);
        return color;
    }
}
