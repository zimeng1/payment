package com.mc.payment.core.service.util;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.mc.payment.core.service.model.dto.AssetConfigSqlExcelDto;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * 根据资产配置excle生成更新sql工具
 *
 * @author Conor
 * @since 2024/5/20 上午10:41
 */
@Slf4j
public class AssetConfigExcel2SqlUtil {

    public static void main(String[] args) throws FileNotFoundException {
        // 读取桌面的excel文件
        String filePath = "D:\\Projects\\mc-payment\\doc\\template\\变更资产模板.xlsx";
        FileInputStream file = new FileInputStream(filePath);

        // ReadListener不是必须的，它主要的设计是读取excel数据的后置处理(并考虑一次性读取到内存潜在的内存泄漏问题)
        EasyExcelFactory.read(file, AssetConfigSqlExcelDto.class, new ReadListener<AssetConfigSqlExcelDto>() {
            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 100;
            /**
             *临时存储
             */
            private List<AssetConfigSqlExcelDto> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(AssetConfigSqlExcelDto assetConfigExcelDto, AnalysisContext analysisContext) {
                cachedDataList.add(assetConfigExcelDto);
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                saveData();
            }

            /**
             * 加上存储数据库
             */
            private void saveData() {
                for (AssetConfigSqlExcelDto dto : cachedDataList) {


                    System.out.println("update mcp_wallet set asset_name = '" + dto.getAssetName() + "' ,net_protocol ='" + dto.getNetProtocol() + "' WHERE channel_asset_name ='" + dto.getChannelAssetName() + "';");

                    System.out.println("UPDATE mcp_asset_config t1 LEFT JOIN mcp_channel_asset t2 ON t1.asset_name = t2.asset_name AND t1.net_protocol = t2.net_protocol\n" +
                            "SET t1.asset_name='" + dto.getAssetName() + "',t2.asset_name='" + dto.getAssetName() + "',t1.asset_net='" + dto.getAssetNet() + "',t1.net_protocol='" + dto.getNetProtocol() + "',t2.net_protocol='" + dto.getNetProtocol() + "',t1.fee_asset_name='" + dto.getFeeAssetName() + "' WHERE t2.channel_asset_name = '" + dto.getChannelAssetName() + "';");
                }
            }

        }).sheet().doRead();
    }
}
