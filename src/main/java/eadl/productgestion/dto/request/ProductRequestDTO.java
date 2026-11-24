package eadl.productgestion.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ProductRequestDTO(

        @NotBlank
        String name,

        @NotBlank
        String code,

        @Positive
        Double price,

        @Positive
        int quantity,

        @Future
        LocalDateTime expirationDate
) {
}
