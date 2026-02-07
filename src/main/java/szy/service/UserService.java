package szy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import szy.entity.User;
import szy.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * 分页查询用户（关联部门+成绩）
     * @param pageNum 页码（从0开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    public Page<User> getUserPage(Integer pageNum, Integer pageSize) {
        // 按用户ID倒序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "userId");
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, sort);
        // JPA自动关联查询（已通过EntityGraph优化性能）
        return userRepository.findAll(pageRequest);
    }
}
