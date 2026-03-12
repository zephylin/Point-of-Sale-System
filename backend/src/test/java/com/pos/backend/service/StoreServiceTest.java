package com.pos.backend.service;

import com.pos.backend.domain.Store;
import com.pos.backend.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreService Unit Tests")
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    private Store sampleStore;

    @BeforeEach
    void setUp() {
        sampleStore = new Store();
        sampleStore.setId(1L);
        sampleStore.setNumber("S001");
        sampleStore.setName("Downtown Store");
        sampleStore.setAddress("123 Main St");
        sampleStore.setCity("Springfield");
        sampleStore.setState("IL");
        sampleStore.setZip("62701");
        sampleStore.setPhone("555-0100");
        sampleStore.setEmail("downtown@store.com");
        sampleStore.setIsActive(true);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all stores")
        void shouldReturnAllStores() {
            when(storeRepository.findAll()).thenReturn(List.of(sampleStore));

            List<Store> result = storeService.findAll();

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return store when found")
        void shouldReturnStoreWhenFound() {
            when(storeRepository.findById(1L)).thenReturn(Optional.of(sampleStore));

            Optional<Store> result = storeService.findById(1L);

            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("findByQueries")
    class FindByQueries {

        @Test
        @DisplayName("should find store by number")
        void shouldFindByNumber() {
            when(storeRepository.findByNumber("S001")).thenReturn(Optional.of(sampleStore));

            Optional<Store> result = storeService.findByNumber("S001");

            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("should search by name")
        void shouldSearchByName() {
            when(storeRepository.findByNameContainingIgnoreCase("Downtown")).thenReturn(List.of(sampleStore));

            List<Store> result = storeService.searchByName("Downtown");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should find by city")
        void shouldFindByCity() {
            when(storeRepository.findByCity("Springfield")).thenReturn(List.of(sampleStore));

            List<Store> result = storeService.findByCity("Springfield");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should find by state with uppercase normalization")
        void shouldFindByState() {
            when(storeRepository.findByState("IL")).thenReturn(List.of(sampleStore));

            List<Store> result = storeService.findByState("il");

            assertThat(result).hasSize(1);
            verify(storeRepository).findByState("IL");
        }

        @Test
        @DisplayName("should find all active stores")
        void shouldFindAllActive() {
            when(storeRepository.findByIsActive(true)).thenReturn(List.of(sampleStore));

            List<Store> result = storeService.findAllActive();

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should find by manager")
        void shouldFindByManager() {
            when(storeRepository.findByManagerContainingIgnoreCase("John")).thenReturn(List.of(sampleStore));

            List<Store> result = storeService.findByManager("John");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create store successfully")
        void shouldCreateStoreSuccessfully() {
            when(storeRepository.existsByNumber("S001")).thenReturn(false);
            when(storeRepository.save(any(Store.class))).thenAnswer(inv -> inv.getArgument(0));

            Store result = storeService.create(sampleStore);

            assertThat(result).isNotNull();
            assertThat(result.getState()).isEqualTo("IL");
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            sampleStore.setIsActive(null);
            sampleStore.setOpenedDate(null);

            when(storeRepository.existsByNumber("S001")).thenReturn(false);
            when(storeRepository.save(any(Store.class))).thenAnswer(inv -> inv.getArgument(0));

            Store result = storeService.create(sampleStore);

            assertThat(result.getIsActive()).isTrue();
            assertThat(result.getOpenedDate()).isNotNull();
        }

        @Test
        @DisplayName("should normalize state to uppercase")
        void shouldNormalizeState() {
            sampleStore.setState("ca");

            when(storeRepository.existsByNumber("S001")).thenReturn(false);
            when(storeRepository.save(any(Store.class))).thenAnswer(inv -> inv.getArgument(0));

            Store result = storeService.create(sampleStore);

            assertThat(result.getState()).isEqualTo("CA");
        }

        @Test
        @DisplayName("should throw when duplicate number")
        void shouldThrowWhenDuplicateNumber() {
            when(storeRepository.existsByNumber("S001")).thenReturn(true);

            assertThatThrownBy(() -> storeService.create(sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("should throw when store is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> storeService.create(null))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should throw when number is missing")
        void shouldThrowWhenNumberMissing() {
            sampleStore.setNumber(null);

            assertThatThrownBy(() -> storeService.create(sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store number is required");
        }

        @Test
        @DisplayName("should throw when name is missing")
        void shouldThrowWhenNameMissing() {
            sampleStore.setName(null);

            assertThatThrownBy(() -> storeService.create(sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store name is required");
        }

        @Test
        @DisplayName("should throw when number exceeds max length")
        void shouldThrowWhenNumberTooLong() {
            sampleStore.setNumber("A".repeat(51));

            assertThatThrownBy(() -> storeService.create(sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store number must not exceed 50 characters");
        }

        @Test
        @DisplayName("should throw when name exceeds max length")
        void shouldThrowWhenNameTooLong() {
            sampleStore.setName("A".repeat(201));

            assertThatThrownBy(() -> storeService.create(sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store name must not exceed 200 characters");
        }

        @Test
        @DisplayName("should throw when state is not 2 letters")
        void shouldThrowWhenInvalidState() {
            sampleStore.setState("ILL");

            assertThatThrownBy(() -> storeService.create(sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("State must be a 2-letter code");
        }

        @Test
        @DisplayName("should throw when ZIP code format is invalid")
        void shouldThrowWhenInvalidZip() {
            sampleStore.setZip("1234");

            assertThatThrownBy(() -> storeService.create(sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ZIP code must be in format");
        }

        @Test
        @DisplayName("should throw when email format is invalid")
        void shouldThrowWhenInvalidEmail() {
            sampleStore.setEmail("not-an-email");

            assertThatThrownBy(() -> storeService.create(sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid email format");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update store successfully")
        void shouldUpdateStoreSuccessfully() {
            Store updated = new Store();
            updated.setNumber("S001");
            updated.setName("Updated Store");
            updated.setState("NY");

            when(storeRepository.findById(1L)).thenReturn(Optional.of(sampleStore));
            when(storeRepository.findByNumber("S001")).thenReturn(Optional.of(sampleStore));
            when(storeRepository.save(any(Store.class))).thenAnswer(inv -> inv.getArgument(0));

            Store result = storeService.update(1L, updated);

            assertThat(result.getName()).isEqualTo("Updated Store");
            assertThat(result.getState()).isEqualTo("NY");
        }

        @Test
        @DisplayName("should throw when store not found")
        void shouldThrowWhenNotFound() {
            when(storeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> storeService.update(99L, sampleStore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store not found with id: 99");
        }

        @Test
        @DisplayName("should throw when duplicate number on update")
        void shouldThrowWhenDuplicateNumberOnUpdate() {
            Store other = new Store();
            other.setId(2L);
            other.setNumber("S001");

            Store updated = new Store();
            updated.setNumber("S001");
            updated.setName("Some Name");

            when(storeRepository.findById(1L)).thenReturn(Optional.of(sampleStore));
            when(storeRepository.findByNumber("S001")).thenReturn(Optional.of(other));

            assertThatThrownBy(() -> storeService.update(1L, updated))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete store")
        void shouldDeleteStore() {
            when(storeRepository.existsById(1L)).thenReturn(true);

            storeService.delete(1L);

            verify(storeRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when store not found")
        void shouldThrowWhenNotFound() {
            when(storeRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> storeService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store not found with id: 99");
        }
    }

    @Nested
    @DisplayName("deactivate and activate")
    class DeactivateActivate {

        @Test
        @DisplayName("should deactivate store")
        void shouldDeactivateStore() {
            when(storeRepository.findById(1L)).thenReturn(Optional.of(sampleStore));
            when(storeRepository.save(any(Store.class))).thenAnswer(inv -> inv.getArgument(0));

            Store result = storeService.deactivate(1L);

            assertThat(result.getIsActive()).isFalse();
            assertThat(result.getClosedDate()).isNotNull();
        }

        @Test
        @DisplayName("should activate store")
        void shouldActivateStore() {
            sampleStore.setIsActive(false);
            when(storeRepository.findById(1L)).thenReturn(Optional.of(sampleStore));
            when(storeRepository.save(any(Store.class))).thenAnswer(inv -> inv.getArgument(0));

            Store result = storeService.activate(1L);

            assertThat(result.getIsActive()).isTrue();
            assertThat(result.getClosedDate()).isNull();
        }

        @Test
        @DisplayName("should throw on deactivate when not found")
        void shouldThrowOnDeactivateWhenNotFound() {
            when(storeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> storeService.deactivate(99L))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should throw on activate when not found")
        void shouldThrowOnActivateWhenNotFound() {
            when(storeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> storeService.activate(99L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return total count")
        void shouldReturnTotalCount() {
            when(storeRepository.count()).thenReturn(5L);

            assertThat(storeService.count()).isEqualTo(5L);
        }

        @Test
        @DisplayName("should return active count")
        void shouldReturnActiveCount() {
            when(storeRepository.countByIsActive(true)).thenReturn(3L);

            assertThat(storeService.countActive()).isEqualTo(3L);
        }

        @Test
        @DisplayName("should return count by state with uppercase normalization")
        void shouldReturnCountByState() {
            when(storeRepository.countByState("IL")).thenReturn(2L);

            assertThat(storeService.countByState("il")).isEqualTo(2L);
            verify(storeRepository).countByState("IL");
        }
    }
}
