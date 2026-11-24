package eadl.productgestion.controller;

import eadl.productgestion.dto.request.CategoryRequest;
import eadl.productgestion.dto.response.ApiError;
import eadl.productgestion.dto.response.CategoryResponse;
import eadl.productgestion.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories", description = "Gestion du catalogue de catégories de produits")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Créer une nouvelle catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Nom de catégorie déjà utilisé",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Lister toutes les catégories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @Operation(summary = "Consulter une catégorie par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catégorie trouvée",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Catégorie introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }
}
