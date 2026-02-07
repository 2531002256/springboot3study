package szy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import szy.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @EntityGraph(attributePaths = {"dept", "userYearScoreList"})
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findUserWithRelations(@Param("userId") Integer userId);
}
