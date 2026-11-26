package eadl.productgestion.controller;

import eadl.productgestion.dto.request.CategoryRequest;
import eadl.productgestion.dto.response.CategoryResponse;
import eadl.productgestion.exception.CategoryNotFoundException;
import eadl.productgestion.exception.DuplicateCategoryException;
import eadl.productgestion.exception.GlobalExceptionHandler;
import eadl.productgestion.services.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("POST /api/v1/categories - Succès (201 Created + Location)")
    void create_whenValid_returns201AndLocationHeader() throws Exception {
        CategoryResponse response = new CategoryResponse(1L, "Informatique");

        when(categoryService.create(any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Informatique"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/categories/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Informatique"));
    }

    @Test
    @DisplayName("POST /api/v1/categories - Nom vide (400 Bad Request)")
    void create_whenBlankName_returns400WithValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.violations").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/categories - Doublon (409 Conflict)")
    void create_whenDuplicate_returns409Conflict() throws Exception {
        when(categoryService.create(any(CategoryRequest.class)))
                .thenThrow(new DuplicateCategoryException("Informatique"));

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Informatique"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_CATEGORY"));
    }

    @Test
    @DisplayName("GET /api/v1/categories - Succès (200 OK)")
    void findAll_returns200AndCategoryList() throws Exception {
        List<CategoryResponse> categories = List.of(
                new CategoryResponse(1L, "Informatique"),
                new CategoryResponse(2L, "Téléphonie")
        );

        when(categoryService.findAll()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Informatique"))
                .andExpect(jsonPath("$[1].name").value("Téléphonie"));
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} - Succès (200 OK)")
    void findById_whenExists_returns200AndCategory() throws Exception {
        CategoryResponse response = new CategoryResponse(1L, "Informatique");
        when(categoryService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Informatique"));
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} - Introuvable (404 Not Found)")
    void findById_whenNotFound_returns404AndApiError() throws Exception {
        when(categoryService.findById(99L)).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(get("/api/v1/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }
}
