package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.api.model.rsp.QueryDepositReportRsp;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.mapper.DepositRecordMapper;
import com.mc.payment.core.service.model.enums.DepositRecordStatusEnum;
import com.mc.payment.core.service.model.req.DepositPageReq;
import com.mc.payment.core.service.model.req.DepositRecordDetailReq;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.rsp.DepositDetailExportRsp;
import com.mc.payment.core.service.model.rsp.DepositDetailRsp;
import com.mc.payment.core.service.model.rsp.DepositRecordPageRsp;
import com.mc.payment.core.service.service.IDepositRecordService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 入金记录表 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-04-17 18:00:15
 */
@Service
public class DepositRecordServiceImpl extends ServiceImpl<DepositRecordMapper, DepositRecordEntity> implements IDepositRecordService {

//    private final IAssetLastQuoteService assetLastQuoteService;
//    private final IWalletService walletService;

    //    @Autowired
//    public DepositRecordServiceImpl(IAssetLastQuoteService assetLastQuoteService, IWalletService walletService) {
//        this.assetLastQuoteService = assetLastQuoteService;
//        this.walletService = walletService;
//    }
    @Autowired
    private AppConfig appConfig;

    @Override
    public DepositRecordEntity getOne(String merchantId, String trackingId) {
        return this.getOne(Wrappers.lambdaQuery(DepositRecordEntity.class)
                .eq(DepositRecordEntity::getMerchantId, merchantId)
                .eq(DepositRecordEntity::getTrackingId, trackingId));
    }

    @Override
    public BasePageRsp<DepositRecordPageRsp> page(DepositPageReq req) {
        Page<DepositRecordPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.page(page, req);
        return BasePageRsp.valueOf(page);
    }


    /**
     * 查询有效的入金记录,用于匹配入金明细
     * 待入金和部分入金,且未过期(失效时间+冷却时间) 大于当前时间
     *
     * @param assetName
     * @param netProtocol
     * @param destinationAddress
     * @return
     */
    @Override
    public DepositRecordEntity queryEffective(String assetName, String netProtocol, String destinationAddress) {
        return this.getOne(Wrappers.lambdaQuery(DepositRecordEntity.class)
                .eq(DepositRecordEntity::getAssetName, assetName)
                .eq(DepositRecordEntity::getNetProtocol, netProtocol)
                .eq(DepositRecordEntity::getDestinationAddress, destinationAddress)
                .in(DepositRecordEntity::getStatus, 0, 1)
                .ge(DepositRecordEntity::getExpireTimestamp, System.currentTimeMillis() - appConfig.getWalletCooldownTime())
                .orderByDesc(DepositRecordEntity::getExpireTimestamp)
                .last("limit 1"));
    }

    @Override
    public List<DepositRecordEntity> listByMerchantIdsAntTime(Set<String> accountIdSet, MerchantQueryReq req) {
        LambdaQueryWrapper<DepositRecordEntity> query = Wrappers.lambdaQuery(DepositRecordEntity.class);
        //查询部分入金和全部入金的记录
        query.in(DepositRecordEntity::getStatus, Arrays.asList(DepositRecordStatusEnum.ITEM_1.getCode(), DepositRecordStatusEnum.ITEM_2.getCode()));

        if (CollectionUtils.isNotEmpty(req.getMerchantIdList())) {
            query.in(DepositRecordEntity::getMerchantId, req.getMerchantIdList());
        }
        if (CollectionUtils.isNotEmpty(accountIdSet)) {
            // 如果赛选账户的条件都为空, 就不调用in查询账户
            if (CollectionUtils.isNotEmpty(req.getAssetNameList()) || CollectionUtils.isNotEmpty(req.getAddrList()) || CollectionUtils.isNotEmpty(req.getAccountTypeList()) || CollectionUtils.isNotEmpty(req.getAccountIdList())) {
                query.in(DepositRecordEntity::getAccountId, accountIdSet);
            }
        }
        if (CollectionUtils.isNotEmpty(req.getAssetNameList())) {
            query.in(DepositRecordEntity::getAssetName, req.getAssetNameList());
        }
        if (CollectionUtils.isNotEmpty(req.getAddrList())) {
            query.in(DepositRecordEntity::getWalletId, req.getAddrList());
        }
        if (req.getTimeStart() != null) {
            query.ge(DepositRecordEntity::getCreateTime, req.getTimeStart());
        }
        if (req.getTimeEnd() != null) {
            query.le(DepositRecordEntity::getCreateTime, req.getTimeEnd());
        }
        if (CollUtil.isNotEmpty(req.getUserIdList())) {
            query.in(DepositRecordEntity::getUserId, req.getUserIdList());
        }
        //select asset_name,destination_address,amount,gas_fee,channel_fee,addr_balance,rate,fee_rate,create_time from  mcp_deposit_record r where r.deleted = 0
        query.select(DepositRecordEntity::getAccountId, DepositRecordEntity::getAssetName, DepositRecordEntity::getSourceAddress, DepositRecordEntity::getDestinationAddress, DepositRecordEntity::getAmount,
                DepositRecordEntity::getGasFee, DepositRecordEntity::getChannelFee, DepositRecordEntity::getAddrBalance, DepositRecordEntity::getWalletId,
                DepositRecordEntity::getRate, DepositRecordEntity::getFeeRate, DepositRecordEntity::getCreateTime);
        query.orderByDesc(DepositRecordEntity::getCreateTime);
        return this.getBaseMapper().selectList(query);
    }

