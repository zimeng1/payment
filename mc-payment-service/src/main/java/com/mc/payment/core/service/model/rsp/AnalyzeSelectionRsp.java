package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marty
 * @since 2024/6/6 11:48
 */
@Data
public class AnalyzeSelectionRsp implements Serializable {
    private static final long serialVersionUID = 5438285754257629287L;

    List<MerchantEntity> merchantList = new ArrayList<>();
    List<AccountEntity> accountTypeList = new ArrayList<>();
    List<AccountEntity> accountList = new ArrayList<>();
    List<String> assetList = new ArrayList<>();
    List<MerchantWalletEntity> walletList = new ArrayList<>();
    List<String> userIdList = new ArrayList<>();
}
