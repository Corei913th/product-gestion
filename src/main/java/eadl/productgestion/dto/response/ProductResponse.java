package eadl.productgestion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Représentation détaillée d'un produit")
public record ProductResponse(
        @Schema(description = "Identifiant unique généré", example = "7")
        Long id,

        @Schema(description = "Référence unique du produit (SKU)", example = "LAP-001")
        String sku,

        @Schema(description = "Nom du produit", example = "Ordinateur portable 15 pouces")
        String name,

        @Schema(description = "Description du produit", example = "8 Go de RAM, SSD 256 Go")
        String description,

        @Schema(description = "Prix unitaire", example = "349000.00")
        BigDecimal price,

        @Schema(description = "Quantité actuellement en stock", example = "12")
        int quantity,

        @Schema(description = "Nom de la catégorie associée", example = "Informatique")
        String categoryName,

        @Schema(description = "Date et heure de création")
        Instant createdAt,

        @Schema(description = "Date et heure de dernière modification")
        Instant updatedAt
) {
}
