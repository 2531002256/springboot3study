package szy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import szy.dto.DeptTotalScoreDTO;
import szy.dto.YearScoreDTO;
import szy.entity.UserYearScore;

import java.util.List;
import java.util.Optional;
public interface UserYearScoreRepository extends JpaRepository<UserYearScore, Integer> {

    // 原有：按年份统计每个部门的总分数
    @Query("SELECT new szy.dto.DeptTotalScoreDTO(" +
            "d.deptId, d.deptName, SUM(ys.score)) " +
            "FROM UserYearScore ys " +
            "LEFT JOIN User u ON ys.userId = u.userId " +
            "LEFT JOIN Dept d ON u.deptId = d.deptId " +
            "WHERE ys.year = :year " +
            "GROUP BY d.deptId, d.deptName")
    List<DeptTotalScoreDTO> findDeptTotalScoreByYear(@Param("year") String year);

    /**
     * 新增：查询指定部门、指定年份区间内的各年总分
     * @param deptId 部门ID
     * @param startYear 开始年份（4位数字，如2021）
     * @param endYear 结束年份（4位数字，如2025）
     * @return 该部门在年份区间内有数据的年份+对应总分
     */
    @Query("SELECT new szy.dto.YearScoreDTO(" +
            "ys.year, SUM(ys.score)) " +
            "FROM UserYearScore ys " +
            "LEFT JOIN User u ON ys.userId = u.userId " +
            "WHERE u.deptId = :deptId " +
            "AND ys.year BETWEEN :startYear AND :endYear " +
            "GROUP BY ys.year " +
            "ORDER BY ys.year ASC")
    List<YearScoreDTO> findDeptYearlyScore(@Param("deptId") Integer deptId,
                                           @Param("startYear") String startYear,
                                           @Param("endYear") String endYear);


    /**
     * 根据用户ID+年度查询是否存在记录（判断重复）
     */
    Optional<UserYearScore> findByUserIdAndYear(Integer userId, String year);
}
