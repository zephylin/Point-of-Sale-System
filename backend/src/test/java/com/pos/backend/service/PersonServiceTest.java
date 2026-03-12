package com.pos.backend.service;

import com.pos.backend.domain.Person;
import com.pos.backend.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonService Unit Tests")
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person samplePerson;

    @BeforeEach
    void setUp() {
        samplePerson = new Person();
        samplePerson.setId(1L);
        samplePerson.setFirstName("John");
        samplePerson.setLastName("Doe");
        samplePerson.setAddress("123 Main St");
        samplePerson.setCity("Springfield");
        samplePerson.setState("IL");
        samplePerson.setZip("62704");
        samplePerson.setPhone("555-1234");
        samplePerson.setSsn("123-45-6789");
    }

    @Nested
    @DisplayName("getAllPersons")
    class GetAllPersons {

        @Test
        @DisplayName("should return all persons")
        void shouldReturnAllPersons() {
            Person person2 = new Person();
            person2.setId(2L);
            person2.setFirstName("Jane");
            person2.setLastName("Smith");
            when(personRepository.findAll()).thenReturn(Arrays.asList(samplePerson, person2));

            List<Person> result = personService.getAllPersons();

            assertThat(result).hasSize(2);
            verify(personRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no persons exist")
        void shouldReturnEmptyList() {
            when(personRepository.findAll()).thenReturn(Collections.emptyList());

            List<Person> result = personService.getAllPersons();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPersonById")
    class GetPersonById {

        @Test
        @DisplayName("should return person when found")
        void shouldReturnPersonWhenFound() {
            when(personRepository.findById(1L)).thenReturn(Optional.of(samplePerson));

            Person result = personService.getPersonById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getLastName()).isEqualTo("Doe");
        }

        @Test
        @DisplayName("should throw exception when not found")
        void shouldThrowWhenNotFound() {
            when(personRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> personService.getPersonById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Person not found with id: 99");
        }
    }

    @Nested
    @DisplayName("getPersonBySsn")
    class GetPersonBySsn {

        @Test
        @DisplayName("should return person when SSN found")
        void shouldReturnPersonWhenSsnFound() {
            when(personRepository.findBySsn("123-45-6789")).thenReturn(Optional.of(samplePerson));

            Person result = personService.getPersonBySsn("123-45-6789");

            assertThat(result.getSsn()).isEqualTo("123-45-6789");
        }

        @Test
        @DisplayName("should throw exception when SSN not found")
        void shouldThrowWhenSsnNotFound() {
            when(personRepository.findBySsn("000-00-0000")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> personService.getPersonBySsn("000-00-0000"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Person not found with SSN");
        }
    }

    @Nested
    @DisplayName("searchPersonsByName")
    class SearchPersonsByName {

        @Test
        @DisplayName("should return matching persons")
        void shouldReturnMatchingPersons() {
            when(personRepository.findByFirstNameIgnoreCaseOrLastNameIgnoreCase("John", "John"))
                    .thenReturn(List.of(samplePerson));

            List<Person> result = personService.searchPersonsByName("John");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFirstName()).isEqualTo("John");
        }
    }

    @Nested
    @DisplayName("createPerson")
    class CreatePerson {

        @Test
        @DisplayName("should create person successfully")
        void shouldCreatePersonSuccessfully() {
            when(personRepository.existsBySsn("123-45-6789")).thenReturn(false);
            when(personRepository.save(any(Person.class))).thenReturn(samplePerson);

            Person result = personService.createPerson(samplePerson);

            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("John");
            verify(personRepository).save(any(Person.class));
        }

        @Test
        @DisplayName("should normalize state to uppercase")
        void shouldNormalizeStateToUppercase() {
            samplePerson.setState("il");
            when(personRepository.existsBySsn(anyString())).thenReturn(false);
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.createPerson(samplePerson);

            assertThat(result.getState()).isEqualTo("IL");
        }

        @Test
        @DisplayName("should throw when SSN already exists")
        void shouldThrowWhenDuplicateSsn() {
            when(personRepository.existsBySsn("123-45-6789")).thenReturn(true);

            assertThatThrownBy(() -> personService.createPerson(samplePerson))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Person with this SSN already exists");
        }

        @Test
        @DisplayName("should throw when first name is missing")
        void shouldThrowWhenFirstNameMissing() {
            samplePerson.setFirstName(null);

            assertThatThrownBy(() -> personService.createPerson(samplePerson))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("First name is required");
        }

        @Test
        @DisplayName("should throw when first name is blank")
        void shouldThrowWhenFirstNameBlank() {
            samplePerson.setFirstName("   ");

            assertThatThrownBy(() -> personService.createPerson(samplePerson))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("First name is required");
        }

        @Test
        @DisplayName("should throw when last name is missing")
        void shouldThrowWhenLastNameMissing() {
            samplePerson.setLastName(null);

            assertThatThrownBy(() -> personService.createPerson(samplePerson))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Last name is required");
        }

        @Test
        @DisplayName("should throw when SSN format is invalid")
        void shouldThrowWhenInvalidSsnFormat() {
            samplePerson.setSsn("12345");

            assertThatThrownBy(() -> personService.createPerson(samplePerson))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid SSN format");
        }

        @Test
        @DisplayName("should throw when state is not 2 letters")
        void shouldThrowWhenStateInvalid() {
            samplePerson.setState("Illinois");

            assertThatThrownBy(() -> personService.createPerson(samplePerson))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("State must be 2 letter code");
        }

        @Test
        @DisplayName("should allow null SSN")
        void shouldAllowNullSsn() {
            samplePerson.setSsn(null);
            when(personRepository.save(any(Person.class))).thenReturn(samplePerson);

            Person result = personService.createPerson(samplePerson);

            assertThat(result).isNotNull();
            verify(personRepository, never()).existsBySsn(anyString());
        }
    }

    @Nested
    @DisplayName("updatePerson")
    class UpdatePerson {

        @Test
        @DisplayName("should update person successfully")
        void shouldUpdatePersonSuccessfully() {
            Person updatedDetails = new Person();
            updatedDetails.setFirstName("Johnny");
            updatedDetails.setLastName("Doe");
            updatedDetails.setState("CA");

            when(personRepository.findById(1L)).thenReturn(Optional.of(samplePerson));
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.updatePerson(1L, updatedDetails);

            assertThat(result.getFirstName()).isEqualTo("Johnny");
            assertThat(result.getState()).isEqualTo("CA");
        }

        @Test
        @DisplayName("should throw when person not found for update")
        void shouldThrowWhenNotFoundForUpdate() {
            when(personRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> personService.updatePerson(99L, samplePerson))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Person not found with id: 99");
        }
    }

    @Nested
    @DisplayName("deletePerson")
    class DeletePerson {

        @Test
        @DisplayName("should delete person successfully")
        void shouldDeletePersonSuccessfully() {
            when(personRepository.findById(1L)).thenReturn(Optional.of(samplePerson));

            personService.deletePerson(1L);

            verify(personRepository).delete(samplePerson);
        }

        @Test
        @DisplayName("should throw when person not found for delete")
        void shouldThrowWhenNotFoundForDelete() {
            when(personRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> personService.deletePerson(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Person not found with id: 99");
        }
    }

    @Nested
    @DisplayName("countPersons")
    class CountPersons {

        @Test
        @DisplayName("should return count")
        void shouldReturnCount() {
            when(personRepository.count()).thenReturn(5L);

            long result = personService.countPersons();

            assertThat(result).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("personExists")
    class PersonExists {

        @Test
        @DisplayName("should return true when exists")
        void shouldReturnTrueWhenExists() {
            when(personRepository.existsById(1L)).thenReturn(true);

            assertThat(personService.personExists(1L)).isTrue();
        }

        @Test
        @DisplayName("should return false when not exists")
        void shouldReturnFalseWhenNotExists() {
            when(personRepository.existsById(99L)).thenReturn(false);

            assertThat(personService.personExists(99L)).isFalse();
        }
    }
}
