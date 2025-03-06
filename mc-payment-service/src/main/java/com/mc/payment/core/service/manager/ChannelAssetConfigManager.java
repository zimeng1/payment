package com.mc.payment.core.service.manager;

import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigSaveReq;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigUpdateReq;

public interface ChannelAssetConfigManager {

    String save(ChannelAssetConfigSaveReq req);

    boolean updateById(ChannelAssetConfigUpdateReq req);
}
