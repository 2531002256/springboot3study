package szy.service;

import org.slf4j.Logger;  // 新增：导入SLF4J Logger
import org.slf4j.LoggerFactory;  // 新增：导入LoggerFactory
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
    // 新增：定义Logger实例（绑定当前类，方便定位日志来源）
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    /**
     * 分页查询用户列表
     */
    public Page<User> getUserPage(Integer pageNum, Integer pageSize) {
        // 新增：INFO级别记录入参（核心业务参数）
        log.info("开始处理用户分页查询，页码：{}，每页条数：{}", pageNum, pageSize);
        try {
            // 新增：DEBUG级别记录核心步骤（封装分页条件，开发调试用）
            log.debug("封装用户分页条件：PageRequest.of({}, {})", pageNum, pageSize);
            PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
            Page<User> userPage = userRepository.findAll(pageRequest);

            // 新增：INFO级别记录出参（核心结果数据）
            log.info("用户分页查询完成，总用户数：{}，总页数：{}",
                    userPage.getTotalElements(), userPage.getTotalPages());
            return userPage;
        } catch (Exception e) {
            // 新增：ERROR级别记录异常（保留堆栈，定位问题）
            log.error("用户分页查询失败，页码：{}，每页条数：{}", pageNum, pageSize, e);
            throw e; // 抛出异常，让Controller层统一处理响应
        }
    }

    /**
     * 根据ID查询单个用户
     */
    public Optional<User> getUserById(Integer userId) {
        // 新增：INFO级别记录入参（核心业务参数）
        log.info("开始处理单个用户查询，用户ID：{}", userId);
        try {
            Optional<User> userOptional = userRepository.findById(userId);

            // 新增：根据查询结果记录不同级别日志（适配User实体常见字段）
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // DEBUG级别：记录查询成功的详情（开发调试用，仅保留User实体存在的字段）
                log.debug("用户ID：{} 查询成功，账号：{}，用户名：{}，所属部门ID：{}",
                        userId, user.getAccount(), user.getName(), user.getDeptId());
            } else {
                // WARN级别：记录业务级失败（用户ID不存在，需关注但非系统异常）
                log.warn("用户ID：{} 查询失败，原因：该用户ID不存在", userId);
            }
            return userOptional;
        } catch (Exception e) {
            // 新增：ERROR级别记录异常（保留堆栈）
            log.error("单个用户查询失败，用户ID：{}", userId, e);
            throw e; // 抛出异常，让Controller层统一处理响应
        }
    }
}
