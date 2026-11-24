package eadl.productgestion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Demande d'ajustement du stock d'un produit")
public record StockAdjustmentRequest(
        @Schema(description = "Variation de stock : positif pour une entrée, négatif pour une sortie (non nul)", example = "5")
        @NotNull(message = "La valeur d'ajustement (delta) est obligatoire")
        Integer delta
) {
    public StockAdjustmentRequest {
        if (delta != null && delta == 0) {
            throw new IllegalArgumentException("La valeur d'ajustement (delta) ne peut pas être égale à 0");
        }
    }
}
