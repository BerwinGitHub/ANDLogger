package com.berwin.logger.entity;

import com.berwin.logger.utility.ColorUtility;
import com.berwin.logger.utility.UserDefault;

import java.awt.*;
import java.util.ArrayList;

public class LogType {
    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;
    public static final int FATAL = 5;
    public static final int SILENT = 6;

    public static final String LEVEL_VERBOSE = "V";
    public static final String LEVEL_DEBUG = "D";
    public static final String LEVEL_INFO = "I";
    public static final String LEVEL_WARN = "W";
    public static final String LEVEL_ERROR = "E";
    public static final String LEVEL_FATAL = "F";
    public static final String LEVEL_SILENT = "S";

    public static final String NAME_VERBOSE = "Verbose";
    public static final String NAME_DEBUG = "Debug";
    public static final String NAME_INFO = "Info";
    public static final String NAME_WARN = "Warn";
    public static final String NAME_ERROR = "Error";
    public static final String NAME_FATAL = "Fatal";
    public static final String NAME_SILENT = "Silent";

    public static final Color COLOR_VERBOSE = ColorUtility.hexToColor(UserDefault.getInstance().getValueForKey("color_" + NAME_VERBOSE, ColorUtility.colorToHex(new Color(64, 64, 64))));
    public static final Color COLOR_DEBUG = ColorUtility.hexToColor(UserDefault.getInstance().getValueForKey("color_" + NAME_DEBUG, ColorUtility.colorToHex(new Color(64, 64, 64))));
    public static final Color COLOR_INFO = ColorUtility.hexToColor(UserDefault.getInstance().getValueForKey("color_" + NAME_INFO, ColorUtility.colorToHex(new Color(64, 64, 64))));
    public static final Color COLOR_WARN = ColorUtility.hexToColor(UserDefault.getInstance().getValueForKey("color_" + NAME_WARN, ColorUtility.colorToHex(new Color(189, 140, 23))));
    public static final Color COLOR_ERROR = ColorUtility.hexToColor(UserDefault.getInstance().getValueForKey("color_" + NAME_ERROR, ColorUtility.colorToHex(new Color(200, 50, 0))));
    public static final Color COLOR_FATAL = ColorUtility.hexToColor(UserDefault.getInstance().getValueForKey("color_" + NAME_FATAL, ColorUtility.colorToHex(new Color(200, 0, 0))));
    public static final Color COLOR_SILENT = ColorUtility.hexToColor(UserDefault.getInstance().getValueForKey("color_" + NAME_SILENT, ColorUtility.colorToHex(new Color(64, 64, 64))));

    private static ArrayList<LogInfo> logInfoList = new ArrayList() {
        {
            add(new LogInfo(VERBOSE, COLOR_VERBOSE, NAME_VERBOSE, LEVEL_VERBOSE));
            add(new LogInfo(DEBUG, COLOR_DEBUG, NAME_DEBUG, LEVEL_DEBUG));
            add(new LogInfo(INFO, COLOR_INFO, NAME_INFO, LEVEL_INFO));
            add(new LogInfo(WARN, COLOR_WARN, NAME_WARN, LEVEL_WARN));
            add(new LogInfo(ERROR, COLOR_ERROR, NAME_ERROR, LEVEL_ERROR));
            add(new LogInfo(FATAL, COLOR_FATAL, NAME_FATAL, LEVEL_FATAL));
            add(new LogInfo(SILENT, COLOR_SILENT, NAME_SILENT, LEVEL_SILENT));
        }
    };

    public static Color getColor(int logType) {
        for (LogInfo logInfo : logInfoList) {
            if (logInfo.getType() == logType)
                return logInfo.getColor();
        }
        return Color.DARK_GRAY;
    }

    public static Color getColorByLevel(String level) {
        for (LogInfo logInfo : logInfoList) {
            if (logInfo.getLevel().equals(level))
                return logInfo.getColor();
        }
        return Color.DARK_GRAY;
    }

    public static Color getColorByName(String name) {
        for (LogInfo logInfo : logInfoList) {
            if (logInfo.getName().equals(name))
                return logInfo.getColor();
        }
        return Color.DARK_GRAY;
    }

    public static int getTypeByLevel(String level) {
        for (LogInfo logInfo : logInfoList) {
            if (logInfo.getLevel().equals(level))
                return logInfo.getType();
        }
        return VERBOSE;
    }

    public static String[] getLogNames() {
        String[] types = new String[logInfoList.size()];
        int i = 0;
        for (LogInfo logInfo : logInfoList) {
            types[i++] = logInfo.getName();
        }
        return types;
    }

    public static void setLogColor(String type, Color newColor) {
        for (LogInfo logInfo : logInfoList) {
            if (logInfo.getName().equals(type))
                logInfo.setColor(newColor);
        }

    }
}
