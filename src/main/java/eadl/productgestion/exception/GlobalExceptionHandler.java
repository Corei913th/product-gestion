package eadl.productgestion.exception;

import eadl.productgestion.dto.response.ApiError;
import eadl.productgestion.dto.response.FieldViolation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest req) {
        log.warn("Produit non trouvé : {} - URI: {}", ex.getMessage(), req.getRequestURI());
        return build(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoryNotFound(CategoryNotFoundException ex, HttpServletRequest req) {
        log.warn("Catégorie non trouvée : {} - URI: {}", ex.getMessage(), req.getRequestURI());
        return build(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        log.warn("Utilisateur non trouvé : {} - URI: {}", ex.getMessage(), req.getRequestURI());
        return build(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(DuplicateUserEmailException.class)
    public ResponseEntity<ApiError> handleDuplicateUserEmail(DuplicateUserEmailException ex, HttpServletRequest req) {
        log.warn("Conflit email utilisateur : {} - URI: {}", ex.getMessage(), req.getRequestURI());
        return build(HttpStatus.CONFLICT, ex.getCode(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ApiError> handleDuplicateProduct(DuplicateProductException ex, HttpServletRequest req) {
        log.warn("Conflit produit (SKU existant) : {} - URI: {}", ex.getMessage(), req.getRequestURI());
        return build(HttpStatus.CONFLICT, ex.getCode(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<ApiError> handleDuplicateCategory(DuplicateCategoryException ex, HttpServletRequest req) {
        log.warn("Conflit catégorie : {} - URI: {}", ex.getMessage(), req.getRequestURI());
        return build(HttpStatus.CONFLICT, ex.getCode(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiError> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest req) {
        log.warn("Stock insuffisant : {} - URI: {}", ex.getMessage(), req.getRequestURI());
        return build(HttpStatus.CONFLICT, ex.getCode(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, HttpServletRequest req) {
        log.warn("Erreur métier : {} - Code: {} - URI: {}", ex.getMessage(), ex.getCode(), req.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, ex.getCode(), ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Argument invalide : {} - URI: {}", ex.getMessage(), req.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldViolation(e.getField(), e.getDefaultMessage()))
                .toList();
        log.warn("Validation échouée sur {} : {} violation(s)", req.getRequestURI(), violations.size());
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Données invalides", req, violations);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Erreur inattendue sur {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Une erreur interne est survenue", req, List.of());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String message, HttpServletRequest req, List<FieldViolation> violations) {
        ApiError error = ApiError.of(status.value(), code, message, req.getRequestURI(), violations);
        return ResponseEntity.status(status).body(error);
    }
}
