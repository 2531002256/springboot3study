package szy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import szy.entity.UserYearScore;

public interface UserYearScoreRepository extends JpaRepository<UserYearScore, Integer> {
}
