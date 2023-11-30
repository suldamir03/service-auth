package nomadteam.auth.persistence.repo;

import nomadteam.auth.persistence.entity.ERole;
import nomadteam.auth.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findRoleByName(ERole name);
}
