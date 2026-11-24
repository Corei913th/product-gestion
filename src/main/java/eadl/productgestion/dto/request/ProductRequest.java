package eadl.productgestion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Données nécessaires à la création ou modification d'un produit")
public record ProductRequest(
        @Schema(description = "Référence unique du produit (SKU)", example = "LAP-001")
        @NotBlank(message = "La référence (SKU) est obligatoire")
        @Pattern(regexp = "^[A-Z0-9-]{3,20}$", message = "La référence SKU doit comporter de 3 à 20 caractères majuscules, chiffres ou tirets")
        String sku,

        @Schema(description = "Nom du produit", example = "Ordinateur portable 15 pouces")
        @NotBlank(message = "Le nom du produit est obligatoire")
        @Size(min = 3, max = 100, message = "Le nom doit comporter entre 3 et 100 caractères")
        String name,

        @Schema(description = "Description détaillée du produit (facultative)", example = "8 Go de RAM, SSD 256 Go")
        @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
        String description,

        @Schema(description = "Prix unitaire strictement positif", example = "349000.00")
        @NotNull(message = "Le prix est obligatoire")
        @Positive(message = "Le prix doit être strictement supérieur à 0")
        BigDecimal price,

        @Schema(description = "Quantité en stock (>= 0)", example = "12")
        @Min(value = 0, message = "La quantité en stock doit être supérieure ou égale à 0")
        int quantity,

        @Schema(description = "Identifiant de la catégorie associée", example = "1")
        @NotNull(message = "L'identifiant de la catégorie est obligatoire")
        Long categoryId
) {
}
