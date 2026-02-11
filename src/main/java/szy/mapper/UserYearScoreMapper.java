package szy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import szy.dto.DeptTotalScoreDTO;
import szy.dto.YearScoreDTO;
import szy.entity.UserYearScore;
import java.util.List;

@Mapper
public interface UserYearScoreMapper {
    List<UserYearScore> selectAll();
    UserYearScore selectById(Integer id);
    List<DeptTotalScoreDTO> selectDeptTotalScoreByYear(@Param("year") String year);
    List<YearScoreDTO> selectDeptYearlyScore(@Param("deptId") Integer deptId,
                                             @Param("startYear") String startYear,
                                             @Param("endYear") String endYear);
    UserYearScore selectByUserIdAndYear(@Param("userId") Integer userId,
                                        @Param("year") String year);
    int insert(UserYearScore score);
}
