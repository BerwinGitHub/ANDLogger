package com.berwin.logger.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterSearch {

    private String content;

    private boolean isMatchCase = false;

    private boolean isWords = false;

    private boolean isRegex = false;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMatchCase() {
        return isMatchCase;
    }

    public void setMatchCase(boolean matchCase) {
        isMatchCase = matchCase;
    }

    public boolean isWords() {
        return isWords;
    }

    public void setWords(boolean words) {
        isWords = words;
    }

    public boolean isRegex() {
        return isRegex;
    }

    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    public boolean matched(String text) {
        if (content.equals(""))
            return true;
        if (this.isRegex) {
            Pattern pattern = Pattern.compile(this.content);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find())
                return true;
        } else {
            if (this.isWords) {
                if (this.isMatchCase) {
                    String reg = "[^a-zA-Z]" + this.content + "[^a-zA-Z]";
                    Pattern pattern = Pattern.compile(reg);
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find())
                        return true;
                } else {
                    String reg = "[^a-zA-Z]" + this.content.toLowerCase() + "[^a-zA-Z]";
                    Pattern pattern = Pattern.compile(reg);
                    Matcher matcher = pattern.matcher(text.toLowerCase());
                    if (matcher.find())
                        return true;
                }
            } else {
                if (this.isMatchCase) {
                    if (text.contains(this.content))
                        return true;
                } else {
                    if (text.toLowerCase().contains(this.content.toLowerCase()))
                        return true;
                }
            }
        }
        return false;
    }
}
