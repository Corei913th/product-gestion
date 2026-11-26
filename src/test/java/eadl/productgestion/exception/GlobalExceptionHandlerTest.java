package eadl.productgestion.exception;

import eadl.productgestion.dto.response.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    @DisplayName("Gérer ProductNotFoundException -> 404 NOT_FOUND")
    void handleProductNotFound_returns404() {
        ProductNotFoundException ex = new ProductNotFoundException(10L);
        ResponseEntity<ApiError> response = exceptionHandler.handleProductNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().code()).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(response.getBody().path()).isEqualTo("/api/v1/test");
    }

    @Test
    @DisplayName("Gérer CategoryNotFoundException -> 404 NOT_FOUND")
    void handleCategoryNotFound_returns404() {
        CategoryNotFoundException ex = new CategoryNotFoundException(5L);
        ResponseEntity<ApiError> response = exceptionHandler.handleCategoryNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().code()).isEqualTo("CATEGORY_NOT_FOUND");
    }

    @Test
    @DisplayName("Gérer DuplicateProductException -> 409 CONFLICT")
    void handleDuplicateProduct_returns409() {
        DuplicateProductException ex = new DuplicateProductException("LAP-001");
        ResponseEntity<ApiError> response = exceptionHandler.handleDuplicateProduct(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().code()).isEqualTo("DUPLICATE_SKU");
    }

    @Test
    @DisplayName("Gérer DuplicateCategoryException -> 409 CONFLICT")
    void handleDuplicateCategory_returns409() {
        DuplicateCategoryException ex = new DuplicateCategoryException("Informatique");
        ResponseEntity<ApiError> response = exceptionHandler.handleDuplicateCategory(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().code()).isEqualTo("DUPLICATE_CATEGORY");
    }

    @Test
    @DisplayName("Gérer InsufficientStockException -> 409 CONFLICT")
    void handleInsufficientStock_returns409() {
        InsufficientStockException ex = new InsufficientStockException(1L, 10, 20);
        ResponseEntity<ApiError> response = exceptionHandler.handleInsufficientStock(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().code()).isEqualTo("INSUFFICIENT_STOCK");
    }

    @Test
    @DisplayName("Gérer BusinessException générique -> 400 BAD_REQUEST")
    void handleBusinessException_returns400() {
        BusinessException ex = new BusinessException("BUSINESS_ERR", "Règle métier enfreinte") {};
        ResponseEntity<ApiError> response = exceptionHandler.handleBusinessException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("BUSINESS_ERR");
    }

    @Test
    @DisplayName("Gérer IllegalArgumentException -> 400 BAD_REQUEST")
    void handleIllegalArgument_returns400() {
        IllegalArgumentException ex = new IllegalArgumentException("Argument erroné");
        ResponseEntity<ApiError> response = exceptionHandler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("INVALID_ARGUMENT");
    }

    @Test
    @DisplayName("Gérer MethodArgumentNotValidException -> 400 avec FieldViolations")
    void handleValidation_returns400WithViolations() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("productRequest", "name", "Le nom est obligatoire");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodParameter parameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ApiError> response = exceptionHandler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().violations()).hasSize(1);
        assertThat(response.getBody().violations().getFirst().field()).isEqualTo("name");
    }

    @Test
    @DisplayName("Gérer Exception inattendue -> 500 INTERNAL_SERVER_ERROR")
    void handleUnexpected_returns500() {
        Exception ex = new RuntimeException("Erreur inattendue de base de données");
        ResponseEntity<ApiError> response = exceptionHandler.handleUnexpected(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
    }
}
