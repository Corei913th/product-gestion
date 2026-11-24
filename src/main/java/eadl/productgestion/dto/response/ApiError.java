package eadl.productgestion.dto.response;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldViolation> violations
) {
    public static ApiError of(int status, String code, String message, String path, List<FieldViolation> violations) {
        return new ApiError(Instant.now(), status, code, message, path, violations != null ? violations : List.of());
    }

    public static ApiError of(int status, String code, String message, String path) {
        return of(status, code, message, path, List.of());
    }
}
