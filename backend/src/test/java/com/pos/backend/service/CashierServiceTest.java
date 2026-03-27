package com.pos.backend.service;

import com.pos.backend.domain.Cashier;
import com.pos.backend.domain.Person;
import com.pos.backend.domain.Store;
import com.pos.backend.repository.CashierRepository;
import com.pos.backend.repository.PersonRepository;
import com.pos.backend.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("CashierService Unit Tests")
class CashierServiceTest {

    @Mock
    private CashierRepository cashierRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CashierService cashierService;

    private Cashier sampleCashier;
    private Person samplePerson;
    private Store sampleStore;

    @BeforeEach
    void setUp() {
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        samplePerson = new Person();
        samplePerson.setId(1L);
        samplePerson.setFirstName("David");
        samplePerson.setLastName("Martin");

        sampleStore = new Store();
        sampleStore.setId(1L);
        sampleStore.setNumber("S001");
        sampleStore.setName("Quick Mart");

        sampleCashier = new Cashier();
        sampleCashier.setId(1L);
        sampleCashier.setNumber("C001");
        sampleCashier.setPassword("password1");
        sampleCashier.setPerson(samplePerson);
        sampleCashier.setStore(sampleStore);
        sampleCashier.setIsActive(true);
        sampleCashier.setHireDate(LocalDateTime.of(2023, 1, 15, 9, 0));
        sampleCashier.setRole("Cashier");
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all cashiers")
        void shouldReturnAll() {
            when(cashierRepository.findAll()).thenReturn(List.of(sampleCashier));
            List<Cashier> result = cashierService.findAll();
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when none exist")
        void shouldReturnEmpty() {
            when(cashierRepository.findAll()).thenReturn(Collections.emptyList());
            assertThat(cashierService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return cashier when found")
        void shouldReturnWhenFound() {
            when(cashierRepository.findById(1L)).thenReturn(Optional.of(sampleCashier));
            assertThat(cashierService.findById(1L)).isPresent();
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(cashierRepository.findById(99L)).thenReturn(Optional.empty());
            assertThat(cashierService.findById(99L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByQueries")
    class FindByQueries {

        @Test
        @DisplayName("should find by number")
        void shouldFindByNumber() {
            when(cashierRepository.findByNumber("C001")).thenReturn(Optional.of(sampleCashier));
            assertThat(cashierService.findByNumber("C001")).isPresent();
        }

        @Test
        @DisplayName("should find by store")
        void shouldFindByStore() {
            when(cashierRepository.findByStore_Id(1L)).thenReturn(List.of(sampleCashier));
            assertThat(cashierService.findByStore(1L)).hasSize(1);
        }

        @Test
        @DisplayName("should find all active")
        void shouldFindAllActive() {
            when(cashierRepository.findByIsActive(true)).thenReturn(List.of(sampleCashier));
            assertThat(cashierService.findAllActive()).hasSize(1);
        }

        @Test
        @DisplayName("should find by role")
        void shouldFindByRole() {
            when(cashierRepository.findByRole("Cashier")).thenReturn(List.of(sampleCashier));
            assertThat(cashierService.findByRole("Cashier")).hasSize(1);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create cashier successfully")
        void shouldCreate() {
            when(cashierRepository.existsByNumber("C001")).thenReturn(false);
            when(cashierRepository.save(any(Cashier.class))).thenAnswer(inv -> inv.getArgument(0));

            Cashier result = cashierService.create(sampleCashier);

            assertThat(result).isNotNull();
            assertThat(result.getNumber()).isEqualTo("C001");
            verify(cashierRepository).save(any(Cashier.class));
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            sampleCashier.setIsActive(null);
            sampleCashier.setHireDate(null);
            sampleCashier.setRole(null);

            when(cashierRepository.existsByNumber("C001")).thenReturn(false);
            when(cashierRepository.save(any(Cashier.class))).thenAnswer(inv -> inv.getArgument(0));

            Cashier result = cashierService.create(sampleCashier);

            assertThat(result.getIsActive()).isTrue();
            assertThat(result.getHireDate()).isNotNull();
            assertThat(result.getRole()).isEqualTo("Cashier");
        }

        @Test
        @DisplayName("should throw when duplicate number")
        void shouldThrowWhenDuplicate() {
            when(cashierRepository.existsByNumber("C001")).thenReturn(true);

            assertThatThrownBy(() -> cashierService.create(sampleCashier))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("should throw when cashier is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> cashierService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw when number is blank")
        void shouldThrowWhenNumberBlank() {
            sampleCashier.setNumber("  ");
            assertThatThrownBy(() -> cashierService.create(sampleCashier))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("number is required");
        }

        @Test
        @DisplayName("should throw when password is blank")
        void shouldThrowWhenPasswordBlank() {
            sampleCashier.setPassword("");
            assertThatThrownBy(() -> cashierService.create(sampleCashier))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password is required");
        }
    }

    @Nested
    @DisplayName("createWithIds")
    class CreateWithIds {

        @Test
        @DisplayName("should resolve person and store from IDs")
        void shouldResolveRelationships() {
            Cashier newCashier = new Cashier();
            newCashier.setNumber("C010");
            newCashier.setPassword("pass");

            when(personRepository.findById(1L)).thenReturn(Optional.of(samplePerson));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(sampleStore));
            when(cashierRepository.existsByNumber("C010")).thenReturn(false);
            when(cashierRepository.save(any(Cashier.class))).thenAnswer(inv -> inv.getArgument(0));

            Cashier result = cashierService.createWithIds(newCashier, 1L, 1L);

            assertThat(result.getPerson()).isEqualTo(samplePerson);
            assertThat(result.getStore()).isEqualTo(sampleStore);
        }

        @Test
        @DisplayName("should throw when person not found")
        void shouldThrowWhenPersonNotFound() {
            Cashier newCashier = new Cashier();
            newCashier.setNumber("C010");
            newCashier.setPassword("pass");

            when(personRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashierService.createWithIds(newCashier, 99L, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Person not found");
        }

        @Test
        @DisplayName("should throw when store not found")
        void shouldThrowWhenStoreNotFound() {
            Cashier newCashier = new Cashier();
            newCashier.setNumber("C010");
            newCashier.setPassword("pass");

            when(personRepository.findById(1L)).thenReturn(Optional.of(samplePerson));
            when(storeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashierService.createWithIds(newCashier, 1L, 99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Store not found");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update cashier successfully")
        void shouldUpdate() {
            Cashier updated = new Cashier();
            updated.setNumber("C001");
            updated.setPassword("newpass");
            updated.setPerson(samplePerson);
            updated.setStore(sampleStore);
            updated.setIsActive(true);
            updated.setRole("Supervisor");

            when(cashierRepository.findById(1L)).thenReturn(Optional.of(sampleCashier));
            when(cashierRepository.findByNumber("C001")).thenReturn(Optional.of(sampleCashier));
            when(cashierRepository.save(any(Cashier.class))).thenAnswer(inv -> inv.getArgument(0));

            Cashier result = cashierService.update(1L, updated);

            assertThat(result.getRole()).isEqualTo("Supervisor");
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashierService.update(99L, sampleCashier))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cashier not found");
        }

        @Test
        @DisplayName("should throw when duplicate number on update")
        void shouldThrowWhenDuplicateOnUpdate() {
            Cashier other = new Cashier();
            other.setId(2L);
            other.setNumber("C001");

            Cashier updated = new Cashier();
            updated.setNumber("C001");
            updated.setPassword("pass");

            when(cashierRepository.findById(1L)).thenReturn(Optional.of(sampleCashier));
            when(cashierRepository.findByNumber("C001")).thenReturn(Optional.of(other));

            assertThatThrownBy(() -> cashierService.update(1L, updated))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("terminate")
    class Terminate {

        @Test
        @DisplayName("should terminate cashier")
        void shouldTerminate() {
            when(cashierRepository.findById(1L)).thenReturn(Optional.of(sampleCashier));
            when(cashierRepository.save(any(Cashier.class))).thenAnswer(inv -> inv.getArgument(0));

            Cashier result = cashierService.terminate(1L);

            assertThat(result.getIsActive()).isFalse();
            assertThat(result.getTerminationDate()).isNotNull();
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(cashierRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashierService.terminate(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cashier not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete cashier")
        void shouldDelete() {
            when(cashierRepository.existsById(1L)).thenReturn(true);
            cashierService.delete(1L);
            verify(cashierRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(cashierRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> cashierService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cashier not found");
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldCount() {
            when(cashierRepository.count()).thenReturn(5L);
            assertThat(cashierService.count()).isEqualTo(5);
        }

        @Test
        @DisplayName("should return count by store")
        void shouldCountByStore() {
            when(cashierRepository.countByStore_Id(1L)).thenReturn(3L);
            assertThat(cashierService.countByStore(1L)).isEqualTo(3);
        }
    }
}
