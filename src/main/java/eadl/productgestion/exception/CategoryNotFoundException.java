package eadl.productgestion.exception;

public class CategoryNotFoundException extends BusinessException {

    public CategoryNotFoundException(Long id) {
        super("CATEGORY_NOT_FOUND", "Catégorie introuvable : id=" + id);
    }
}
