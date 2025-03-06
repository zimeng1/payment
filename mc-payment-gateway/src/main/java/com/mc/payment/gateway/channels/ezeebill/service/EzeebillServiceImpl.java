package com.mc.payment.gateway.channels.ezeebill.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.channels.ezeebill.constants.EzeebillConstants;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillOrderReq;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillWithdrawalReq;
import com.mc.payment.gateway.channels.ezeebill.model.rsp.EzeebillOrderRsp;
import com.mc.payment.gateway.channels.ezeebill.model.rsp.EzeebillWithdrawalRsp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * EzeebillServiceImpl
 *
 * @author GZM
 * @since 2024/10/18 下午7:47
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EzeebillServiceImpl implements EzeebillService {

    @Value("${app.ezeebill-api-base-url}")
    private String apiUrl;

    @Override
    public RetResult<EzeebillOrderRsp> createOrder(EzeebillOrderReq req) {
        RetResult<EzeebillOrderRsp> result = new RetResult<>();
        Map<String, Object> map = req.convertToMap();
        log.info("Ezeebill Request Body:{}", map);
        String responseBody = null;
        try {
            // 发送POST请求
            HttpResponse response = HttpUtil.createPost(apiUrl)
                    .form(map)
                    .execute();
            // 获取响应信息
            responseBody = response.body();
            log.info("Ezeebill Response Body:{}",responseBody);

            // 将XML字符串解析为Document对象
            Document doc = XmlUtil.parseXml(responseBody);
            // 获取根节点
            Element root = doc.getDocumentElement();
            //判断是否收到正确返回结果
            if (response.getStatus() == 200) {
                EzeebillOrderRsp ezeebillOrderRsp = BeanUtil.mapToBean(XmlUtil.xmlToMap(root), EzeebillOrderRsp.class, CopyOptions.create().ignoreError());
                if (ezeebillOrderRsp.getTxn_response_code()==EzeebillConstants.EZEEBILL_DEPOSIT_SUCCESS_CODE
                    || ezeebillOrderRsp.getTxn_response_code()==EzeebillConstants.EZEEBILL_DEPOSIT_SUCCESS_CODE2) {
                    result = RetResult.data(ezeebillOrderRsp);
                } else {
                    result = RetResult.error(ezeebillOrderRsp.getTxn_response_code() + ":" + ezeebillOrderRsp.getTxn_message());
                }
            } else {
                result = RetResult.error(responseBody);
            }
        } catch (Exception e) {
            log.error("EzeebillService.createOrder error", e);
            result.setMsg("EzeebillService.createOrder error." + e.getMessage());
        } finally {
            log.info("EzeebillService.createOrder req:{},responseBody: {}", map, responseBody);
        }
        return result;
    }

    @Override
    public RetResult<EzeebillWithdrawalRsp> createPayOut(EzeebillWithdrawalReq req) {
        RetResult<EzeebillWithdrawalRsp> result = new RetResult<>();
        Map<String, Object> map = req.convertToMap();
        log.info("Ezeebill Withdrawal Request Body:{}", map);
        String responseBody = null;
        try {
            // 发送POST请求
            HttpResponse response = HttpUtil.createPost(apiUrl)
                    .form(map)
                    .execute();
            // 获取响应信息
            responseBody = response.body();
            log.info("Ezeebill Withdrawal Response Body:{}",responseBody);

            // 将XML字符串解析为Document对象
            Document doc = XmlUtil.parseXml(responseBody);
            // 获取根节点
            Element root = doc.getDocumentElement();
            //判断是否收到正确返回结果
            EzeebillWithdrawalRsp ezeebillWithdrawalRsp = BeanUtil.mapToBean(XmlUtil.xmlToMap(root), EzeebillWithdrawalRsp.class, CopyOptions.create().ignoreError());
            if (response.getStatus() == 200) {
                if (ezeebillWithdrawalRsp.getTxn_response_code()==EzeebillConstants.EZEEBILL_PAYOUT_SUCCESS_CODE
                        || ezeebillWithdrawalRsp.getTxn_response_code() == EzeebillConstants.EZEEBILL_PAYOUT_WAITING_CODE) {
                     result = RetResult.data(ezeebillWithdrawalRsp);
                } else {
                    result = RetResult.error(ezeebillWithdrawalRsp.getTxn_response_code() + ":" + ezeebillWithdrawalRsp.getTxn_message(),ezeebillWithdrawalRsp);
                }
            } else {
                result = RetResult.error(responseBody,ezeebillWithdrawalRsp);
            }
        } catch (Exception e) {
            log.error("EzeebillService.createPayOut error", e);
            result.setMsg("EzeebillService.createPayOut error." + e.getMessage());
        } finally {
            log.info("EzeebillService.createPayOut req:{},responseBody: {}", map, responseBody);
        }
        return result;
    }

}
