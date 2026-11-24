package eadl.productgestion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données requises pour créer une catégorie")
public record CategoryRequest(
        @Schema(description = "Nom unique de la catégorie", example = "Informatique")
        @NotBlank(message = "Le nom de la catégorie est obligatoire")
        @Size(min = 2, max = 100, message = "Le nom doit comporter entre 2 et 100 caractères")
        String name
) {
}
