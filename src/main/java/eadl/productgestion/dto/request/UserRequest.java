package eadl.productgestion.dto.request;

import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données requises pour créer ou modifier un utilisateur")
public record UserRequest(
        @Schema(description = "Nom complet de l'utilisateur", example = "Jean Mbarga")
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 2, max = 60, message = "Le nom doit comporter entre 2 et 60 caractères")
        String userName,

        @Schema(description = "Adresse email unique", example = "jean.mbarga@example.com")
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        String userEmail,

        @Schema(description = "Mot de passe de l'utilisateur", example = "P@ssword123")
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit comporter au moins 6 caractères")
        String userPassword,

        @Schema(description = "Rôle de l'utilisateur (ADMIN ou CLIENT)", example = "CLIENT")
        UserRole userRole,

        @Schema(description = "Statut du compte (ACTIVE ou BLOCKED)", example = "ACTIVE")
        UserAccountStatus userAccountStatus
) {
    public UserRequest {
        if (userRole == null) {
            userRole = UserRole.CLIENT;
        }
        if (userAccountStatus == null) {
            userAccountStatus = UserAccountStatus.ACTIVE;
        }
    }
}
