package com.mc.payment.core.service.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.hutool.core.exceptions.ValidateException;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author conor
 * @since 2024/01/25 10:35
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public RetResult handlerException(Exception e) {
        if (e instanceof NotLoginException notLoginException) {
            log.debug("未登录,{}", notLoginException.getMessage());
            return RetResult.code(401, e.getMessage());
        } else if (e instanceof NotPermissionException notPermissionException) {
            log.debug("无权限,{}", notPermissionException.getPermission());
            return RetResult.error(e);
        } else if (e instanceof BusinessException businessException) {
            log.debug("业务异常,{}", businessException.getMessage());
            ExceptionTypeEnum exceptionTypeEnum = businessException.getExceptionTypeEnum();
            return RetResult.code(exceptionTypeEnum.getCode(), businessException.getMessage());
        } else if (e instanceof ValidateException validateException) {
            log.debug("参数校验不通过,{}", validateException.getMessage());
            return RetResult.code(303, validateException.getMessage());
        }
        log.error("系统异常", e);
        return RetResult.error(e);
    }

    @ExceptionHandler
    public RetResult handlerException(MethodArgumentNotValidException e) {
        log.debug("参数校验不通过", e);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String msg = allErrors.stream().map(allError -> allError.getDefaultMessage() + ";").collect(Collectors.joining());
        return RetResult.code(303, msg);
    }

}
