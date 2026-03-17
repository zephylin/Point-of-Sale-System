package com.pos.backend.service;

import com.pos.backend.domain.Payment;
import com.pos.backend.domain.Sale;
import com.pos.backend.repository.PaymentRepository;
import com.pos.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;

    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Payment> findBySale(Long saleId) {
        return paymentRepository.findBySale_Id(saleId);
    }

    @Transactional(readOnly = true)
    public List<Payment> findByPaymentType(String paymentType) {
        return paymentRepository.findByPaymentType(paymentType);
    }

    @Transactional(readOnly = true)
    public List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentsBySale(Long saleId) {
        BigDecimal total = paymentRepository.getTotalPaymentsBySale(saleId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCashBySession(Long sessionId) {
        BigDecimal total = paymentRepository.getTotalCashBySession(sessionId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Payment create(Payment payment) {
        validatePayment(payment);
        if (payment.getPaymentDateTime() == null) {
            payment.setPaymentDateTime(LocalDateTime.now());
        }
        if (payment.getCountAsCash() == null) {
            payment.setCountAsCash("CASH".equals(payment.getPaymentType()));
        }

        // Calculate change for cash payments
        if ("CASH".equals(payment.getPaymentType())
                && payment.getAmountTendered() != null
                && payment.getAmount() != null) {
            payment.setChangeDue(
                    payment.getAmountTendered().subtract(payment.getAmount()).max(BigDecimal.ZERO));
        }

        // Auto-authorize credit/check payments (simplified for now)
        if ("CREDIT".equals(payment.getPaymentType()) || "CHECK".equals(payment.getPaymentType())) {
            if (payment.getAuthorized() == null) {
                payment.setAuthorized(true);
                payment.setAuthorizationCode("AUTH-" + System.currentTimeMillis());
            }
        }

        Payment saved = paymentRepository.save(payment);
        log.info("Created {} payment (id={}) for sale {}", saved.getPaymentType(), saved.getId(),
                saved.getSale().getId());
        return saved;
    }

    /**
     * Create a payment, resolving the sale from its ID.
     */
    public Payment createWithSaleId(Payment payment, Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found with id: " + saleId));
        payment.setSale(sale);
        return create(payment);
    }

    public Payment update(Long id, Payment payment) {
        Payment existing = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));
        validatePayment(payment);

        existing.setAmount(payment.getAmount());
        existing.setAmountTendered(payment.getAmountTendered());
        existing.setChangeDue(payment.getChangeDue());
        existing.setPaymentType(payment.getPaymentType());
        existing.setCountAsCash(payment.getCountAsCash());
        existing.setAuthorizationCode(payment.getAuthorizationCode());
        existing.setAuthorized(payment.getAuthorized());
        existing.setCardType(payment.getCardType());
        existing.setCardLastFour(payment.getCardLastFour());
        existing.setExpirationDate(payment.getExpirationDate());
        existing.setRoutingNumber(payment.getRoutingNumber());
        existing.setAccountNumber(payment.getAccountNumber());
        existing.setCheckNumber(payment.getCheckNumber());

        return paymentRepository.save(existing);
    }

    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new IllegalArgumentException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return paymentRepository.count();
    }

    @Transactional(readOnly = true)
    public long countBySale(Long saleId) {
        return paymentRepository.countBySale_Id(saleId);
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        if (payment.getSale() == null) {
            throw new IllegalArgumentException("Sale is required");
        }
        if (payment.getPaymentType() == null || payment.getPaymentType().isBlank()) {
            throw new IllegalArgumentException("Payment type is required");
        }
        if (!List.of("CASH", "CREDIT", "CHECK").contains(payment.getPaymentType())) {
            throw new IllegalArgumentException("Payment type must be CASH, CREDIT, or CHECK");
        }
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Type-specific validation
        if ("CASH".equals(payment.getPaymentType())) {
            if (payment.getAmountTendered() == null
                    || payment.getAmountTendered().compareTo(payment.getAmount()) < 0) {
                throw new IllegalArgumentException("Amount tendered must be at least the payment amount for cash");
            }
        }
        if ("CREDIT".equals(payment.getPaymentType())) {
            if (payment.getCardLastFour() == null || payment.getCardLastFour().length() != 4) {
                throw new IllegalArgumentException("Card last four digits are required for credit payments");
            }
            if (payment.getExpirationDate() == null) {
                throw new IllegalArgumentException("Expiration date is required for credit payments");
            }
        }
        if ("CHECK".equals(payment.getPaymentType())) {
            if (payment.getCheckNumber() == null || payment.getCheckNumber().isBlank()) {
                throw new IllegalArgumentException("Check number is required for check payments");
            }
        }
    }
}
