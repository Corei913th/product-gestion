package eadl.productgestion.repositories;

import eadl.productgestion.entity.Category;
import eadl.productgestion.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category informatiques;
    private Category telephonie;
    private Product laptop;
    private Product mouse;
    private Product phone;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        informatiques = categoryRepository.save(new Category("Informatique"));
        telephonie = categoryRepository.save(new Category("Téléphonie"));

        laptop = productRepository.save(Product.builder()
                .sku("LAP-001")
                .name("Ordinateur portable 15 pouces")
                .description("8 Go RAM")
                .price(new BigDecimal("350000.00"))
                .quantity(10)
                .category(informatiques)
                .build());

        mouse = productRepository.save(Product.builder()
                .sku("MOU-001")
                .name("Souris sans fil")
                .description("Optique")
                .price(new BigDecimal("15000.00"))
                .quantity(50)
                .category(informatiques)
                .build());

        phone = productRepository.save(Product.builder()
                .sku("PHO-001")
                .name("Smartphone 5G")
                .description("128 Go")
                .price(new BigDecimal("250000.00"))
                .quantity(20)
                .category(telephonie)
                .build());
    }

    @Test
    @DisplayName("existsBySku : retourne true quand le SKU existe")
    void existsBySku_whenSkuExists_returnsTrue() {
        boolean exists = productRepository.existsBySku("LAP-001");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsBySku : retourne false quand le SKU n'existe pas")
    void existsBySku_whenSkuDoesNotExist_returnsFalse() {
        boolean exists = productRepository.existsBySku("UNKNOWN-SKU");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsBySkuAndIdNot : détecte un doublon sur un autre produit")
    void existsBySkuAndIdNot_whenDuplicateOnDifferentProduct_returnsTrue() {
        boolean exists = productRepository.existsBySkuAndIdNot("LAP-001", mouse.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsBySkuAndIdNot : ignore le produit lui-même lors de la mise à jour")
    void existsBySkuAndIdNot_whenSameProduct_returnsFalse() {
        boolean exists = productRepository.existsBySkuAndIdNot("LAP-001", laptop.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findBySku : retourne le produit correspondant")
    void findBySku_whenExists_returnsProduct() {
        Optional<Product> found = productRepository.findBySku("PHO-001");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Smartphone 5G");
    }

    @Test
    @DisplayName("findAll avec Specification : recherche par nom partiel")
    void findAll_withNameFilter_returnsMatchingProducts() {
        Specification<Product> spec = ProductSpecification.filterBy("portable", null, null, null);
        Page<Product> page = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getSku()).isEqualTo("LAP-001");
    }

    @Test
    @DisplayName("findAll avec Specification : filtre par catégorie")
    void findAll_withCategoryFilter_returnsMatchingProducts() {
        Specification<Product> spec = ProductSpecification.filterBy(null, informatiques.getId(), null, null);
        Page<Product> page = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("findAll avec Specification : filtre par fourchette de prix")
    void findAll_withPriceRangeFilter_returnsMatchingProducts() {
        Specification<Product> spec = ProductSpecification.filterBy(
                null,
                null,
                new BigDecimal("10000.00"),
                new BigDecimal("200000.00")
        );
        Page<Product> page = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getSku()).isEqualTo("MOU-001");
    }

    @Test
    @DisplayName("findAll avec pagination et tri par prix décroissant")
    void findAll_withPaginationAndSorting_returnsPagedAndSortedProducts() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("price").descending());
        Page<Product> page = productRepository.findAll(pageRequest);

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent().get(0).getSku()).isEqualTo("LAP-001");
        assertThat(page.getContent().get(1).getSku()).isEqualTo("PHO-001");
        assertThat(page.getTotalElements()).isEqualTo(3);
    }
}
