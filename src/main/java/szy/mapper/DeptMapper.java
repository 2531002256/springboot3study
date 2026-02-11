package szy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import szy.entity.Dept;
import java.util.List;

@Mapper
public interface DeptMapper {
    @Select("SELECT dept_id, dept_name FROM dept")
    List<Dept> selectAll();

    @Select("SELECT dept_id, dept_name FROM dept WHERE dept_id = #{deptId}")
    Dept selectById(Integer deptId);
}
