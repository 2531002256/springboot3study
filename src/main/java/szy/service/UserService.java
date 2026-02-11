package szy.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import szy.entity.User;
import szy.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserMapper userMapper;

    public PageInfo<User> getUserPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(userMapper.selectAll());
    }

    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }
}
