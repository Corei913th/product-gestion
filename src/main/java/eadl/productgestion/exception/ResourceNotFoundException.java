package eadl.productgestion.exception;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String code;

    public ResourceNotFoundException(String message) {
        super(message);
        this.code = "RESOURCE_NOT_FOUND";
    }

    public ResourceNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }


    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found.");
        this.code = "RESOURCE_NOT_FOUND";
    }
}