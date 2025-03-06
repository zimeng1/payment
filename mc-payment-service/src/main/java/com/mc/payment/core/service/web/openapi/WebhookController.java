package com.mc.payment.core.service.web.openapi;

import com.mc.payment.core.service.config.aspect.WebhookRecord;
import com.mc.payment.core.service.service.IWebhookService;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillDepositCallBackReq;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillWithdrawalCallBackReq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/openapi/webhook")
public class WebhookController {

    @Autowired
    private IWebhookService webhookService;

    @WebhookRecord("OfaPay-deposit")
    @PostMapping("/ofaPay/deposit")
    public String ofaPayDeposit(@RequestBody String payload) {
        log.info("ofaPayDeposit Received webhook payload: {}", payload);
        return webhookService.depositCallBack(payload);
    }

    @WebhookRecord("OfaPay-withdraw")
    @PostMapping("/ofaPay/withdraw")
    public String ofaPayWithdraw(@RequestBody String payload) {
        log.info("ofaPayWithdraw Received webhook payload: {}", payload);
        return webhookService.withdrawCallBack(payload);
    }

    @WebhookRecord("Paypal-order-approved")
    @PostMapping("/paypal/order/approved")
    public String paypalOrderApproved(@RequestBody String payload, HttpServletRequest request) {
        log.info("paypalOrderApproved Received webhook payload: {}", payload);
        return webhookService.paypalOrderApproved(payload, request);
    }

    @WebhookRecord("Paypal")
    @PostMapping("/paypal")
    public String paypal(@RequestBody String payload, HttpServletRequest request) {
        log.info("paypal Received webhook payload: {}", payload);
        // 9BL42669A48311459
        return "Webhook received";
    }

    @WebhookRecord("Paypal-payoutsItem-succeeded")
    @PostMapping("/paypal/payoutsItem/succeeded")
    public String paypalPayoutsItemSucceeded(@RequestBody String payload, HttpServletRequest request) {
        //勿删，暂时注释
//		log.info("paypalPayoutsItemSucceeded Received webhook payload: {}", payload);
//		return webhookService.paypalPayoutsItemSucceeded(payload, request);
        return null;
    }

    @WebhookRecord("Paypal-payoutsItem-failed")
    @PostMapping("/paypal/payoutsItem/failed")
    public String paypalPayoutsItemFailed(@RequestBody String payload, HttpServletRequest request) {
        //勿删，暂时注释
//		log.info("paypalPayoutsItemFailed Received webhook payload: {}", payload);
//		return webhookService.paypalPayoutsItemFailed(payload, request);
        return null;
    }

    @WebhookRecord("PassToPay-deposit")
    @PostMapping("/passToPay/deposit")
    public String passToPayDeposit(@RequestBody String payload) {
        log.info("passToPayDeposit Received webhook payload: {}", payload);
        return webhookService.passToPayDepositCallBack(payload);
    }

    @WebhookRecord("Ezeebill-deposit")
    @PostMapping("/ezeebill/deposit")
    public String ezeebillDeposit(@ModelAttribute EzeebillDepositCallBackReq req) {
        log.info("ezeebillDeposit Received webhook payload: {}", req);
        return webhookService.ezeebillDepositCallBack(req);
    }

    @WebhookRecord("Ezeebill-withdrawal")
    @PostMapping("/ezeebill/withdrawal")
    public String ezeebillWithdraw(@ModelAttribute EzeebillWithdrawalCallBackReq req) {
        log.info("ezeebillWithdrawal Received webhook payload: {}", req);
        return webhookService.ezeebillWithdrawalCallBack(req);
    }

    @WebhookRecord("CheezeePay")
    @PostMapping("/cheezeePay/deposit")
    public String cheezeepay(@RequestBody String payload, HttpServletResponse response) {
        log.info("cheezeePay received webhook payload: {}", payload);
        return webhookService.cheezeePayDepositCallBack(payload, response);
    }

    @WebhookRecord("CheezeePay-withdraw")
    @PostMapping("/cheezeePay/withdraw")
    public String cheezeePayWithdraw(@RequestBody String payload, HttpServletResponse response) {
        log.info("cheezeePayWithdraw Received webhook payload: {}", payload);
        return webhookService.cheezeePayWithdrawCallBack(payload, response);
    }


    @WebhookRecord("mcPayment-deposit")
    @PostMapping("/mcPayment/deposit")
    public String mcPaymentDeposit(@RequestBody String payload) {
        log.info("mcPaymentDeposit Received webhook payload: {}", payload);
        // 来自mc-payment自身的入金回调,不需要做任何处理
        return "Webhook received";
    }

    @WebhookRecord("mcPayment-withdrawal")
    @PostMapping("/mcPayment/withdrawal")
    public String mcPaymentWithdrawal(@RequestBody String payload) {
        log.info("mcPaymentWithdrawal Received webhook payload: {}", payload);
        // 来自mc-payment自身的出金回调,不需要做任何处理
        return "Webhook received";
    }
}
