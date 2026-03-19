package com.pos.backend.service;

import com.pos.backend.domain.Item;
import com.pos.backend.domain.Sale;
import com.pos.backend.domain.SaleLineItem;
import com.pos.backend.repository.ItemRepository;
import com.pos.backend.repository.SaleLineItemRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleLineItemService Unit Tests")
class SaleLineItemServiceTest {

    @Mock
    private SaleLineItemRepository saleLineItemRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private SaleLineItemService saleLineItemService;

    private SaleLineItem sampleLineItem;
    private Sale sampleSale;
    private Item sampleItem;

    @BeforeEach
    void setUp() {
        sampleSale = new Sale();
        sampleSale.setId(1L);

        sampleItem = new Item();
        sampleItem.setId(1L);
        sampleItem.setNumber("1001");
        sampleItem.setDescription("Turkey Sandwich");
        sampleItem.setPrice(new BigDecimal("2.59"));

        sampleLineItem = new SaleLineItem();
        sampleLineItem.setId(1L);
        sampleLineItem.setSale(sampleSale);
        sampleLineItem.setItem(sampleItem);
        sampleLineItem.setQuantity(2);
        sampleLineItem.setUnitPrice(new BigDecimal("2.59"));
        sampleLineItem.setExtendedPrice(new BigDecimal("5.18"));
        sampleLineItem.setTaxRate(new BigDecimal("0.0700"));
        sampleLineItem.setTaxAmount(new BigDecimal("0.36"));
        sampleLineItem.setTotalPrice(new BigDecimal("5.54"));
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all line items")
        void shouldReturnAll() {
            when(saleLineItemRepository.findAll()).thenReturn(List.of(sampleLineItem));
            assertThat(saleLineItemService.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when none exist")
        void shouldReturnEmpty() {
            when(saleLineItemRepository.findAll()).thenReturn(Collections.emptyList());
            assertThat(saleLineItemService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return line item when found")
        void shouldReturnWhenFound() {
            when(saleLineItemRepository.findById(1L)).thenReturn(Optional.of(sampleLineItem));
            assertThat(saleLineItemService.findById(1L)).isPresent();
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(saleLineItemRepository.findById(99L)).thenReturn(Optional.empty());
            assertThat(saleLineItemService.findById(99L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByQueries")
    class FindByQueries {

        @Test
        @DisplayName("should find by sale")
        void shouldFindBySale() {
            when(saleLineItemRepository.findBySale_Id(1L)).thenReturn(List.of(sampleLineItem));
            assertThat(saleLineItemService.findBySale(1L)).hasSize(1);
        }

        @Test
        @DisplayName("should find by item")
        void shouldFindByItem() {
            when(saleLineItemRepository.findByItem_Id(1L)).thenReturn(List.of(sampleLineItem));
            assertThat(saleLineItemService.findByItem(1L)).hasSize(1);
        }

        @Test
        @DisplayName("should get total quantity sold for item")
        void shouldGetTotalQuantity() {
            when(saleLineItemRepository.getTotalQuantitySoldForItem(1L)).thenReturn(25);
            assertThat(saleLineItemService.getTotalQuantitySoldForItem(1L)).isEqualTo(25);
        }

        @Test
        @DisplayName("should return zero when no quantity sold")
        void shouldReturnZeroWhenNoQuantity() {
            when(saleLineItemRepository.getTotalQuantitySoldForItem(99L)).thenReturn(null);
            assertThat(saleLineItemService.getTotalQuantitySoldForItem(99L)).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create line item and calculate prices")
        void shouldCreateAndCalculate() {
            SaleLineItem newItem = new SaleLineItem();
            newItem.setSale(sampleSale);
            newItem.setItem(sampleItem);
            newItem.setQuantity(3);
            newItem.setUnitPrice(new BigDecimal("2.59"));
            newItem.setTaxRate(new BigDecimal("0.0700"));

            when(saleLineItemRepository.save(any(SaleLineItem.class))).thenAnswer(inv -> inv.getArgument(0));

            SaleLineItem result = saleLineItemService.create(newItem);

            assertThat(result.getExtendedPrice()).isEqualByComparingTo(new BigDecimal("7.77"));
            assertThat(result.getTaxAmount()).isNotNull();
            assertThat(result.getTotalPrice()).isNotNull();
        }

        @Test
        @DisplayName("should calculate zero tax when no tax rate")
        void shouldCalculateZeroTaxWhenNoRate() {
            SaleLineItem newItem = new SaleLineItem();
            newItem.setSale(sampleSale);
            newItem.setItem(sampleItem);
            newItem.setQuantity(1);
            newItem.setUnitPrice(new BigDecimal("5.00"));
            newItem.setTaxRate(null);

            when(saleLineItemRepository.save(any(SaleLineItem.class))).thenAnswer(inv -> inv.getArgument(0));

            SaleLineItem result = saleLineItemService.create(newItem);

            assertThat(result.getExtendedPrice()).isEqualByComparingTo(new BigDecimal("5.00"));
            assertThat(result.getTaxAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("5.00"));
        }

        @Test
        @DisplayName("should throw when null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> saleLineItemService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw when sale is null")
        void shouldThrowWhenSaleNull() {
            sampleLineItem.setSale(null);
            assertThatThrownBy(() -> saleLineItemService.create(sampleLineItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale is required");
        }

        @Test
        @DisplayName("should throw when item is null")
        void shouldThrowWhenItemNull() {
            sampleLineItem.setItem(null);
            assertThatThrownBy(() -> saleLineItemService.create(sampleLineItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item is required");
        }

        @Test
        @DisplayName("should throw when quantity is zero")
        void shouldThrowWhenQuantityZero() {
            sampleLineItem.setQuantity(0);
            assertThatThrownBy(() -> saleLineItemService.create(sampleLineItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantity must be greater than zero");
        }

        @Test
        @DisplayName("should throw when quantity is negative")
        void shouldThrowWhenQuantityNegative() {
            sampleLineItem.setQuantity(-1);
            assertThatThrownBy(() -> saleLineItemService.create(sampleLineItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantity must be greater than zero");
        }

        @Test
        @DisplayName("should throw when unit price is negative")
        void shouldThrowWhenPriceNegative() {
            sampleLineItem.setUnitPrice(new BigDecimal("-1.00"));
            assertThatThrownBy(() -> saleLineItemService.create(sampleLineItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unit price must be non-negative");
        }
    }

    @Nested
    @DisplayName("createWithIds")
    class CreateWithIds {

        @Test
        @DisplayName("should resolve sale and item from IDs")
        void shouldResolveRelationships() {
            SaleLineItem newItem = new SaleLineItem();
            newItem.setQuantity(1);
            newItem.setUnitPrice(new BigDecimal("2.59"));

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sampleSale));
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(saleLineItemRepository.save(any(SaleLineItem.class))).thenAnswer(inv -> inv.getArgument(0));

            SaleLineItem result = saleLineItemService.createWithIds(newItem, 1L, 1L);

            assertThat(result.getSale()).isEqualTo(sampleSale);
            assertThat(result.getItem()).isEqualTo(sampleItem);
        }

        @Test
        @DisplayName("should throw when sale not found")
        void shouldThrowWhenSaleNotFound() {
            SaleLineItem newItem = new SaleLineItem();
            newItem.setQuantity(1);
            newItem.setUnitPrice(new BigDecimal("2.59"));

            when(saleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleLineItemService.createWithIds(newItem, 99L, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale not found");
        }

        @Test
        @DisplayName("should throw when item not found")
        void shouldThrowWhenItemNotFound() {
            SaleLineItem newItem = new SaleLineItem();
            newItem.setQuantity(1);
            newItem.setUnitPrice(new BigDecimal("2.59"));

            when(saleRepository.findById(1L)).thenReturn(Optional.of(sampleSale));
            when(itemRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleLineItemService.createWithIds(newItem, 1L, 99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item not found");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update line item and recalculate prices")
        void shouldUpdateAndRecalculate() {
            SaleLineItem updated = new SaleLineItem();
            updated.setSale(sampleSale);
            updated.setItem(sampleItem);
            updated.setQuantity(5);
            updated.setUnitPrice(new BigDecimal("3.00"));
            updated.setTaxRate(new BigDecimal("0.0700"));

            when(saleLineItemRepository.findById(1L)).thenReturn(Optional.of(sampleLineItem));
            when(saleLineItemRepository.save(any(SaleLineItem.class))).thenAnswer(inv -> inv.getArgument(0));

            SaleLineItem result = saleLineItemService.update(1L, updated);

            assertThat(result.getExtendedPrice()).isEqualByComparingTo(new BigDecimal("15.00"));
            assertThat(result.getTaxAmount()).isNotNull();
            assertThat(result.getTotalPrice()).isNotNull();
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(saleLineItemRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleLineItemService.update(99L, sampleLineItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale line item not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete line item")
        void shouldDelete() {
            when(saleLineItemRepository.existsById(1L)).thenReturn(true);
            saleLineItemService.delete(1L);
            verify(saleLineItemRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(saleLineItemRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> saleLineItemService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Sale line item not found");
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldCount() {
            when(saleLineItemRepository.count()).thenReturn(20L);
            assertThat(saleLineItemService.count()).isEqualTo(20);
        }
    }
}
