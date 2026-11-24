package eadl.productgestion.dto.response;

import java.time.LocalDateTime;

public record ProductResponseDTO(
        Long productId,

        String name,

        String code,

        Double price,

        int quantity,

        LocalDateTime expirationDate
) {
}
