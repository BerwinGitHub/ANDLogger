package com.berwin.logger.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    // 被找到的列
    private List<Integer> findedList;
    private int row;
    private int finsSelectedColumn = -1;

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
        try {
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
            if (p > 0) {
                log.setTag(str.substring(0, p).trim());
                str = str.substring(p + 1).trim();
            } else {
                if (p == 0)
                    str = str.substring(1).trim();
                log.setTag("");
            }
            // text
            log.setText(str);
        } catch (Exception e) {
            System.out.println(txt);
            e.printStackTrace();
        }
        return log;
    }

    public Log() {
        this.findedList = new ArrayList<>();
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

    public boolean isFinded(int column) {
        return this.findedList.contains(column);
    }

    public void addFinded(int column) {
        this.findedList.add(column);
    }

    public void clearFinded() {
        this.findedList.clear();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setFindSelectedColumn(int column) {
        this.finsSelectedColumn = column;
    }

    public boolean isFindSelectedColumn(int column) {
        return this.finsSelectedColumn == column;
    }
}
