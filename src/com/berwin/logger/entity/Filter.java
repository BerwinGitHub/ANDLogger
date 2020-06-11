package com.berwin.logger.entity;

/**
 * 日志筛选
 */
public class Filter {
    private int logType = LogType.VERBOSE;
    private FilterSearch searchFilter = null;


    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public FilterSearch getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(FilterSearch searchFilter) {
        this.searchFilter = searchFilter;
    }

    public boolean matched(Log log) {
        int type = LogType.getType(log.getLevel());
        if (logType == LogType.VERBOSE || type == logType) {
            if (this.searchFilter.matched(log.getOriginText())) {
                return true;
            }
        }
        return false;
    }
}
