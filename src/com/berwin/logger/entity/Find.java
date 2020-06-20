package com.berwin.logger.entity;

import java.util.ArrayList;
import java.util.List;

public class Find {
    private FilterSearch searchFilter = null;

    public FilterSearch getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(FilterSearch searchFilter) {
        this.searchFilter = searchFilter;
    }

    public boolean isHasCondition() {
        return !this.searchFilter.getContent().equals("");
    }

    public List<Integer> finded(Log log) {
        List<Integer> result = new ArrayList<>();
        log.clearFinded();
        String[] rowDatas = log.toRowData();
        for (int i = 0; i < rowDatas.length; i++) {
            if (this.searchFilter.matched(rowDatas[i])) {
                result.add(i);
            }
        }
        return result;
    }
}
