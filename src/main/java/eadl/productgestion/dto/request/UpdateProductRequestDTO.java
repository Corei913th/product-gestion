package eadl.productgestion.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record UpdateProductRequestDTO(
        String name,

        String code,

        @Positive
        Double price,

        @Positive
        int quantity,

        @Future
        LocalDateTime expirationDate
) {
}