    @Override
    public void depositExport(DepositPageReq req, HttpServletResponse response) throws UnsupportedEncodingException {
        BasePageRsp<DepositRecordPageRsp> depositPage = this.page(req);
        List<DepositRecordPageRsp> list = depositPage.getRecords();
        List<String> ids = list.stream().map(DepositRecordPageRsp::getId).collect(Collectors.toList());
        List<DepositDetailExportRsp> detailList = baseMapper.getDetail(ids);
        LocalDateTime now = LocalDateTime.now();
        String formatDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String fileName = "入金记录" + formatDate + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        try {
            WriteCellStyle style = new WriteCellStyle();
            style.setHorizontalAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).registerWriteHandler(
                    new HorizontalCellStyleStrategy(null, style)).build();
            WriteSheet depositSheet = EasyExcel.writerSheet(0, "入金记录").head(DepositRecordPageRsp.class).build();
            WriteSheet detailSheet = EasyExcel.writerSheet(1, "入金记录明细").head(DepositDetailExportRsp.class).build();
            excelWriter.write(list, depositSheet);
            excelWriter.write(detailList, detailSheet);
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getDepositAmount(String merchantId, Integer assetType, String assetName, String netProtocol) {
        List<DepositRecordEntity> list = lambdaQuery().eq(DepositRecordEntity::getMerchantId, merchantId)
                .eq(DepositRecordEntity::getAssetType, assetType)
                .eq(DepositRecordEntity::getAssetName, assetName)
                .eq(DepositRecordEntity::getNetProtocol, netProtocol)
                .in(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_1.getCode()
                        , DepositRecordStatusEnum.ITEM_2.getCode()).list();
        BigDecimal amount = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(list)) {
            amount = list.stream().map(DepositRecordEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return amount;
    }

    @Override
    public List<DepositRecordEntity> getOverdue() {
        List<DepositRecordEntity> list = baseMapper.getOverdue();
        return list;
    }

    @Override
    public BasePageRsp<DepositDetailRsp> getDepositDetailExport(DepositRecordDetailReq req) {
        Page<DepositDetailRsp> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.getDepositDetailExport(page, req);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public void depositDetailExport(DepositRecordDetailReq req, HttpServletResponse response) {
        BasePageRsp<DepositDetailRsp> depositExportPage = this.getDepositDetailExport(req);
        List<DepositDetailRsp> list = depositExportPage.getRecords();
        LocalDateTime now = LocalDateTime.now();
        String formatDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String fileName = "入金记录明细" + formatDate + ".xlsx";
        try {
            ClassPathResource resource = new ClassPathResource("/template/depositDetail.xlsx");
            InputStream depositDetailStream = resource.getInputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(depositDetailStream).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();
            excelWriter.fill(list, fillConfig, writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<QueryDepositReportRsp> queryReport(List<String> trackingIdList) {
        return baseMapper.queryReport(trackingIdList);
    }


}
