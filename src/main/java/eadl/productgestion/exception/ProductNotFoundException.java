package eadl.productgestion.exception;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(Long id) {
        super("PRODUCT_NOT_FOUND", "Produit introuvable : id=" + id);
    }
}
