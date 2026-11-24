package eadl.productgestion.controller;

import eadl.productgestion.dto.request.ProductRequest;
import eadl.productgestion.dto.request.StockAdjustmentRequest;
import eadl.productgestion.dto.response.ProductResponse;
import eadl.productgestion.exception.GlobalExceptionHandler;
import eadl.productgestion.exception.InsufficientStockException;
import eadl.productgestion.exception.ProductNotFoundException;
import eadl.productgestion.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("POST /api/v1/products - Succès (201 Created + Location)")
    void create_whenValid_returns201AndLocationHeader() throws Exception {
        ProductResponse response = new ProductResponse(
                7L,
                "LAP-001",
                "Ordinateur portable 15 pouces",
                "8 Go de RAM",
                new BigDecimal("349000.00"),
                12,
                "Informatique",
                Instant.now(),
                Instant.now()
        );

        when(productService.create(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sku": "LAP-001",
                                    "name": "Ordinateur portable 15 pouces",
                                    "description": "8 Go de RAM",
                                    "price": 349000.00,
                                    "quantity": 12,
                                    "categoryId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/products/7"))
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.sku").value("LAP-001"))
                .andExpect(jsonPath("$.name").value("Ordinateur portable 15 pouces"));
    }

    @Test
    @DisplayName("POST /api/v1/products - Données invalides (400 Bad Request avec violations)")
    void create_whenInvalid_returns400WithViolations() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sku": "invalid sku!",
                                    "name": "",
                                    "price": -10.0,
                                    "quantity": -5,
                                    "categoryId": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.violations").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Produit existant (200 OK)")
    void findById_whenExists_returns200() throws Exception {
        ProductResponse response = new ProductResponse(
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

        when(productService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sku").value("LAP-001"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Inexistant (404 Not Found avec PRODUCT_NOT_FOUND)")
    void findById_whenNotFound_returns404WithApiError() throws Exception {
        when(productService.findById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Produit introuvable : id=99"));
    }

    @Test
    @DisplayName("GET /api/v1/products - Liste paginée (200 OK)")
    void findAll_returns200AndPage() throws Exception {
        ProductResponse response = new ProductResponse(
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

        when(productService.findAll(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].sku").value("LAP-001"));
    }

    @Test
    @DisplayName("PATCH /api/v1/products/{id}/stock - Stock insuffisant (409 Conflict)")
    void adjustStock_whenInsufficient_returns409Conflict() throws Exception {
        when(productService.adjustStock(eq(1L), any(StockAdjustmentRequest.class)))
                .thenThrow(new InsufficientStockException(1L, 20, 10));

        mockMvc.perform(patch("/api/v1/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "delta": -20
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - Suppression valide (204 No Content)")
    void deleteById_whenValid_returns204() throws Exception {
        doNothing().when(productService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }
}
