package eadl.productgestion.repositories;

import eadl.productgestion.entity.User;
import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder()
                .userName("Alice Dupont")
                .userEmail("alice@example.com")
                .userPassword("secret123")
                .userRole(UserRole.ADMIN)
                .userAccountStatus(UserAccountStatus.ACTIVE)
                .build());

        user2 = userRepository.save(User.builder()
                .userName("Bob Martin")
                .userEmail("bob@example.com")
                .userPassword("secret456")
                .userRole(UserRole.CLIENT)
                .userAccountStatus(UserAccountStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("existsByUserEmail : retourne true quand l'email existe")
    void existsByUserEmail_whenExists_returnsTrue() {
        boolean exists = userRepository.existsByUserEmail("alice@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserEmail : retourne false quand l'email n'existe pas")
    void existsByUserEmail_whenDoesNotExist_returnsFalse() {
        boolean exists = userRepository.existsByUserEmail("unknown@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByUserEmailAndIdNot : détecte un doublon sur un autre utilisateur")
    void existsByUserEmailAndIdNot_whenDuplicateOnDifferentUser_returnsTrue() {
        boolean exists = userRepository.existsByUserEmailAndIdNot("alice@example.com", user2.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserEmailAndIdNot : ignore l'utilisateur lui-même lors d'une mise à jour")
    void existsByUserEmailAndIdNot_whenSameUser_returnsFalse() {
        boolean exists = userRepository.existsByUserEmailAndIdNot("alice@example.com", user1.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByUserEmail : retrouve l'utilisateur par son email")
    void findByUserEmail_whenExists_returnsUser() {
        Optional<User> found = userRepository.findByUserEmail("bob@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("Bob Martin");
    }
}
