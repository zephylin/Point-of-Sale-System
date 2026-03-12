package com.pos.backend.service;

import com.pos.backend.domain.Cashier;
import com.pos.backend.domain.Sale;
import com.pos.backend.domain.Session;
import com.pos.backend.domain.Store;
import com.pos.backend.repository.CashierRepository;
import com.pos.backend.repository.SaleRepository;
import com.pos.backend.repository.SessionRepository;
import com.pos.backend.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleService Unit Tests")
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CashierRepository cashierRepository;

    @InjectMocks
    private SaleService saleService;

    private Sale sampleSale;
    private Session sampleSession;
    private Store sampleStore;
    private Cashier sampleCashier;

    @BeforeEach
    void setUp() {
        sampleSession = new Session();
        sampleSession.setId(1L);
        sampleStore = new Store();
        sampleStore.setId(1L);
        sampleCashier = new Cashier();
        sampleCashier.setId(1L);

        sampleSale = new Sale();
        sampleSale.setId(1L);
        sampleSale.setSession(sampleSession);
        sampleSale.setStore(sampleStore);
        sampleSale.setCashier(sampleCashier);
        sampleSale.setDateTime(LocalDateTime.now());
        sampleSale.setSubtotal(new BigDecimal("100.00"));
        sampleSale.setTax(new BigDecimal("8.00"));
        sampleSale.setTotal(new BigDecimal("108.00"));
        sampleSale.setStatus("PENDING");
        sampleSale.setTaxFree(false);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all sales")
        void shouldReturnAllSales() {
            when(saleRepository.findAll()).thenReturn(List.of(sampleSale));

            List<Sale> result = saleService.findAll();

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return sale when found")
        void shouldReturnSaleWhenFound() {
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sampleSale));

            Optional<Sale> result = saleService.findById(1L);

            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create sale successfully")
        void shouldCreateSaleSuccessfully() {
            when(saleRepository.save(any(Sale.class))).thenReturn(sampleSale);

            Sale result = saleService.create(sampleSale);

            assertThat(result).isNotNull();
            verify(saleRepository).save(any(Sale.class));
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            sampleSale.setDateTime(null);
            sampleSale.setStatus(null);
            sampleSale.setTaxFree(null);

            when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

            Sale result = saleService.create(sampleSale);

            assertThat(result.getDateTime()).isNotNull();
            assertThat(result.getStatus()).isEqualTo("PENDING");
            assertThat(result.getTaxFree()).isFalse();
        }

        @Test
        @DisplayName("should throw when sale is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> saleService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale cannot be null");
        }

        @Test
        @DisplayName("should throw when session is missing")
        void shouldThrowWhenSessionMissing() {
            sampleSale.setSession(null);

            assertThatThrownBy(() -> saleService.create(sampleSale))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Session is required");
        }

        @Test
        @DisplayName("should throw when store is missing")
        void shouldThrowWhenStoreMissing() {
            sampleSale.setStore(null);

            assertThatThrownBy(() -> saleService.create(sampleSale))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store is required");
        }

        @Test
        @DisplayName("should throw when cashier is missing")
        void shouldThrowWhenCashierMissing() {
            sampleSale.setCashier(null);

            assertThatThrownBy(() -> saleService.create(sampleSale))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cashier is required");
        }
    }

    @Nested
    @DisplayName("createWithIds")
    class CreateWithIds {

        @Test
        @DisplayName("should resolve relationships from IDs")
        void shouldResolveRelationships() {
            Sale newSale = new Sale();
            newSale.setSession(sampleSession);  // will be overridden
            newSale.setStore(sampleStore);
            newSale.setCashier(sampleCashier);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(sampleSession));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(sampleStore));
            when(cashierRepository.findById(1L)).thenReturn(Optional.of(sampleCashier));
            when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

            Sale result = saleService.createWithIds(newSale, 1L, 1L, 1L);

            assertThat(result.getSession()).isEqualTo(sampleSession);
            assertThat(result.getStore()).isEqualTo(sampleStore);
            assertThat(result.getCashier()).isEqualTo(sampleCashier);
        }

        @Test
        @DisplayName("should throw when session not found")
        void shouldThrowWhenSessionNotFound() {
            Sale newSale = new Sale();

            when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.createWithIds(newSale, 99L, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Session not found with id: 99");
        }
    }

    @Nested
    @DisplayName("completeSale")
    class CompleteSale {

        @Test
        @DisplayName("should complete pending sale")
        void shouldCompletePendingSale() {
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sampleSale));
            when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

            Sale result = saleService.completeSale(1L, new BigDecimal("120.00"), "CASH");

            assertThat(result.getStatus()).isEqualTo("COMPLETED");
            assertThat(result.getAmountPaid()).isEqualByComparingTo(new BigDecimal("120.00"));
            assertThat(result.getPaymentMethod()).isEqualTo("CASH");
            assertThat(result.getChange()).isEqualByComparingTo(new BigDecimal("12.00")); // 120 - 108
        }

        @Test
        @DisplayName("should throw when sale not found")
        void shouldThrowWhenNotFound() {
            when(saleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.completeSale(99L, BigDecimal.TEN, "CASH"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale not found with id: 99");
        }

        @Test
        @DisplayName("should throw when sale is not pending")
        void shouldThrowWhenNotPending() {
            sampleSale.setStatus("COMPLETED");
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sampleSale));

            assertThatThrownBy(() -> saleService.completeSale(1L, BigDecimal.TEN, "CASH"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Only pending sales can be completed");
        }
    }

    @Nested
    @DisplayName("voidSale")
    class VoidSale {

        @Test
        @DisplayName("should void sale with reason")
        void shouldVoidSaleWithReason() {
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sampleSale));
            when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

            Sale result = saleService.voidSale(1L, "Customer changed mind");

            assertThat(result.getStatus()).isEqualTo("VOID");
            assertThat(result.getNotes()).isEqualTo("Customer changed mind");
        }

        @Test
        @DisplayName("should throw when sale not found")
        void shouldThrowWhenNotFound() {
            when(saleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.voidSale(99L, "reason"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale not found with id: 99");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete sale")
        void shouldDeleteSale() {
            when(saleRepository.existsById(1L)).thenReturn(true);

            saleService.delete(1L);

            verify(saleRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when sale not found")
        void shouldThrowWhenNotFound() {
            when(saleRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> saleService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale not found with id: 99");
        }
    }

    @Nested
    @DisplayName("getTotalSales")
    class GetTotalSales {

        @Test
        @DisplayName("should return total sales by store")
        void shouldReturnTotalByStore() {
            when(saleRepository.getTotalSalesByStore(1L)).thenReturn(new BigDecimal("500.00"));

            BigDecimal result = saleService.getTotalSalesByStore(1L);

            assertThat(result).isEqualByComparingTo(new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("should return zero when no sales for store")
        void shouldReturnZeroWhenNoSales() {
            when(saleRepository.getTotalSalesByStore(1L)).thenReturn(null);

            BigDecimal result = saleService.getTotalSalesByStore(1L);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should return total sales by session")
        void shouldReturnTotalBySession() {
            when(saleRepository.getTotalSalesBySession(1L)).thenReturn(new BigDecimal("250.00"));

            BigDecimal result = saleService.getTotalSalesBySession(1L);

            assertThat(result).isEqualByComparingTo(new BigDecimal("250.00"));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update sale successfully")
        void shouldUpdateSaleSuccessfully() {
            Sale updatedSale = new Sale();
            updatedSale.setSession(sampleSession);
            updatedSale.setStore(sampleStore);
            updatedSale.setCashier(sampleCashier);
            updatedSale.setSubtotal(new BigDecimal("200.00"));
            updatedSale.setTax(new BigDecimal("16.00"));
            updatedSale.setTotal(new BigDecimal("216.00"));
            updatedSale.setStatus("PENDING");
            updatedSale.setTaxFree(false);

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sampleSale));
            when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

            Sale result = saleService.update(1L, updatedSale);

            assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("200.00"));
            assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("216.00"));
        }

        @Test
        @DisplayName("should throw when sale not found for update")
        void shouldThrowWhenNotFoundForUpdate() {
            when(saleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.update(99L, sampleSale))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale not found with id: 99");
        }
    }
}
