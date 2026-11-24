package eadl.productgestion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représentation d'une catégorie")
public record CategoryResponse(
        @Schema(description = "Identifiant unique de la catégorie", example = "1")
        Long id,

        @Schema(description = "Nom de la catégorie", example = "Informatique")
        String name
) {
}
