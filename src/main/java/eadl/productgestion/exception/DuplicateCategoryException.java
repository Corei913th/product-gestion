package eadl.productgestion.exception;

public class DuplicateCategoryException extends BusinessException {

    public DuplicateCategoryException(String name) {
        super("DUPLICATE_CATEGORY", "La catégorie existe déjà : " + name);
    }
}
