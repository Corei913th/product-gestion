package eadl.productgestion.dto.request;

import jakarta.validation.constraints.NotNull;

public record StockAdjustmentRequest(
        @NotNull(message = "La valeur d'ajustement (delta) est obligatoire")
        Integer delta
) {
    public StockAdjustmentRequest {
        if (delta != null && delta == 0) {
            throw new IllegalArgumentException("La valeur d'ajustement (delta) ne peut pas être égale à 0");
        }
    }
}
