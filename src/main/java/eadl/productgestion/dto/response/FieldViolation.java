package eadl.productgestion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Détail d'une violation de contrainte de validation")
public record FieldViolation(
        @Schema(description = "Nom du champ en erreur", example = "price")
        String field,

        @Schema(description = "Message explicatif de l'erreur", example = "doit être supérieur à 0")
        String message
) {
}
