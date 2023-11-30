package nomadteam.auth.persistence.repo;

import nomadteam.auth.persistence.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserCredentials, Long> {

    UserCredentials findUserCredentialsByUsername(String username);
}
