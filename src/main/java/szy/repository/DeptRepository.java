package szy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import szy.entity.Dept;

public interface DeptRepository extends JpaRepository<Dept, Integer> {
}
