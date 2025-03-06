package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mc.payment.core.service.entity.ChannelWalletEntity;
import com.mc.payment.core.service.model.dto.FireBlocksWalletSyncInfoDto;
import com.mc.payment.core.service.model.req.ChannelWalletPageReq;
import com.mc.payment.core.service.model.rsp.ChannelWalletExportRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Conor
* @description 针对表【mcp_channel_wallet(通道钱包)】的数据库操作Mapper
* @createDate 2024-08-14 13:44:50
* @Entity com.mc.payment.core.service.entity.ChannelWalletEntity
*/
public interface ChannelWalletMapper extends BaseMapper<ChannelWalletEntity> {


    /**
     * 通道钱包查询
     * @param page
     * @param req
     * @return
     */
    IPage<ChannelWalletEntity> page(IPage<ChannelWalletEntity> page, @Param("req") ChannelWalletPageReq req);

    /**
     * 查询同步fireblocks钱包需要的信息
     */
    List<FireBlocksWalletSyncInfoDto> queryFireBlocksWalletSyncInfo(@Param("accountId") String accountId);

    /**
     * 通道钱包导出查询
     * @param req
     * @return
     */
    List<ChannelWalletExportRsp> queryExportInfo(@Param("req") ChannelWalletPageReq req);
}




