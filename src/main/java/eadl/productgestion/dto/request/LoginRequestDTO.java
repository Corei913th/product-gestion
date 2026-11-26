package eadl.productgestion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Identifiants de connexion")
public record LoginRequestDTO(
        @Schema(description = "Adresse email de l'utilisateur", example = "admin@example.com")
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        String userEmail,

        @Schema(description = "Mot de passe", example = "Password123!")
        @NotBlank(message = "Le mot de passe est obligatoire")
        String userPassword
) {
}
