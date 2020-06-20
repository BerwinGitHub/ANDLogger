package com.berwin.logger.entity;

public class FindSelect {
    private int column;
    private Log log;

    public FindSelect(int column, Log log) {
        this.column = column;
        this.log = log;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public void select() {
        this.log.setFindSelectedColumn(this.column);
    }

    public void unselect() {
        this.log.setFindSelectedColumn(-1);
    }
}
