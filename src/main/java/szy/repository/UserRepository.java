package szy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import szy.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * 根据用户账号查询用户（核心：关联账号→userId）
     */
    Optional<User> findByAccount(String account);
}
