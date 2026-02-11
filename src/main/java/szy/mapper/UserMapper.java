package szy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import szy.entity.User;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT user_id, account, name, dept_id FROM \"user\"")
    List<User> selectAll();

    @Select("SELECT user_id, account, name, dept_id FROM \"user\" WHERE user_id = #{userId}")
    User selectById(Integer userId);

    @Select("SELECT user_id, account, name, dept_id FROM \"user\" WHERE account = #{account}")
    User selectByAccount(String account);
}
