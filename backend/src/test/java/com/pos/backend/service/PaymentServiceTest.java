package com.pos.backend.service;

import com.pos.backend.domain.Payment;
import com.pos.backend.domain.Sale;
import com.pos.backend.repository.PaymentRepository;
import com.pos.backend.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment sampleCashPayment;
    private Payment sampleCreditPayment;
    private Payment sampleCheckPayment;
    private Sale sampleSale;

    @BeforeEach
    void setUp() {
        sampleSale = new Sale();
        sampleSale.setId(1L);

        sampleCashPayment = new Payment();
        sampleCashPayment.setId(1L);
        sampleCashPayment.setSale(sampleSale);
        sampleCashPayment.setPaymentType("CASH");
        sampleCashPayment.setAmount(new BigDecimal("10.00"));
        sampleCashPayment.setAmountTendered(new BigDecimal("20.00"));
        sampleCashPayment.setPaymentDateTime(LocalDateTime.now());

        sampleCreditPayment = new Payment();
        sampleCreditPayment.setId(2L);
        sampleCreditPayment.setSale(sampleSale);
        sampleCreditPayment.setPaymentType("CREDIT");
        sampleCreditPayment.setAmount(new BigDecimal("25.00"));
        sampleCreditPayment.setCardLastFour("5550");
        sampleCreditPayment.setExpirationDate(LocalDate.of(2027, 10, 1));
        sampleCreditPayment.setCardType("Visa");
        sampleCreditPayment.setPaymentDateTime(LocalDateTime.now());

        sampleCheckPayment = new Payment();
        sampleCheckPayment.setId(3L);
        sampleCheckPayment.setSale(sampleSale);
        sampleCheckPayment.setPaymentType("CHECK");
        sampleCheckPayment.setAmount(new BigDecimal("15.00"));
        sampleCheckPayment.setCheckNumber("1001");
        sampleCheckPayment.setRoutingNumber("111111111");
        sampleCheckPayment.setAccountNumber("9999999");
        sampleCheckPayment.setPaymentDateTime(LocalDateTime.now());
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all payments")
        void shouldReturnAll() {
            when(paymentRepository.findAll()).thenReturn(List.of(sampleCashPayment, sampleCreditPayment));
            assertThat(paymentService.findAll()).hasSize(2);
        }

        @Test
        @DisplayName("should return empty list when none exist")
        void shouldReturnEmpty() {
            when(paymentRepository.findAll()).thenReturn(Collections.emptyList());
            assertThat(paymentService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return payment when found")
        void shouldReturnWhenFound() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(sampleCashPayment));
            assertThat(paymentService.findById(1L)).isPresent();
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
            assertThat(paymentService.findById(99L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByQueries")
    class FindByQueries {

        @Test
        @DisplayName("should find by sale")
        void shouldFindBySale() {
            when(paymentRepository.findBySale_Id(1L)).thenReturn(List.of(sampleCashPayment));
            assertThat(paymentService.findBySale(1L)).hasSize(1);
        }

        @Test
        @DisplayName("should find by payment type")
        void shouldFindByType() {
            when(paymentRepository.findByPaymentType("CASH")).thenReturn(List.of(sampleCashPayment));
            assertThat(paymentService.findByPaymentType("CASH")).hasSize(1);
        }

        @Test
        @DisplayName("should find by date range")
        void shouldFindByDateRange() {
            LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);
            when(paymentRepository.findByDateRange(start, end)).thenReturn(List.of(sampleCashPayment));
            assertThat(paymentService.findByDateRange(start, end)).hasSize(1);
        }
    }

    @Nested
    @DisplayName("aggregations")
    class Aggregations {

        @Test
        @DisplayName("should return total payments by sale")
        void shouldGetTotalBySale() {
            when(paymentRepository.getTotalPaymentsBySale(1L)).thenReturn(new BigDecimal("35.00"));
            assertThat(paymentService.getTotalPaymentsBySale(1L)).isEqualByComparingTo(new BigDecimal("35.00"));
        }

        @Test
        @DisplayName("should return zero when no payments for sale")
        void shouldReturnZeroWhenNone() {
            when(paymentRepository.getTotalPaymentsBySale(99L)).thenReturn(null);
            assertThat(paymentService.getTotalPaymentsBySale(99L)).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should return total cash by session")
        void shouldGetTotalCashBySession() {
            when(paymentRepository.getTotalCashBySession(1L)).thenReturn(new BigDecimal("100.00"));
            assertThat(paymentService.getTotalCashBySession(1L)).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("should return zero when no cash for session")
        void shouldReturnZeroCashWhenNone() {
            when(paymentRepository.getTotalCashBySession(99L)).thenReturn(null);
            assertThat(paymentService.getTotalCashBySession(99L)).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("create cash payment")
    class CreateCash {

        @Test
        @DisplayName("should create cash payment and calculate change")
        void shouldCreateAndCalculateChange() {
            sampleCashPayment.setCountAsCash(null);
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            Payment result = paymentService.create(sampleCashPayment);

            assertThat(result.getChangeDue()).isEqualByComparingTo(new BigDecimal("10.00"));
            assertThat(result.getCountAsCash()).isTrue();
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            sampleCashPayment.setPaymentDateTime(null);
            sampleCashPayment.setCountAsCash(null);

            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            Payment result = paymentService.create(sampleCashPayment);

            assertThat(result.getPaymentDateTime()).isNotNull();
            assertThat(result.getCountAsCash()).isTrue();
        }

        @Test
        @DisplayName("should throw when amount tendered is less than amount")
        void shouldThrowWhenUnderpaid() {
            sampleCashPayment.setAmountTendered(new BigDecimal("5.00"));

            assertThatThrownBy(() -> paymentService.create(sampleCashPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least the payment amount");
        }
    }

    @Nested
    @DisplayName("create credit payment")
    class CreateCredit {

        @Test
        @DisplayName("should create credit payment with auto-authorization")
        void shouldCreateAndAutoAuthorize() {
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            Payment result = paymentService.create(sampleCreditPayment);

            assertThat(result.getAuthorized()).isTrue();
            assertThat(result.getAuthorizationCode()).startsWith("AUTH-");
        }

        @Test
        @DisplayName("should throw when card last four missing")
        void shouldThrowWhenCardMissing() {
            sampleCreditPayment.setCardLastFour(null);

            assertThatThrownBy(() -> paymentService.create(sampleCreditPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Card last four");
        }

        @Test
        @DisplayName("should throw when card last four wrong length")
        void shouldThrowWhenCardWrongLength() {
            sampleCreditPayment.setCardLastFour("123");

            assertThatThrownBy(() -> paymentService.create(sampleCreditPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Card last four");
        }

        @Test
        @DisplayName("should throw when expiration date missing")
        void shouldThrowWhenExpirationMissing() {
            sampleCreditPayment.setExpirationDate(null);

            assertThatThrownBy(() -> paymentService.create(sampleCreditPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Expiration date");
        }
    }

    @Nested
    @DisplayName("create check payment")
    class CreateCheck {

        @Test
        @DisplayName("should create check payment with auto-authorization")
        void shouldCreateAndAutoAuthorize() {
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            Payment result = paymentService.create(sampleCheckPayment);

            assertThat(result.getAuthorized()).isTrue();
            assertThat(result.getAuthorizationCode()).startsWith("AUTH-");
        }

        @Test
        @DisplayName("should throw when check number missing")
        void shouldThrowWhenCheckNumberMissing() {
            sampleCheckPayment.setCheckNumber(null);

            assertThatThrownBy(() -> paymentService.create(sampleCheckPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Check number");
        }
    }

    @Nested
    @DisplayName("create validation")
    class CreateValidation {

        @Test
        @DisplayName("should throw when payment is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> paymentService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw when sale is null")
        void shouldThrowWhenSaleNull() {
            sampleCashPayment.setSale(null);
            assertThatThrownBy(() -> paymentService.create(sampleCashPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale is required");
        }

        @Test
        @DisplayName("should throw when payment type is null")
        void shouldThrowWhenTypeNull() {
            sampleCashPayment.setPaymentType(null);
            assertThatThrownBy(() -> paymentService.create(sampleCashPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Payment type is required");
        }

        @Test
        @DisplayName("should throw when payment type is invalid")
        void shouldThrowWhenTypeInvalid() {
            sampleCashPayment.setPaymentType("BITCOIN");
            assertThatThrownBy(() -> paymentService.create(sampleCashPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CASH, CREDIT, or CHECK");
        }

        @Test
        @DisplayName("should throw when amount is zero")
        void shouldThrowWhenAmountZero() {
            sampleCashPayment.setAmount(BigDecimal.ZERO);
            assertThatThrownBy(() -> paymentService.create(sampleCashPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Amount must be positive");
        }

        @Test
        @DisplayName("should throw when amount is negative")
        void shouldThrowWhenAmountNegative() {
            sampleCashPayment.setAmount(new BigDecimal("-5.00"));
            assertThatThrownBy(() -> paymentService.create(sampleCashPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Amount must be positive");
        }
    }

    @Nested
    @DisplayName("createWithSaleId")
    class CreateWithSaleId {

        @Test
        @DisplayName("should resolve sale from ID")
        void shouldResolveSale() {
            Payment newPayment = new Payment();
            newPayment.setPaymentType("CASH");
            newPayment.setAmount(new BigDecimal("10.00"));
            newPayment.setAmountTendered(new BigDecimal("10.00"));

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sampleSale));
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            Payment result = paymentService.createWithSaleId(newPayment, 1L);

            assertThat(result.getSale()).isEqualTo(sampleSale);
        }

        @Test
        @DisplayName("should throw when sale not found")
        void shouldThrowWhenSaleNotFound() {
            Payment newPayment = new Payment();
            newPayment.setPaymentType("CASH");
            newPayment.setAmount(new BigDecimal("10.00"));

            when(saleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.createWithSaleId(newPayment, 99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale not found");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update payment successfully")
        void shouldUpdate() {
            Payment updated = new Payment();
            updated.setSale(sampleSale);
            updated.setPaymentType("CASH");
            updated.setAmount(new BigDecimal("15.00"));
            updated.setAmountTendered(new BigDecimal("20.00"));

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(sampleCashPayment));
            when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

            Payment result = paymentService.update(1L, updated);

            assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("15.00"));
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.update(99L, sampleCashPayment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Payment not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete payment")
        void shouldDelete() {
            when(paymentRepository.existsById(1L)).thenReturn(true);
            paymentService.delete(1L);
            verify(paymentRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(paymentRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> paymentService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Payment not found");
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldCount() {
            when(paymentRepository.count()).thenReturn(10L);
            assertThat(paymentService.count()).isEqualTo(10);
        }

        @Test
        @DisplayName("should return count by sale")
        void shouldCountBySale() {
            when(paymentRepository.countBySale_Id(1L)).thenReturn(2L);
            assertThat(paymentService.countBySale(1L)).isEqualTo(2);
        }
    }
}
