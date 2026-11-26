package eadl.productgestion.repositories;

import eadl.productgestion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserEmailAndIdNot(String userEmail, Long id);

    Optional<User> findByUserEmail(String userEmail);
}
