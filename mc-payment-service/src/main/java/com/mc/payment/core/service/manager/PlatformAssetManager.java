package com.mc.payment.core.service.manager;

import com.mc.payment.core.service.model.req.platform.PlatformAssetUpdateReq;

public interface PlatformAssetManager {
    boolean updateById(PlatformAssetUpdateReq req);

}
