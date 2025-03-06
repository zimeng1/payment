package com.mc.payment.core.service.base;

import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 传输请求分页参数父类
 * @author conor
 * @since 2024/01/26 14:23
 */
public class BasePageReq extends BaseReq {
    /**
     * 每页显示条数，默认 10
     */
    @Schema(title = "每页显示条数",example = "10")
    protected long size = 10;

    /**
     * 当前页
     */
    @Schema(title = "当前页",example = "1")
    protected long current = 1;

    public BasePageReq() {
    }

    public BasePageReq(long size, long current) {
        this.size = size;
        this.current = current;
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
}
