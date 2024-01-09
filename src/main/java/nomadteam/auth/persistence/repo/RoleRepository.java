package nomadteam.auth.persistence.repo;

import java.util.Optional;
import nomadteam.auth.persistence.entity.ERole;
import nomadteam.auth.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByName(ERole name);
}
