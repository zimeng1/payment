package com.mc.payment.core.service.facade;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.model.enums.AccountStatusEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.service.*;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.CreateAccountReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.VaultAccountVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Conor
 * @since 2024/4/26 下午9:21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceFacade {
    private final IAccountService accountService;
    private final FireBlocksAPI fireBlocksAPI;
    private final WalletServiceFacade walletServiceFacade;


    public void generateForeBlocksAccount(String accountId) {
        AccountEntity accountEntity = accountService.getById(accountId);
        if (accountEntity == null) {
            log.error("generateForeBlocksAccount,账号不存在,accountId:{}", accountId);
            return;
        }
        if (accountEntity.getChannelSubType() != ChannelSubTypeEnum.FIRE_BLOCKS.getCode()) {
            log.error("generateForeBlocksAccount,通道子类型不是fireblocks,accountId:{}", accountId);
            return;
        }
        if (accountEntity.getStatus() != AccountStatusEnum.GENERATE_WAIT.getCode()) {
            log.error("generateForeBlocksAccount,账号状态不是待生成,accountId:{}", accountId);
            return;
        }
        accountService.update(Wrappers.lambdaUpdate(AccountEntity.class).set(AccountEntity::getStatus, AccountStatusEnum.GENERATE_ING.getCode()).eq(AccountEntity::getId, accountId));
        // 调用远程接口创建远程账号
        CreateAccountReq createAccountReq = new CreateAccountReq();
        createAccountReq.setName(accountEntity.getName());
        RetResult<VaultAccountVo> retResult = fireBlocksAPI.createAccount(createAccountReq);
        log.debug("fireBlocksAPI.createAccount createAccountReq:{},ret:{}", createAccountReq, retResult);
        if (retResult.isSuccess()) {
            accountEntity.setStatus(AccountStatusEnum.GENERATE_SUCCESS.getCode());
            accountEntity.setExternalId(retResult.getData().getId());
        } else {
            accountEntity.setStatus(AccountStatusEnum.GENERATE_FAIL.getCode());
            accountEntity.setStatusMsg(retResult.getMsg());
        }
        accountService.updateById(accountEntity);
    }

    /**
     * 扫描待生成的账户,根据通道要求完成账户生成
     */
    public void generateAccountJob() {
        List<AccountEntity> list = accountService.list(Wrappers.lambdaQuery(AccountEntity.class)
                .eq(AccountEntity::getStatus, AccountStatusEnum.GENERATE_WAIT.getCode()));
        for (AccountEntity accountEntity : list) {
            if (accountEntity.getChannelSubType() == ChannelSubTypeEnum.FIRE_BLOCKS.getCode()) {
                this.generateForeBlocksAccount(accountEntity.getId());
                walletServiceFacade.initChannelAndMerchantWallet(accountEntity.getId());
            }
        }
    }
}
