package szy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import szy.dto.DeptTotalScoreDTO;
import szy.entity.UserYearScore;

import java.util.List;

public interface UserYearScoreRepository extends JpaRepository<UserYearScore, Integer> {

    /**
     * 按年份统计每个部门的总分数
     * JPQL关联查询：UserYearScore → User → Dept，按部门分组求和
     */
    @Query("SELECT new szy.dto.DeptTotalScoreDTO(" +
            "d.deptId, d.deptName, SUM(ys.score)) " +
            "FROM UserYearScore ys " +
            "LEFT JOIN User u ON ys.userId = u.userId " +
            "LEFT JOIN Dept d ON u.deptId = d.deptId " +
            "WHERE ys.year = :year " +
            "GROUP BY d.deptId, d.deptName")
    List<DeptTotalScoreDTO> findDeptTotalScoreByYear(@Param("year") String year);
}
