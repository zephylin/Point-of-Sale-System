package com.pos.backend.mapper;

import com.pos.backend.domain.Payment;
import com.pos.backend.dto.PaymentDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Payment entity and PaymentDTO.
 */
@Component
public class PaymentMapper {

    public Payment toEntity(PaymentDTO.Request request) {
        Payment payment = new Payment();
        // sale is set by the service layer
        payment.setPaymentType(request.getPaymentType());
        payment.setAmount(request.getAmount());
        payment.setAmountTendered(request.getAmountTendered());

        switch (request.getPaymentType()) {
            case "CASH":
                payment.setCountAsCash(true);
                break;
            case "CREDIT":
                payment.setCountAsCash(false);
                payment.setCardType(request.getCardType());
                payment.setCardLastFour(request.getCardLastFour());
                payment.setExpirationDate(request.getExpirationDate());
                break;
            case "CHECK":
                payment.setCountAsCash(false);
                payment.setRoutingNumber(request.getRoutingNumber());
                payment.setAccountNumber(request.getAccountNumber());
                payment.setCheckNumber(request.getCheckNumber());
                break;
        }

        return payment;
    }

    public PaymentDTO.Response toResponse(Payment payment) {
        return PaymentDTO.Response.builder()
                .id(payment.getId())
                .saleId(payment.getSale() != null ? payment.getSale().getId() : null)
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .amountTendered(payment.getAmountTendered())
                .changeDue(payment.getChangeDue())
                .countAsCash(payment.getCountAsCash())
                .paymentDateTime(payment.getPaymentDateTime())
                .authorizationCode(payment.getAuthorizationCode())
                .authorized(payment.getAuthorized())
                .cardType(payment.getCardType())
                .cardLastFour(payment.getCardLastFour())
                .expirationDate(payment.getExpirationDate())
                .routingNumber(payment.getRoutingNumber())
                .accountNumber(payment.getAccountNumber())
                .checkNumber(payment.getCheckNumber())
                .build();
    }
}
