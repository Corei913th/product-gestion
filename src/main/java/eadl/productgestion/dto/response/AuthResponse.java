package eadl.productgestion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'authentification contenant le jeton JWT")
public record AuthResponse(
        @Schema(description = "Jeton d'accès JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Type de jeton", example = "Bearer")
        String tokenType,

        @Schema(description = "Informations sur l'utilisateur connecté")
        UserResponse user
) {
}
