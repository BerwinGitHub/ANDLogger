package com.berwin.logger.entity;


import com.berwin.logger.views.MainView;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    public static final int MAX_CACHE = 1024 * 512;

    public static final String LOG_VERBOSE = "Verbose";
    public static final String LOG_DEBUG = "Debug";
    public static final String LOG_INFO = "Info";
    public static final String LOG_WARN = "Warn";
    public static final String LOG_ERROR = "Error";
    public static final String LOG_ASSERT = "Assert";
    public static final List<String> LOG_CMD_MARK = new ArrayList<String>() {
        {
            add("V");
            add("D");
            add("I");
            add("W");
            add("E");
            add("F");
            add("S");
        }
    };

    private JScrollPane scrollPane;
    private JTextPane textPane;
    private String content;

    public Logger(JScrollPane scrollPane, JTextPane textPane, String content) {
        this.scrollPane = scrollPane;
        this.textPane = textPane;
        this.content = content;
    }

    public void type(int idx) {
        switch (idx) {
            case 0:
                this.verbose();
                break;
            case 1:
                this.debug();
                break;
            case 2:
                this.info();
                break;
            case 3:
                this.warn();
                break;
            case 4:
                this.error();
                break;
            case 5:
                this.asserts();
                break;
        }
    }

    public void verbose() {
        this.setDocs(this.content, Color.GRAY, false, 12);
    }

    public void error() {
        this.setDocs(this.content, Color.RED, false, 12);
    }

    public void warn() {
        this.setDocs(this.content, Color.YELLOW, false, 12);
    }

    public void info() {
        this.setDocs(this.content, Color.BLUE, false, 12);
    }

    public void debug() {
        this.setDocs(this.content, Color.RED, false, 12);
    }

    public void asserts() {
        this.setDocs(this.content, Color.MAGENTA, false, 12);
    }

    public void title() {
        this.setDocs(this.content, Color.DARK_GRAY, true, 14);
    }

    public void color(Color color) {
        this.setDocs(this.content, color, false, 12);
    }


    public void error(int fontSize) {
        this.setDocs(this.content, Color.RED, false, fontSize);
    }

    public void warn(int fontSize) {
        this.setDocs(this.content, Color.YELLOW, false, fontSize);
    }

    public void info(int fontSize) {
        this.setDocs(this.content, Color.BLUE, false, fontSize);
    }

    public void debug(int fontSize) {
        this.setDocs(this.content, Color.PINK, false, fontSize);
    }

    public void asserts(int fontSize) {
        this.setDocs(this.content, Color.MAGENTA, false, fontSize);
    }

    public void title(int fontSize) {
        this.setDocs(this.content, Color.DARK_GRAY, true, fontSize);
    }

    public void color(Color color, int fontSize) {
        this.setDocs(this.content, color, false, fontSize);
    }


    private void insert(String str, AttributeSet attrSet) {
        Document doc = textPane.getDocument();
        str = str + "\n";
        try {
            doc.insertString(doc.getLength(), str, attrSet);
            int delta = doc.getLength() - MAX_CACHE;
            if (delta > 0)
                doc.remove(0, delta);
        } catch (BadLocationException e) {
            System.out.println("BadLocationException: " + e);
        }
    }

    private void setDocs(String str, Color col, boolean bold, int fontSize) {
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attrSet, col);
        //颜色
        if (bold == true) {
            StyleConstants.setBold(attrSet, true);
        }//字体类型
        StyleConstants.setFontSize(attrSet, fontSize);
        //字体大小
        insert(str, attrSet);
        if (!MainView.isScrollBottom)
            this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());
    }

}
