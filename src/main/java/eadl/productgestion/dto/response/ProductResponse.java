package eadl.productgestion.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        int quantity,
        String categoryName,
        Instant createdAt,
        Instant updatedAt
) {
}
