package io.kida.yuen.vo.pagehelper;

import java.util.List;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: Pages.java
 * @ClassName: Pages
 * @Description:page分页参数
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class Pages<T> {

    // 每页显示条数
    private int pageSize = 10;

    // 总条数
    private int totalCount;

    // 开始记录数
    private int start;

    // 总页数
    private int totalPages;
    private List<T> pageList;

    public int getStart() {
        return start;
    }

    public Pages(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentPageNo() {
        return start / pageSize + 1;
    }

    public boolean getHasNextPage() {
        return getCurrentPageNo() < totalPages;
    }

    public boolean getHasPavPage() {
        return getCurrentPageNo() > 1;
    }

    public int getTotalPages() {
        totalPages = totalCount / pageSize;
        if (totalCount % pageSize != 0) {
            totalPages++;
        }
        return totalPages;
    }

    public int getStart(int pageNo) {

        if (pageNo < 1) {
            pageNo = 1;
        } else if (pageNo > getTotalPages()) {
            pageNo = getTotalPages();
        }

        start = (pageNo - 1) * pageSize;
        return start;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public List<T> getPageList() {
        return pageList;
    }

    public void setPageList(List<T> pageList) {
        this.pageList = pageList;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
