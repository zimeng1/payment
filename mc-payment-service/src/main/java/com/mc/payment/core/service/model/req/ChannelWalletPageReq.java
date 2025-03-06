package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Conor
 * @since 2024/4/15 下午2:36
 */
@Data
@Schema(title = "通道钱包-分页查询参数实体")
public class ChannelWalletPageReq extends BasePageReq {

    private static final long serialVersionUID = -9123941624155317527L;

    @Schema(title = "钱包id")
    private String id;

    @Schema(title = "通道子类型集合")
    private List<Integer> channelSubTypeList;

    @Schema(title = "钱包指定资产名称")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "钱包地址")
    private String walletAddress;

    @Schema(title = "隐藏无资产列表,[0:冻结金额, 1:可用金额, 2:商户资产余额]")
    private List<Integer> hideEmptyWalletList;

    @Schema(title = "状态,[0:待生成,1:生成中,2:生成失败,3:待使用,4:锁定中,5:冷却中]")
    private Integer status;

    @Schema(title = "按金额排序,[0:冻结金额, 1:可用金额, 2:商户资产余额]")
    private Integer sortByAmount;

    @Schema(title = "排序,[ASC:升序, DESC:降序]")
    private String sortOrder;

}
