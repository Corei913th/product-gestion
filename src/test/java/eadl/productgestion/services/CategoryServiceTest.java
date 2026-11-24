package eadl.productgestion.services;

import eadl.productgestion.dto.request.CategoryRequest;
import eadl.productgestion.dto.response.CategoryResponse;
import eadl.productgestion.entity.Category;
import eadl.productgestion.exception.CategoryNotFoundException;
import eadl.productgestion.exception.DuplicateCategoryException;
import eadl.productgestion.mappers.CategoryMapper;
import eadl.productgestion.repositories.CategoryRepository;
import eadl.productgestion.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Création valide d'une catégorie")
    void create_whenValidRequest_returnsCategoryResponse() {
        // Arrange
        CategoryRequest request = new CategoryRequest("Informatique");
        Category savedCategory = new Category(1L, "Informatique");
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Informatique");

        when(categoryRepository.existsByNameIgnoreCase("Informatique")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toResponse(savedCategory)).thenReturn(expectedResponse);

        // Act
        CategoryResponse actualResponse = categoryService.create(request);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(1L);
        assertThat(actualResponse.name()).isEqualTo("Informatique");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Création avec un nom de catégorie déjà existant")
    void create_whenDuplicateName_throwsDuplicateCategoryException() {
        // Arrange
        CategoryRequest request = new CategoryRequest("Informatique");
        when(categoryRepository.existsByNameIgnoreCase("Informatique")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.create(request))
                .isInstanceOf(DuplicateCategoryException.class)
                .hasMessageContaining("Informatique");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Récupération de toutes les catégories")
    void findAll_returnsListOfCategoryResponses() {
        // Arrange
        List<Category> categories = List.of(
                new Category(1L, "Informatique"),
                new Category(2L, "Téléphonie")
        );
        List<CategoryResponse> expectedResponses = List.of(
                new CategoryResponse(1L, "Informatique"),
                new CategoryResponse(2L, "Téléphonie")
        );

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponseList(categories)).thenReturn(expectedResponses);

        // Act
        List<CategoryResponse> actualResponses = categoryService.findAll();

        // Assert
        assertThat(actualResponses).hasSize(2);
        assertThat(actualResponses).isEqualTo(expectedResponses);
    }

    @Test
    @DisplayName("Consultation d'une catégorie existante")
    void findById_whenCategoryExists_returnsCategoryResponse() {
        // Arrange
        Category category = new Category(1L, "Informatique");
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Informatique");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

        // Act
        CategoryResponse actualResponse = categoryService.findById(1L);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(1L);
        assertThat(actualResponse.name()).isEqualTo("Informatique");
    }

    @Test
    @DisplayName("Consultation d'une catégorie inexistante")
    void findById_whenCategoryDoesNotExist_throwsCategoryNotFoundException() {
        // Arrange
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("99");
    }
}
