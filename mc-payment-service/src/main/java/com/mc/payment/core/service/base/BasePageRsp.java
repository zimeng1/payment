package com.mc.payment.core.service.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mc.payment.common.base.BaseRsp;

import java.util.Collections;
import java.util.List;

/**
 * 传输返回分页结果
 *
 * @author conor
 * @since 2024/01/26 14:23
 */
public class BasePageRsp<T> extends BaseRsp {

    /**
     * 查询数据列表
     */
    protected List<T> records = Collections.emptyList();

    /**
     * 总数
     */
    protected long total = 0;
    /**
     * 每页显示条数，默认 10
     */
    protected long size = 10;
    /**
     * 当前页
     */
    protected long current = 1;
    /**
     * 总页数
     */
    protected long pages = 1;
    /**
     * 是否存在上一页
     */
    protected boolean hasPrevious = false;
    /**
     * 是否存在下一页
     */
    protected boolean hasNext = false;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public BasePageRsp() {
    }

    public BasePageRsp(List<T> records, long total, long size, long current, long pages, boolean hasPrevious, boolean hasNext) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = pages;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }

    //====================
    public static <T> BasePageRsp<T> valueOf(Page<T> page) {
        return new BasePageRsp<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent(), page.getPages(), page.hasPrevious(), page.hasNext());
    }
}
