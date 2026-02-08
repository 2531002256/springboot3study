package szy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import szy.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {
    // 仅继承，复用JPA默认的分页findAll(PageRequest)和单查findById(Integer)
}
