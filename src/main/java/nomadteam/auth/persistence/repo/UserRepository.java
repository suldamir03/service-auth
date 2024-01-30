package nomadteam.auth.persistence.repo;

import java.util.Collection;
import java.util.List;
import nomadteam.auth.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIdIn(Collection<Long> id);

}
