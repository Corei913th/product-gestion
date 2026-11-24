package eadl.productgestion.services;

import eadl.productgestion.dto.request.ProductRequest;
import eadl.productgestion.dto.request.StockAdjustmentRequest;
import eadl.productgestion.dto.response.ProductResponse;
import eadl.productgestion.entity.Category;
import eadl.productgestion.entity.Product;
import eadl.productgestion.exception.CategoryNotFoundException;
import eadl.productgestion.exception.DuplicateProductException;
import eadl.productgestion.exception.InsufficientStockException;
import eadl.productgestion.exception.ProductNotFoundException;
import eadl.productgestion.mappers.ProductMapper;
import eadl.productgestion.repositories.CategoryRepository;
import eadl.productgestion.repositories.ProductRepository;
import eadl.productgestion.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category testCategory;
    private Product testProduct;
    private ProductRequest testRequest;
    private ProductResponse testResponse;

    @BeforeEach
    void setUp() {
        testCategory = new Category(1L, "Informatique");
        testProduct = Product.builder()
                .id(1L)
                .sku("LAP-001")
                .name("Ordinateur portable")
                .description("8 Go RAM")
                .price(new BigDecimal("1500.00"))
                .quantity(10)
                .category(testCategory)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        testRequest = new ProductRequest(
                "LAP-001",
                "Ordinateur portable",
                "8 Go RAM",
                new BigDecimal("1500.00"),
                10,
                1L
        );

        testResponse = new ProductResponse(
                1L,
                "LAP-001",
                "Ordinateur portable",
                "8 Go RAM",
                new BigDecimal("1500.00"),
                10,
                "Informatique",
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("Création valide d'un produit")
    void create_whenValid_returnsProductResponse() {
        when(productRepository.existsBySku("LAP-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productMapper.toEntity(testRequest)).thenReturn(testProduct);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponse(testProduct)).thenReturn(testResponse);

        ProductResponse response = productService.create(testRequest);

        assertThat(response).isNotNull();
        assertThat(response.sku()).isEqualTo("LAP-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Création avec SKU déjà existant (RG2)")
    void create_whenDuplicateSku_throwsDuplicateProductException() {
        when(productRepository.existsBySku("LAP-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(testRequest))
                .isInstanceOf(DuplicateProductException.class)
                .hasMessageContaining("LAP-001");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Création avec catégorie inexistante (RG5)")
    void create_whenCategoryNotFound_throwsCategoryNotFoundException() {
        when(productRepository.existsBySku("LAP-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(testRequest))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("1");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Consultation d'un produit existant")
    void findById_whenProductExists_returnsProductResponse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponse(testProduct)).thenReturn(testResponse);

        ProductResponse response = productService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consultation d'un produit inexistant (RG7)")
    void findById_whenProductNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Modification valide d'un produit")
    void update_whenValid_returnsUpdatedProductResponse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.existsBySkuAndIdNot("LAP-001", 1L)).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponse(testProduct)).thenReturn(testResponse);

        ProductResponse response = productService.update(1L, testRequest);

        assertThat(response).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Modification d'un produit inexistant (RG7)")
    void update_whenNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, testRequest))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Entrée en stock valide")
    void adjustStock_whenValidEntry_increasesStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponse(testProduct)).thenReturn(testResponse);

        StockAdjustmentRequest request = new StockAdjustmentRequest(5);
        ProductResponse response = productService.adjustStock(1L, request);

        assertThat(response).isNotNull();
        assertThat(testProduct.getQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("Sortie de stock valide")
    void adjustStock_whenValidExit_decreasesStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponse(testProduct)).thenReturn(testResponse);

        StockAdjustmentRequest request = new StockAdjustmentRequest(-4);
        ProductResponse response = productService.adjustStock(1L, request);

        assertThat(response).isNotNull();
        assertThat(testProduct.getQuantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("Sortie de stock supérieure au disponible (RG6)")
    void adjustStock_whenExitExceedsAvailableStock_throwsInsufficientStockException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        StockAdjustmentRequest request = new StockAdjustmentRequest(-20);

        assertThatThrownBy(() -> productService.adjustStock(1L, request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Stock insuffisant");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Suppression d'un produit existant")
    void deleteById_whenValid_deletesProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteById(1L);

        verify(productRepository).delete(testProduct);
    }

    @Test
    @DisplayName("Suppression d'un produit inexistant (RG7)")
    void deleteById_whenNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).delete(any(Product.class));
    }
}
