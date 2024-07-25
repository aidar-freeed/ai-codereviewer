package com.adins.mss.foundation.formatter;

import com.adins.mss.constant.Global;


public class PagingHelper {
    private int page = 1;
    private int totalPage;
    private int pageSize;
    private int start;
    private int end;
    private int totalRecord;

    public PagingHelper() {
        this.pageSize = Global.ROW_PER_PAGE;
    }

    public PagingHelper(int rowPerPage) {
        this.pageSize = rowPerPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStart() {
        this.start = (page - 1) * pageSize + 1;
        return start;
    }

    public int getEnd() {
        this.end = this.start + pageSize - 1;
        this.end = (this.end > totalRecord) ? totalRecord : this.end;
        return end;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
        this.totalPage = (int) Math.ceil((double) totalRecord / this.pageSize);
    }

    /* NAVIGATE */
    public void next() {
        page++;
        page = (page > totalPage) ? totalPage : page;
    }

    public void previous() {
        page--;
        page = (page < 1) ? 1 : page;
    }
}
