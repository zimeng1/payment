package com.mc.payment.fireblocksapi.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
//Payout Request")
public class PayoutReq implements Serializable {
    //Specify the payout Gateway contract address")
    @NotBlank(message = "[payoutGatewayAddress] is null")
    String payoutGatewayAddress;
    //payout list")
    @NotEmpty(message = "[payouts] is empty")
    List<PayOutDetail> payouts;
    //    //business-related information")
//    @NotEmpty(message = "[business] is empty")
//    List<Utf8String> business;

    //testnet 1, mainnet 5")
    Integer chainId;
}
