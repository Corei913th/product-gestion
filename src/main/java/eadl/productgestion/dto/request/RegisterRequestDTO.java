package eadl.productgestion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données d'enregistrement d'un utilisateur")
public record RegisterRequestDTO(
        @Schema(description = "Nom complet de l'utilisateur", example = "Admin User")
        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 3, max = 50, message = "Le nom doit comporter entre 3 et 50 caractères")
        String userName,

        @Schema(description = "Adresse email de l'utilisateur", example = "admin@example.com")
        @NotBlank(message = "L'adresse email est obligatoire")
        @Email(message = "Le format de l'email est invalide")
        String userEmail,

        @Schema(description = "Mot de passe de l'utilisateur", example = "Password123!")
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit comporter au moins 6 caractères")
        String userPassword
) {}
