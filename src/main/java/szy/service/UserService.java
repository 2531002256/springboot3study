package szy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import szy.entity.User;
import szy.repository.UserRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * 分页查询用户列表
     */
    public Page<User> getUserPage(Integer pageNum, Integer pageSize) {
        // 封装分页条件
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        return userRepository.findAll(pageRequest);
    }

    /**
     * 根据ID查询单个用户
     */
    public Optional<User> getUserById(Integer userId) {

        return userRepository.findById(userId);
    }
}
