package com.pos.backend.service;

import com.pos.backend.domain.TaxCategory;
import com.pos.backend.domain.TaxRate;
import com.pos.backend.repository.TaxCategoryRepository;
import com.pos.backend.repository.TaxRateRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaxRateService Unit Tests")
class TaxRateServiceTest {

    @Mock
    private TaxRateRepository taxRateRepository;

    @Mock
    private TaxCategoryRepository taxCategoryRepository;

    @InjectMocks
    private TaxRateService taxRateService;

    private TaxRate sampleRate;
    private TaxCategory sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new TaxCategory();
        sampleCategory.setId(1L);
        sampleCategory.setCategory("Food");
        sampleCategory.setIsActive(true);

        sampleRate = new TaxRate();
        sampleRate.setId(1L);
        sampleRate.setRate(new BigDecimal("0.0700"));
        sampleRate.setEffectiveDate(LocalDate.of(2023, 1, 1));
        sampleRate.setTaxCategory(sampleCategory);
        sampleRate.setDescription("Standard food tax");
        sampleRate.setIsActive(true);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all tax rates")
        void shouldReturnAll() {
            when(taxRateRepository.findAll()).thenReturn(List.of(sampleRate));
            assertThat(taxRateService.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when none exist")
        void shouldReturnEmpty() {
            when(taxRateRepository.findAll()).thenReturn(Collections.emptyList());
            assertThat(taxRateService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return rate when found")
        void shouldReturnWhenFound() {
            when(taxRateRepository.findById(1L)).thenReturn(Optional.of(sampleRate));
            assertThat(taxRateService.findById(1L)).isPresent();
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(taxRateRepository.findById(99L)).thenReturn(Optional.empty());
            assertThat(taxRateService.findById(99L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByQueries")
    class FindByQueries {

        @Test
        @DisplayName("should find by tax category")
        void shouldFindByTaxCategory() {
            when(taxRateRepository.findByTaxCategory_Id(1L)).thenReturn(List.of(sampleRate));
            assertThat(taxRateService.findByTaxCategory(1L)).hasSize(1);
        }

        @Test
        @DisplayName("should find all active")
        void shouldFindAllActive() {
            when(taxRateRepository.findByIsActive(true)).thenReturn(List.of(sampleRate));
            assertThat(taxRateService.findAllActive()).hasSize(1);
        }

        @Test
        @DisplayName("should get current rate for category")
        void shouldGetCurrentRate() {
            when(taxRateRepository
                    .findTopByTaxCategory_IdAndEffectiveDateLessThanEqualAndIsActiveTrueOrderByEffectiveDateDesc(
                            eq(1L), any(LocalDate.class)))
                    .thenReturn(Optional.of(sampleRate));

            Optional<TaxRate> result = taxRateService.getCurrentRateForCategory(1L);
            assertThat(result).isPresent();
            assertThat(result.get().getRate()).isEqualByComparingTo(new BigDecimal("0.0700"));
        }

        @Test
        @DisplayName("should get rate for category and specific date")
        void shouldGetRateForDate() {
            LocalDate date = LocalDate.of(2024, 6, 15);
            when(taxRateRepository
                    .findTopByTaxCategory_IdAndEffectiveDateLessThanEqualAndIsActiveTrueOrderByEffectiveDateDesc(
                            1L, date))
                    .thenReturn(Optional.of(sampleRate));

            Optional<TaxRate> result = taxRateService.getRateForCategoryAndDate(1L, date);
            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create tax rate successfully")
        void shouldCreate() {
            when(taxRateRepository.save(any(TaxRate.class))).thenAnswer(inv -> inv.getArgument(0));

            TaxRate result = taxRateService.create(sampleRate);

            assertThat(result).isNotNull();
            assertThat(result.getRate()).isEqualByComparingTo(new BigDecimal("0.0700"));
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            sampleRate.setIsActive(null);
            when(taxRateRepository.save(any(TaxRate.class))).thenAnswer(inv -> inv.getArgument(0));

            TaxRate result = taxRateService.create(sampleRate);

            assertThat(result.getIsActive()).isTrue();
        }

        @Test
        @DisplayName("should throw when null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> taxRateService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw when rate is null")
        void shouldThrowWhenRateNull() {
            sampleRate.setRate(null);
            assertThatThrownBy(() -> taxRateService.create(sampleRate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Rate is required");
        }

        @Test
        @DisplayName("should throw when rate is negative")
        void shouldThrowWhenRateNegative() {
            sampleRate.setRate(new BigDecimal("-0.01"));
            assertThatThrownBy(() -> taxRateService.create(sampleRate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between 0 and 1");
        }

        @Test
        @DisplayName("should throw when rate exceeds 1")
        void shouldThrowWhenRateTooHigh() {
            sampleRate.setRate(new BigDecimal("1.01"));
            assertThatThrownBy(() -> taxRateService.create(sampleRate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between 0 and 1");
        }

        @Test
        @DisplayName("should throw when effective date is null")
        void shouldThrowWhenDateNull() {
            sampleRate.setEffectiveDate(null);
            assertThatThrownBy(() -> taxRateService.create(sampleRate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Effective date is required");
        }

        @Test
        @DisplayName("should throw when tax category is null")
        void shouldThrowWhenCategoryNull() {
            sampleRate.setTaxCategory(null);
            assertThatThrownBy(() -> taxRateService.create(sampleRate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tax category is required");
        }
    }

    @Nested
    @DisplayName("createWithIds")
    class CreateWithIds {

        @Test
        @DisplayName("should resolve tax category from ID")
        void shouldResolveCategory() {
            TaxRate newRate = new TaxRate();
            newRate.setRate(new BigDecimal("0.0500"));
            newRate.setEffectiveDate(LocalDate.of(2025, 1, 1));

            when(taxCategoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
            when(taxRateRepository.save(any(TaxRate.class))).thenAnswer(inv -> inv.getArgument(0));

            TaxRate result = taxRateService.createWithIds(newRate, 1L);

            assertThat(result.getTaxCategory()).isEqualTo(sampleCategory);
        }

        @Test
        @DisplayName("should throw when category not found")
        void shouldThrowWhenCategoryNotFound() {
            TaxRate newRate = new TaxRate();
            newRate.setRate(new BigDecimal("0.0500"));
            newRate.setEffectiveDate(LocalDate.of(2025, 1, 1));

            when(taxCategoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taxRateService.createWithIds(newRate, 99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tax category not found");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update tax rate successfully")
        void shouldUpdate() {
            TaxRate updated = new TaxRate();
            updated.setRate(new BigDecimal("0.0800"));
            updated.setEffectiveDate(LocalDate.of(2025, 1, 1));
            updated.setTaxCategory(sampleCategory);
            updated.setDescription("Updated rate");
            updated.setIsActive(true);

            when(taxRateRepository.findById(1L)).thenReturn(Optional.of(sampleRate));
            when(taxRateRepository.save(any(TaxRate.class))).thenAnswer(inv -> inv.getArgument(0));

            TaxRate result = taxRateService.update(1L, updated);

            assertThat(result.getRate()).isEqualByComparingTo(new BigDecimal("0.0800"));
            assertThat(result.getDescription()).isEqualTo("Updated rate");
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(taxRateRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taxRateService.update(99L, sampleRate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tax rate not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete tax rate")
        void shouldDelete() {
            when(taxRateRepository.existsById(1L)).thenReturn(true);
            taxRateService.delete(1L);
            verify(taxRateRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(taxRateRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> taxRateService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tax rate not found");
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldCount() {
            when(taxRateRepository.count()).thenReturn(8L);
            assertThat(taxRateService.count()).isEqualTo(8);
        }
    }
}
