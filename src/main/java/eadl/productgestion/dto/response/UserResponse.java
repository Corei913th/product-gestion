package eadl.productgestion.dto.response;

import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représentation d'un utilisateur")
public record UserResponse(
        @Schema(description = "Identifiant unique de l'utilisateur", example = "1")
        Long id,

        @Schema(description = "Nom complet de l'utilisateur", example = "Jean Mbarga")
        String userName,

        @Schema(description = "Adresse email de l'utilisateur", example = "jean.mbarga@example.com")
        String userEmail,

        @Schema(description = "Rôle de l'utilisateur", example = "CLIENT")
        UserRole userRole,

        @Schema(description = "Statut du compte utilisateur", example = "ACTIVE")
        UserAccountStatus userAccountStatus
) {
}
