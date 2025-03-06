package com.mc.payment.core.service.web;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetConfigControllerTest {

//    private MockMvc mockMvc;
//    @InjectMocks
//    private AssetConfigController assetConfigController;
//
//
//    @BeforeEach
//    void init() {
//        mockMvc = MockMvcBuilders.standaloneSetup(assetConfigController).alwaysDo(print()).build();
//        MockitoAnnotations.openMocks(this);
//    }
//
//    //    @Test
//    @ParameterizedTest(name = "test save when assetName={0},assetNet={1},status={2},tokenAddress={3},expectedMsg={4}")
//    @CsvSource({
//            "'',资产网络,1,合约地址,[资产名称]不能为空;",
//            ",资产网络,1,合约地址,[资产名称]不能为空;",
//            "' ',资产网络,1,合约地址,[资产名称]不能为空;",
//            "123456789012345678901,资产网络,1,合约地址,[资产名称]长度不能超过20;",
//            "资产名称-单元测试,'',1,合约地址,[资产网络]不能为空;",
//            "资产名称-单元测试,123456789012345678901,1,合约地址,[资产网络]长度不能超过20;",
//            "资产名称-单元测试,资产网络-单元测试,2,合约地址,'[资产状态]必须为0或1,0:禁用,1:激活;'",
//            "资产名称-单元测试,资产网络-单元测试,-1,合约地址,'[资产状态]必须为0或1,0:禁用,1:激活;'",
//            "资产名称-单元测试,资产网络-单元测试,,合约地址,'[资产状态]必须为0或1,0:禁用,1:激活;'",
//            "资产名称-单元测试,资产网络-单元测试,1,'',[合约地址]不能为空;",
//    })
//    void saveValidated(String assetName, String assetNet, Integer status, String tokenAddress, String expectedMsg) throws Exception {
//        AssetConfigSaveReq req = new AssetConfigSaveReq(assetName, assetNet, status, tokenAddress, "");
//        MvcResult result = this.mockMvc.perform(
//                        MockMvcRequestBuilders.post("/asset/config/save")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(JSONUtil.toJsonStr(req))
//                )
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        Assertions.assertEquals(expectedMsg, getValidMsg(result));
//    }
//
//    private static String getValidMsg(MvcResult result) {
//        MethodArgumentNotValidException resolvedException = (MethodArgumentNotValidException) result.getResolvedException();
//        List<ObjectError> allErrors = resolvedException.getBindingResult().getAllErrors();
//        return allErrors.stream().map(allError -> allError.getDefaultMessage() + ";").collect(Collectors.joining());
//    }
//
//    @ParameterizedTest(name = "test updateById when id={0},assetNet={1},status={2},tokenAddress={3},expectedMsg={4}")
//    @CsvSource({
//            "'',资产网络-单元测试,1,合约地址,[资产id]不能为空;",
//            ",资产网络-单元测试,1,合约地址,[资产id]不能为空;",
//            "id,'',1,合约地址,[资产网络]不能为空;",
//            "id,,1,合约地址,[资产网络]不能为空;",
//            "id,123456789012345678901,1,合约地址,[资产网络]长度不能超过20;",
//            "id,资产网络-单元测试,2,合约地址,'[资产状态]必须为0或1,0:禁用,1:激活;'",
//            "id,资产网络-单元测试,,合约地址,'[资产状态]必须为0或1,0:禁用,1:激活;'",
//            "id,资产网络-单元测试,-1,合约地址,'[资产状态]必须为0或1,0:禁用,1:激活;'",
//    })
//    void updateByIdValidated(String id, String assetNet, Integer status, String tokenAddress, String expectedMsg) throws Exception {
//
//        AssetConfigUpdateReq req = new AssetConfigUpdateReq();
//        req.setId(id);
//        req.setAssetNet(assetNet);
//        req.setStatus(status);
////        req.setTokenAddress(tokenAddress);
//
//        MvcResult result = this.mockMvc.perform(
//                        MockMvcRequestBuilders.post("/asset/config/updateById")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(JSONUtil.toJsonStr(req))
//                )
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andReturn();
//
//        Assertions.assertEquals(expectedMsg, getValidMsg(result));
//    }


}