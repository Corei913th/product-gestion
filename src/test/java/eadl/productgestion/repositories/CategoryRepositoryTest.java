package eadl.productgestion.repositories;

import eadl.productgestion.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        categoryRepository.save(new Category("Informatique"));
    }

    @Test
    @DisplayName("existsByNameIgnoreCase : retourne true quand le nom existe (insensible à la casse)")
    void existsByNameIgnoreCase_whenExists_returnsTrue() {
        boolean exists = categoryRepository.existsByNameIgnoreCase("informatique");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByNameIgnoreCase : retourne false quand le nom n'existe pas")
    void existsByNameIgnoreCase_whenDoesNotExist_returnsFalse() {
        boolean exists = categoryRepository.existsByNameIgnoreCase("Mobilier");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByNameIgnoreCase : retrouve la catégorie existante")
    void findByNameIgnoreCase_whenExists_returnsCategory() {
        Optional<Category> found = categoryRepository.findByNameIgnoreCase("INFORMATIQUE");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Informatique");
    }
}
