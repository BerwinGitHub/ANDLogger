package com.berwin.logger.entity;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Log {
    //    {"Level", "Time", "PID", "TID", "Tag", "Text"};
    public static final String LEVEL_V = "V";
    public static final String LEVEL_D = "D";
    public static final String LEVEL_I = "I";
    public static final String LEVEL_W = "W";
    public static final String LEVEL_E = "E";
    public static final String LEVEL_F = "F";
    public static final String LEVEL_S = "S";

    private String level = LEVEL_V;
    private String time = "";
    private int pid = -1;
    private int tid = -1;
    private String tag = "";
    private String text = "";

    private String originText;

    public static Log buildLogForText(String text, String level) {
        Log log = new Log();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        String formatStr = formatter.format(new Date());
        log.setTime(formatStr);
        log.setTag("ANDLogger");
        log.setText(text);
        log.setLevel(level);
        log.setOriginText(String.format("%s  %d  %d  %s %s : %s", formatStr, log.getPid(), log.getTid(), log.getLevel(), log.getTag(), log.getText()));
        return log;
    }

    public static Log buildLogFromText(String txt) {
        Log log = new Log();
        log.setOriginText(txt);
        String str = new String(txt);
        // 时间
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return buildLogForText(txt, LEVEL_V);
        }
        log.setTime(matcher.group());
        str = str.replace(log.getTime(), "").trim();
        // pid
        int p = str.indexOf(" ");
        log.setPid(Integer.parseInt(str.substring(0, p)));
        str = str.substring(p + 1).trim();
        // tid
        p = str.indexOf(" ");
        log.setTid(Integer.parseInt(str.substring(0, p)));
        str = str.substring(p + 1).trim();
        // level
        p = str.indexOf(" ");
        log.setLevel(str.substring(0, p));
        str = str.substring(p + 1).trim();
        // tag
        p = str.indexOf(":");
        log.setTag(str.substring(0, p - 1).trim());
        str = str.substring(p + 1).trim();
        // text
        log.setText(str);
        return log;
    }


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOriginText() {
        return originText;
    }

    public void setOriginText(String originText) {
        this.originText = originText;
    }

    public boolean contains(String text) {
        return this.originText.contains(text);
    }

    public String[] toRowData() {
        //    {"Level", "Time", "PID", "TID", "Tag", "Text"};
        return new String[]{this.level, this.time, this.pid + "", this.tid + "", this.tag, this.text};
    }
}
