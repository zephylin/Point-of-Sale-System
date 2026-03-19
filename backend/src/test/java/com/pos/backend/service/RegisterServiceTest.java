package com.pos.backend.service;

import com.pos.backend.domain.Register;
import com.pos.backend.domain.Store;
import com.pos.backend.repository.RegisterRepository;
import com.pos.backend.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterService Unit Tests")
class RegisterServiceTest {

    @Mock
    private RegisterRepository registerRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private RegisterService registerService;

    private Register sampleRegister;
    private Store sampleStore;

    @BeforeEach
    void setUp() {
        sampleStore = new Store();
        sampleStore.setId(1L);
        sampleStore.setNumber("S001");
        sampleStore.setName("Quick Mart");

        sampleRegister = new Register();
        sampleRegister.setId(1L);
        sampleRegister.setNumber("R001");
        sampleRegister.setStore(sampleStore);
        sampleRegister.setDescription("Front Register - Lane 1");
        sampleRegister.setIsActive(true);
        sampleRegister.setStatus("CLOSED");
        sampleRegister.setInstalledDate(LocalDateTime.of(2023, 1, 1, 8, 0));
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all registers")
        void shouldReturnAll() {
            when(registerRepository.findAll()).thenReturn(List.of(sampleRegister));
            assertThat(registerService.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when none exist")
        void shouldReturnEmpty() {
            when(registerRepository.findAll()).thenReturn(Collections.emptyList());
            assertThat(registerService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return register when found")
        void shouldReturnWhenFound() {
            when(registerRepository.findById(1L)).thenReturn(Optional.of(sampleRegister));
            assertThat(registerService.findById(1L)).isPresent();
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(registerRepository.findById(99L)).thenReturn(Optional.empty());
            assertThat(registerService.findById(99L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByQueries")
    class FindByQueries {

        @Test
        @DisplayName("should find by number")
        void shouldFindByNumber() {
            when(registerRepository.findByNumber("R001")).thenReturn(Optional.of(sampleRegister));
            assertThat(registerService.findByNumber("R001")).isPresent();
        }

        @Test
        @DisplayName("should find by store")
        void shouldFindByStore() {
            when(registerRepository.findByStore_Id(1L)).thenReturn(List.of(sampleRegister));
            assertThat(registerService.findByStore(1L)).hasSize(1);
        }

        @Test
        @DisplayName("should find all active")
        void shouldFindAllActive() {
            when(registerRepository.findByIsActive(true)).thenReturn(List.of(sampleRegister));
            assertThat(registerService.findAllActive()).hasSize(1);
        }

        @Test
        @DisplayName("should find by status")
        void shouldFindByStatus() {
            when(registerRepository.findByStatus("CLOSED")).thenReturn(List.of(sampleRegister));
            assertThat(registerService.findByStatus("CLOSED")).hasSize(1);
        }

        @Test
        @DisplayName("should find by store and status")
        void shouldFindByStoreAndStatus() {
            when(registerRepository.findByStore_IdAndStatus(1L, "CLOSED")).thenReturn(List.of(sampleRegister));
            assertThat(registerService.findByStoreAndStatus(1L, "CLOSED")).hasSize(1);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create register successfully")
        void shouldCreate() {
            when(registerRepository.existsByNumber("R001")).thenReturn(false);
            when(registerRepository.save(any(Register.class))).thenAnswer(inv -> inv.getArgument(0));

            Register result = registerService.create(sampleRegister);

            assertThat(result).isNotNull();
            assertThat(result.getNumber()).isEqualTo("R001");
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            sampleRegister.setIsActive(null);
            sampleRegister.setStatus(null);
            sampleRegister.setInstalledDate(null);

            when(registerRepository.existsByNumber("R001")).thenReturn(false);
            when(registerRepository.save(any(Register.class))).thenAnswer(inv -> inv.getArgument(0));

            Register result = registerService.create(sampleRegister);

            assertThat(result.getIsActive()).isTrue();
            assertThat(result.getStatus()).isEqualTo("CLOSED");
            assertThat(result.getInstalledDate()).isNotNull();
        }

        @Test
        @DisplayName("should throw when duplicate number")
        void shouldThrowWhenDuplicate() {
            when(registerRepository.existsByNumber("R001")).thenReturn(true);

            assertThatThrownBy(() -> registerService.create(sampleRegister))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("should throw when register is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> registerService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw when number is blank")
        void shouldThrowWhenNumberBlank() {
            sampleRegister.setNumber("  ");
            assertThatThrownBy(() -> registerService.create(sampleRegister))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("number is required");
        }

        @Test
        @DisplayName("should throw when store is null")
        void shouldThrowWhenStoreNull() {
            sampleRegister.setStore(null);
            assertThatThrownBy(() -> registerService.create(sampleRegister))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store is required");
        }
    }

    @Nested
    @DisplayName("createWithIds")
    class CreateWithIds {

        @Test
        @DisplayName("should resolve store from ID")
        void shouldResolveStore() {
            Register newReg = new Register();
            newReg.setNumber("R010");

            when(storeRepository.findById(1L)).thenReturn(Optional.of(sampleStore));
            when(registerRepository.existsByNumber("R010")).thenReturn(false);
            when(registerRepository.save(any(Register.class))).thenAnswer(inv -> inv.getArgument(0));

            Register result = registerService.createWithIds(newReg, 1L);

            assertThat(result.getStore()).isEqualTo(sampleStore);
        }

        @Test
        @DisplayName("should throw when store not found")
        void shouldThrowWhenStoreNotFound() {
            Register newReg = new Register();
            newReg.setNumber("R010");

            when(storeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> registerService.createWithIds(newReg, 99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store not found");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update register successfully")
        void shouldUpdate() {
            Register updated = new Register();
            updated.setNumber("R001");
            updated.setStore(sampleStore);
            updated.setDescription("Updated Lane");
            updated.setIsActive(true);
            updated.setStatus("OPEN");

            when(registerRepository.findById(1L)).thenReturn(Optional.of(sampleRegister));
            when(registerRepository.findByNumber("R001")).thenReturn(Optional.of(sampleRegister));
            when(registerRepository.save(any(Register.class))).thenAnswer(inv -> inv.getArgument(0));

            Register result = registerService.update(1L, updated);

            assertThat(result.getDescription()).isEqualTo("Updated Lane");
            assertThat(result.getStatus()).isEqualTo("OPEN");
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(registerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> registerService.update(99L, sampleRegister))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Register not found");
        }

        @Test
        @DisplayName("should throw when duplicate number on update")
        void shouldThrowWhenDuplicateOnUpdate() {
            Register other = new Register();
            other.setId(2L);
            other.setNumber("R001");

            Register updated = new Register();
            updated.setNumber("R001");
            updated.setStore(sampleStore);

            when(registerRepository.findById(1L)).thenReturn(Optional.of(sampleRegister));
            when(registerRepository.findByNumber("R001")).thenReturn(Optional.of(other));

            assertThatThrownBy(() -> registerService.update(1L, updated))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("updateStatus")
    class UpdateStatus {

        @Test
        @DisplayName("should update status")
        void shouldUpdateStatus() {
            when(registerRepository.findById(1L)).thenReturn(Optional.of(sampleRegister));
            when(registerRepository.save(any(Register.class))).thenAnswer(inv -> inv.getArgument(0));

            Register result = registerService.updateStatus(1L, "OPEN");

            assertThat(result.getStatus()).isEqualTo("OPEN");
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(registerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> registerService.updateStatus(99L, "OPEN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Register not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete register")
        void shouldDelete() {
            when(registerRepository.existsById(1L)).thenReturn(true);
            registerService.delete(1L);
            verify(registerRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(registerRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> registerService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Register not found");
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldCount() {
            when(registerRepository.count()).thenReturn(3L);
            assertThat(registerService.count()).isEqualTo(3);
        }

        @Test
        @DisplayName("should return count by store")
        void shouldCountByStore() {
            when(registerRepository.countByStore_Id(1L)).thenReturn(2L);
            assertThat(registerService.countByStore(1L)).isEqualTo(2);
        }
    }
}
