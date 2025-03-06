package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.UserEntity;
import lombok.Data;

import java.io.Serial;

/**
 * @author Conor
 * @since 2024/6/3 下午1:37
 */
@Data
public class UserPageRsp extends UserEntity {

    @Serial
    private static final long serialVersionUID = -7591011862643564220L;

}
