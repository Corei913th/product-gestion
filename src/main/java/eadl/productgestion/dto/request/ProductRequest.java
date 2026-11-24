package eadl.productgestion.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "La référence (SKU) est obligatoire")
        @Pattern(regexp = "^[A-Z0-9-]{3,20}$", message = "La référence SKU doit comporter de 3 à 20 caractères majuscules, chiffres ou tirets")
        String sku,

        @NotBlank(message = "Le nom du produit est obligatoire")
        @Size(min = 3, max = 100, message = "Le nom doit comporter entre 3 et 100 caractères")
        String name,

        @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
        String description,

        @NotNull(message = "Le prix est obligatoire")
        @Positive(message = "Le prix doit être strictement supérieur à 0")
        BigDecimal price,

        @Min(value = 0, message = "La quantité en stock doit être supérieure ou égale à 0")
        int quantity,

        @NotNull(message = "L'identifiant de la catégorie est obligatoire")
        Long categoryId
) {
}
