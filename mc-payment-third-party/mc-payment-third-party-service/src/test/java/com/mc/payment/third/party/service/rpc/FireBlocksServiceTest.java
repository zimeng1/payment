package com.mc.payment.third.party.service.rpc;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author Marty
 * @since 2024/4/20 13:54
 */
@ExtendWith(MockitoExtension.class)
//@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
public class FireBlocksServiceTest {

    // 代付网关合约地址
    static String url = "https://sandbox-api.fireblocks.io/v1";
    // Decimals 6
    static String key = "028e9910-428a-4364-8dac-dde86ea0c59b";
    // Decimals 6
    static String secretKey = "-----BEGIN PRIVATE KEY----- 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 cdVzzCjBH31FLmjDlCQ2ZfWHXBgciQ== -----END PRIVATE KEY-----";

    @Resource
    @InjectMocks
    private FireBlocksFeignController fireBlocksService;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(fireBlocksService).alwaysDo(print()).build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTest() {
/*        MvcResult result = this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/fireBlocks/getTest")
                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(JSONUtil.toJsonStr(req))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(result);*/
    }


}
