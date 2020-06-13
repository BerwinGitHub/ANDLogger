package com.berwin.logger.entity;

import java.awt.*;

public class LogInfo {

    private int type = LogType.VERBOSE;
    // ADB 输出的日志类型表示(V,D,F,E,S,I,W)
    private String level = LogType.LEVEL_VERBOSE;

    private Color color = Color.GRAY;

    private String name;

    public LogInfo(int type, Color color, String name, String level) {
        this.type = type;
        this.color = color;
        this.name = name;
        this.level = level;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
