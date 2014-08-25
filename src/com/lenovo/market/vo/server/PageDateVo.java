package com.lenovo.market.vo.server;

import java.util.ArrayList;

/** 分页vo */
public class PageDateVo<T> {
    
    private int pageSize;
    private int currentPageNO;
    private int totalPages;
    private long totalRows;
    private ArrayList<T> dateList;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageNO() {
        return currentPageNO;
    }

    public void setCurrentPageNO(int currentPageNO) {
        this.currentPageNO = currentPageNO;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
    }

    public ArrayList<T> getDateList() {
        return dateList;
    }

    public void setDateList(ArrayList<T> dateList) {
        this.dateList = dateList;
    }
}
