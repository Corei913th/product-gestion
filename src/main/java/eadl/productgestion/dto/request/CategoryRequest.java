package eadl.productgestion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Le nom de la catégorie est obligatoire")
        @Size(min = 2, max = 100, message = "Le nom doit comporter entre 2 et 100 caractères")
        String name
) {
}
