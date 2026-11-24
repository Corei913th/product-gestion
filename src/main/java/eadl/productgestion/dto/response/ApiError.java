package eadl.productgestion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Format standardisé de réponse d'erreur de l'API")
public record ApiError(
        @Schema(description = "Horodatage de l'erreur")
        Instant timestamp,

        @Schema(description = "Code de statut HTTP", example = "404")
        int status,

        @Schema(description = "Code métier de l'erreur", example = "PRODUCT_NOT_FOUND")
        String code,

        @Schema(description = "Message descriptif de l'erreur", example = "Produit introuvable : id=99")
        String message,

        @Schema(description = "Chemin URI ayant provoqué l'erreur", example = "/api/v1/products/99")
        String path,

        @Schema(description = "Liste des éventuelles erreurs de validation")
        List<FieldViolation> violations
) {
    public static ApiError of(int status, String code, String message, String path, List<FieldViolation> violations) {
        return new ApiError(Instant.now(), status, code, message, path, violations != null ? violations : List.of());
    }

    public static ApiError of(int status, String code, String message, String path) {
        return of(status, code, message, path, List.of());
    }
}
