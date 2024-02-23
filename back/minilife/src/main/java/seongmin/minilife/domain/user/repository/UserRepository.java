package seongmin.minilife.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seongmin.minilife.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);

}
