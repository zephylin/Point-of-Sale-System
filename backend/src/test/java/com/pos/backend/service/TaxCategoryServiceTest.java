package com.pos.backend.service;

import com.pos.backend.domain.TaxCategory;
import com.pos.backend.repository.TaxCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaxCategoryService Unit Tests")
class TaxCategoryServiceTest {

    @Mock
    private TaxCategoryRepository taxCategoryRepository;

    @InjectMocks
    private TaxCategoryService taxCategoryService;

    private TaxCategory sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new TaxCategory();
        sampleCategory.setId(1L);
        sampleCategory.setCategory("Food");
        sampleCategory.setDescription("Prepared and packaged food items");
        sampleCategory.setIsActive(true);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all tax categories")
        void shouldReturnAll() {
            when(taxCategoryRepository.findAll()).thenReturn(List.of(sampleCategory));
            assertThat(taxCategoryService.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when none exist")
        void shouldReturnEmpty() {
            when(taxCategoryRepository.findAll()).thenReturn(Collections.emptyList());
            assertThat(taxCategoryService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return category when found")
        void shouldReturnWhenFound() {
            when(taxCategoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
            assertThat(taxCategoryService.findById(1L)).isPresent();
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(taxCategoryRepository.findById(99L)).thenReturn(Optional.empty());
            assertThat(taxCategoryService.findById(99L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByQueries")
    class FindByQueries {

        @Test
        @DisplayName("should find by category name")
        void shouldFindByCategory() {
            when(taxCategoryRepository.findByCategoryIgnoreCase("Food")).thenReturn(Optional.of(sampleCategory));
            assertThat(taxCategoryService.findByCategory("Food")).isPresent();
        }

        @Test
        @DisplayName("should search by keyword")
        void shouldSearch() {
            when(taxCategoryRepository.findByCategoryContainingIgnoreCase("Foo")).thenReturn(List.of(sampleCategory));
            assertThat(taxCategoryService.search("Foo")).hasSize(1);
        }

        @Test
        @DisplayName("should find all active")
        void shouldFindAllActive() {
            when(taxCategoryRepository.findByIsActive(true)).thenReturn(List.of(sampleCategory));
            assertThat(taxCategoryService.findAllActive()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create tax category successfully")
        void shouldCreate() {
            when(taxCategoryRepository.existsByCategoryIgnoreCase("Food")).thenReturn(false);
            when(taxCategoryRepository.save(any(TaxCategory.class))).thenAnswer(inv -> inv.getArgument(0));

            TaxCategory result = taxCategoryService.create(sampleCategory);

            assertThat(result).isNotNull();
            assertThat(result.getCategory()).isEqualTo("Food");
        }

        @Test
        @DisplayName("should set defaults when not provided")
        void shouldSetDefaults() {
            sampleCategory.setIsActive(null);

            when(taxCategoryRepository.existsByCategoryIgnoreCase("Food")).thenReturn(false);
            when(taxCategoryRepository.save(any(TaxCategory.class))).thenAnswer(inv -> inv.getArgument(0));

            TaxCategory result = taxCategoryService.create(sampleCategory);

            assertThat(result.getIsActive()).isTrue();
        }

        @Test
        @DisplayName("should throw when duplicate name")
        void shouldThrowWhenDuplicate() {
            when(taxCategoryRepository.existsByCategoryIgnoreCase("Food")).thenReturn(true);

            assertThatThrownBy(() -> taxCategoryService.create(sampleCategory))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("should throw when null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> taxCategoryService.create(null))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should throw when category name blank")
        void shouldThrowWhenNameBlank() {
            sampleCategory.setCategory("  ");
            assertThatThrownBy(() -> taxCategoryService.create(sampleCategory))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Category name is required");
        }

        @Test
        @DisplayName("should throw when name exceeds max length")
        void shouldThrowWhenNameTooLong() {
            sampleCategory.setCategory("A".repeat(101));
            assertThatThrownBy(() -> taxCategoryService.create(sampleCategory))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not exceed 100 characters");
        }

        @Test
        @DisplayName("should throw when description exceeds max length")
        void shouldThrowWhenDescriptionTooLong() {
            sampleCategory.setDescription("A".repeat(501));
            assertThatThrownBy(() -> taxCategoryService.create(sampleCategory))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not exceed 500 characters");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update tax category successfully")
        void shouldUpdate() {
            TaxCategory updated = new TaxCategory();
            updated.setCategory("Prepared Food");
            updated.setDescription("Updated description");
            updated.setIsActive(true);

            when(taxCategoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
            when(taxCategoryRepository.findByCategoryIgnoreCase("Prepared Food")).thenReturn(Optional.empty());
            when(taxCategoryRepository.save(any(TaxCategory.class))).thenAnswer(inv -> inv.getArgument(0));

            TaxCategory result = taxCategoryService.update(1L, updated);

            assertThat(result.getCategory()).isEqualTo("Prepared Food");
            assertThat(result.getDescription()).isEqualTo("Updated description");
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(taxCategoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taxCategoryService.update(99L, sampleCategory))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tax category not found");
        }

        @Test
        @DisplayName("should throw when duplicate name on update")
        void shouldThrowWhenDuplicateOnUpdate() {
            TaxCategory other = new TaxCategory();
            other.setId(2L);
            other.setCategory("Food");

            TaxCategory updated = new TaxCategory();
            updated.setCategory("Food");

            when(taxCategoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
            when(taxCategoryRepository.findByCategoryIgnoreCase("Food")).thenReturn(Optional.of(other));

            assertThatThrownBy(() -> taxCategoryService.update(1L, updated))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("deactivate")
    class Deactivate {

        @Test
        @DisplayName("should deactivate tax category")
        void shouldDeactivate() {
            when(taxCategoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
            when(taxCategoryRepository.save(any(TaxCategory.class))).thenAnswer(inv -> inv.getArgument(0));

            TaxCategory result = taxCategoryService.deactivate(1L);

            assertThat(result.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(taxCategoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taxCategoryService.deactivate(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tax category not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete tax category")
        void shouldDelete() {
            when(taxCategoryRepository.existsById(1L)).thenReturn(true);
            taxCategoryService.delete(1L);
            verify(taxCategoryRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(taxCategoryRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> taxCategoryService.delete(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tax category not found");
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return count")
        void shouldCount() {
            when(taxCategoryRepository.count()).thenReturn(4L);
            assertThat(taxCategoryService.count()).isEqualTo(4);
        }
    }
}
