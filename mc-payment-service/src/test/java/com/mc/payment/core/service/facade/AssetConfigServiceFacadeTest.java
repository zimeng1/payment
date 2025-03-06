package com.mc.payment.core.service.facade;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetConfigServiceFacadeTest {
/*
    @InjectMocks
    private AssetConfigServiceFacade assetConfigServiceFacade;
    @Mock
    private IAssetConfigService assetConfigService;
    @Mock
    private IChannelCostService channelCostService;


    @ParameterizedTest(name = "test updateById when id={0},assetNet={1},status={2},expectedCode={3},expectedMsg={4}")
    @CsvSource({
            "不存在的id,资产网络-单元测试,1,500,该数据不存在",
    })
    void updateById_1(String id, String assetNet, Integer status, int expectedCode, String expectedMsg) {
        when(assetConfigService.getById("不存在的id")).thenReturn(null);

        AssetConfigUpdateReq req = new AssetConfigUpdateReq();
        req.setId(id);
        req.setAssetNet(assetNet);
        req.setStatus(status);

        RetResult<Boolean> result = assetConfigServiceFacade.updateById(req);

        Assertions.assertEquals(expectedCode, result.getCode());
        Assertions.assertEquals(expectedMsg, result.getMsg());

    }

    @ParameterizedTest(name = "test updateById when id={0},assetNet={1},status={2},expectedCode={3},expectedMsg={4}")
    @CsvSource({
            "存在的id,资产网络-单元测试,1,500,此资产目前已被配置，请先取消配置后再进行修改。",
    })
    void updateById_2(String id, String assetNet, Integer status, int expectedCode, String expectedMsg) {
        when(assetConfigService.getById("存在的id")).thenReturn(new AssetConfigEntity());
        when(channelCostService.checkConfigByAssetId("存在的id")).thenReturn(true);


        AssetConfigUpdateReq req = new AssetConfigUpdateReq();
        req.setId(id);
        req.setAssetNet(assetNet);
        req.setStatus(status);

        RetResult<Boolean> result = assetConfigServiceFacade.updateById(req);

        Assertions.assertEquals(expectedCode, result.getCode());
        Assertions.assertEquals(expectedMsg, result.getMsg());

    }*/
}