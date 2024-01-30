package nomadteam.auth.persistence.repo;

import java.util.Optional;
import java.util.UUID;
import nomadteam.auth.persistence.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {

    Optional<UserCredentials> findByUsername(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    UserCredentials findUserCredentialsByUsername(String username);

}
