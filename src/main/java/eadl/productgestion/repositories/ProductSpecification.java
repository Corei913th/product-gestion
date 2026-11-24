package eadl.productgestion.repositories;

import eadl.productgestion.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> filterBy(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        return Specification
                .where(hasName(name))
                .and(hasCategory(categoryId))
                .and(minPrice(minPrice))
                .and(maxPrice(maxPrice));
    }

    private static Specification<Product> hasName(String name) {
        return (root, query, cb) -> (name == null || name.isBlank()) ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    private static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null :
                cb.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null :
                cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null :
                cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}
