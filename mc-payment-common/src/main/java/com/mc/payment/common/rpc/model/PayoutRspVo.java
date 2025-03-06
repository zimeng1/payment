package com.mc.payment.common.rpc.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * payout
 *
 * 
 * @since 2024/2/21 11:15
 */
@Data
public class PayoutRspVo implements Serializable {
   // "The transaction occurred at the corresponding block height.")
    private Long blockId;
   // "ID used to identify different chains.")
    private Integer chainId;
   // "The corresponding smart contract address is triggered by this transaction")
    private String contractAddress;
   // "")
    private List<PayoutDetailRspVo> detailList;
   // "The address associated with initiating this transaction.")
    private String fromAddress;
   // "Your unique identifier in BlockATM")
    private Integer merchantId;
    // "apiKey")
    private Integer apiKey;
   // "Code of the blockchain.(e.g. Ethereum、TRON、Arbtrium)")
    private String network;
   // "The Transaction identifier in blockchain")
    private String txId;
   // "status")
    private Integer status;
}

