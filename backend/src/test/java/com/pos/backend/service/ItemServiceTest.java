package com.pos.backend.service;

import com.pos.backend.domain.Item;
import com.pos.backend.domain.Store;
import com.pos.backend.domain.TaxCategory;
import com.pos.backend.repository.ItemRepository;
import com.pos.backend.repository.StoreRepository;
import com.pos.backend.repository.TaxCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService Unit Tests")
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private TaxCategoryRepository taxCategoryRepository;

    @InjectMocks
    private ItemService itemService;

    private Item sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new Item();
        sampleItem.setId(1L);
        sampleItem.setNumber("ITM-001");
        sampleItem.setDescription("Test Item");
        sampleItem.setPrice(new BigDecimal("9.99"));
        sampleItem.setCost(new BigDecimal("5.00"));
        sampleItem.setQuantity(100);
        sampleItem.setMinQuantity(10);
        sampleItem.setMaxQuantity(500);
        sampleItem.setIsActive(true);
        sampleItem.setIsTaxable(true);
        sampleItem.setBarcode("123456789");
        sampleItem.setCategory("Electronics");
        sampleItem.setBrand("TestBrand");
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all items")
        void shouldReturnAllItems() {
            when(itemRepository.findAll()).thenReturn(Arrays.asList(sampleItem));

            List<Item> result = itemService.findAll();

            assertThat(result).hasSize(1);
            verify(itemRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no items exist")
        void shouldReturnEmptyList() {
            when(itemRepository.findAll()).thenReturn(Collections.emptyList());

            List<Item> result = itemService.findAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return item when found")
        void shouldReturnItemWhenFound() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

            Optional<Item> result = itemService.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getNumber()).isEqualTo("ITM-001");
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(itemRepository.findById(99L)).thenReturn(Optional.empty());

            Optional<Item> result = itemService.findById(99L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByNumber")
    class FindByNumber {

        @Test
        @DisplayName("should return item by number")
        void shouldReturnItemByNumber() {
            when(itemRepository.findByNumber("ITM-001")).thenReturn(Optional.of(sampleItem));

            Optional<Item> result = itemService.findByNumber("ITM-001");

            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("should return items matching keyword")
        void shouldReturnMatchingItems() {
            when(itemRepository.findByDescriptionContainingIgnoreCase("Test"))
                    .thenReturn(List.of(sampleItem));

            List<Item> result = itemService.search("Test");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create item successfully")
        void shouldCreateItemSuccessfully() {
            when(itemRepository.existsByNumber("ITM-001")).thenReturn(false);
            when(itemRepository.existsByBarcode("123456789")).thenReturn(false);
            when(itemRepository.save(any(Item.class))).thenReturn(sampleItem);

            Item result = itemService.create(sampleItem);

            assertThat(result).isNotNull();
            assertThat(result.getNumber()).isEqualTo("ITM-001");
            verify(itemRepository).save(any(Item.class));
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            Item newItem = new Item();
            newItem.setNumber("ITM-002");
            newItem.setDescription("New Item");
            newItem.setPrice(new BigDecimal("5.00"));
            newItem.setIsActive(null);
            newItem.setIsTaxable(null);
            newItem.setQuantity(null);

            when(itemRepository.existsByNumber("ITM-002")).thenReturn(false);
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

            Item result = itemService.create(newItem);

            assertThat(result.getIsActive()).isTrue();
            assertThat(result.getIsTaxable()).isTrue();
            assertThat(result.getQuantity()).isEqualTo(0);
            assertThat(result.getCreatedDate()).isNotNull();
        }

        @Test
        @DisplayName("should throw when duplicate item number")
        void shouldThrowWhenDuplicateNumber() {
            when(itemRepository.existsByNumber("ITM-001")).thenReturn(true);

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item with number 'ITM-001' already exists");
        }

        @Test
        @DisplayName("should throw when duplicate barcode")
        void shouldThrowWhenDuplicateBarcode() {
            when(itemRepository.existsByNumber("ITM-001")).thenReturn(false);
            when(itemRepository.existsByBarcode("123456789")).thenReturn(true);

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item with barcode '123456789' already exists");
        }

        @Test
        @DisplayName("should throw when item is null")
        void shouldThrowWhenItemNull() {
            assertThatThrownBy(() -> itemService.create(null))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should throw when number is missing")
        void shouldThrowWhenNumberMissing() {
            sampleItem.setNumber(null);

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item number is required");
        }

        @Test
        @DisplayName("should throw when description is missing")
        void shouldThrowWhenDescriptionMissing() {
            sampleItem.setDescription(null);

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item description is required");
        }

        @Test
        @DisplayName("should throw when price is null")
        void shouldThrowWhenPriceNull() {
            sampleItem.setPrice(null);

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item price is required");
        }

        @Test
        @DisplayName("should throw when price is negative")
        void shouldThrowWhenPriceNegative() {
            sampleItem.setPrice(new BigDecimal("-1.00"));

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item price cannot be negative");
        }

        @Test
        @DisplayName("should throw when cost is negative")
        void shouldThrowWhenCostNegative() {
            sampleItem.setCost(new BigDecimal("-1.00"));

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item cost cannot be negative");
        }

        @Test
        @DisplayName("should throw when quantity is negative")
        void shouldThrowWhenQuantityNegative() {
            sampleItem.setQuantity(-5);

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item quantity cannot be negative");
        }

        @Test
        @DisplayName("should throw when number exceeds 50 chars")
        void shouldThrowWhenNumberTooLong() {
            sampleItem.setNumber("A".repeat(51));

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item number must not exceed 50 characters");
        }

        @Test
        @DisplayName("should throw when description exceeds 500 chars")
        void shouldThrowWhenDescriptionTooLong() {
            sampleItem.setDescription("A".repeat(501));

            assertThatThrownBy(() -> itemService.create(sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item description must not exceed 500 characters");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update item successfully")
        void shouldUpdateItemSuccessfully() {
            Item updatedItem = new Item();
            updatedItem.setNumber("ITM-001");
            updatedItem.setDescription("Updated Item");
            updatedItem.setPrice(new BigDecimal("19.99"));

            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(itemRepository.findByNumber("ITM-001")).thenReturn(Optional.of(sampleItem));
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

            Item result = itemService.update(1L, updatedItem);

            assertThat(result.getDescription()).isEqualTo("Updated Item");
            assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
        }

        @Test
        @DisplayName("should throw when item not found for update")
        void shouldThrowWhenNotFoundForUpdate() {
            when(itemRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> itemService.update(99L, sampleItem))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item not found with id: 99");
        }

        @Test
        @DisplayName("should throw when updating to duplicate number")
        void shouldThrowWhenUpdatingToDuplicateNumber() {
            Item otherItem = new Item();
            otherItem.setId(2L);
            otherItem.setNumber("ITM-001");

            Item updateData = new Item();
            updateData.setNumber("ITM-001");
            updateData.setDescription("Updated");
            updateData.setPrice(new BigDecimal("5.00"));

            when(itemRepository.findById(2L)).thenReturn(Optional.of(otherItem));
            when(itemRepository.findByNumber("ITM-001")).thenReturn(Optional.of(sampleItem));

            assertThatThrownBy(() -> itemService.update(2L, updateData))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item with number 'ITM-001' already exists");
        }
    }

    @Nested
    @DisplayName("updateQuantity")
    class UpdateQuantity {

        @Test
        @DisplayName("should update quantity successfully")
        void shouldUpdateQuantitySuccessfully() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

            Item result = itemService.updateQuantity(1L, 200);

            assertThat(result.getQuantity()).isEqualTo(200);
        }

        @Test
        @DisplayName("should throw when quantity is negative")
        void shouldThrowWhenNegativeQuantity() {
            assertThatThrownBy(() -> itemService.updateQuantity(1L, -5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantity cannot be negative");
        }

        @Test
        @DisplayName("should throw when item not found")
        void shouldThrowWhenItemNotFound() {
            when(itemRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> itemService.updateQuantity(99L, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item not found with id: 99");
        }
    }

    @Nested
    @DisplayName("adjustQuantity")
    class AdjustQuantity {

        @Test
        @DisplayName("should increase quantity")
        void shouldIncreaseQuantity() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

            Item result = itemService.adjustQuantity(1L, 50);

            assertThat(result.getQuantity()).isEqualTo(150); // 100 + 50
        }

        @Test
        @DisplayName("should decrease quantity")
        void shouldDecreaseQuantity() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

            Item result = itemService.adjustQuantity(1L, -30);

            assertThat(result.getQuantity()).isEqualTo(70); // 100 - 30
        }

        @Test
        @DisplayName("should throw when adjustment results in negative quantity")
        void shouldThrowWhenResultNegative() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

            assertThatThrownBy(() -> itemService.adjustQuantity(1L, -200))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Adjustment would result in negative quantity");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete item successfully")
        void shouldDeleteItemSuccessfully() {
            when(itemRepository.existsById(1L)).thenReturn(true);

            itemService.delete(1L);

            verify(itemRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when item not found for delete")
        void shouldThrowWhenNotFoundForDelete() {
            when(itemRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> itemService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item not found with id: 99");
        }
    }

    @Nested
    @DisplayName("deactivate")
    class Deactivate {

        @Test
        @DisplayName("should deactivate item")
        void shouldDeactivateItem() {
            when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

            Item result = itemService.deactivate(1L);

            assertThat(result.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("should throw when item not found")
        void shouldThrowWhenNotFound() {
            when(itemRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> itemService.deactivate(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item not found with id: 99");
        }
    }

    @Nested
    @DisplayName("createWithIds")
    class CreateWithIds {

        @Test
        @DisplayName("should resolve store and tax category from IDs")
        void shouldResolveRelationships() {
            Store store = new Store();
            store.setId(1L);
            TaxCategory taxCat = new TaxCategory();
            taxCat.setId(1L);

            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(taxCategoryRepository.findById(1L)).thenReturn(Optional.of(taxCat));
            when(itemRepository.existsByNumber("ITM-001")).thenReturn(false);
            when(itemRepository.existsByBarcode("123456789")).thenReturn(false);
            when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

            Item result = itemService.createWithIds(sampleItem, 1L, 1L);

            assertThat(result.getStore()).isEqualTo(store);
            assertThat(result.getTaxCategory()).isEqualTo(taxCat);
        }

        @Test
        @DisplayName("should throw when store not found")
        void shouldThrowWhenStoreNotFound() {
            when(storeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> itemService.createWithIds(sampleItem, 99L, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store not found with id: 99");
        }
    }

    @Nested
    @DisplayName("count operations")
    class CountOperations {

        @Test
        @DisplayName("should return total count")
        void shouldReturnTotalCount() {
            when(itemRepository.count()).thenReturn(10L);

            assertThat(itemService.count()).isEqualTo(10L);
        }

        @Test
        @DisplayName("should return active count")
        void shouldReturnActiveCount() {
            when(itemRepository.countByIsActive(true)).thenReturn(8L);

            assertThat(itemService.countActive()).isEqualTo(8L);
        }
    }
}
