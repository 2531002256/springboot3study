package szy.controller;

import org.slf4j.Logger;  // 新增：导入SLF4J Logger
import org.slf4j.LoggerFactory;  // 新增：导入LoggerFactory
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import szy.common.Result;
import szy.entity.User;
import szy.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users") // 统一接口前缀
public class UserController {
    // 新增：定义Logger实例（绑定当前类，方便定位日志来源）
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * 接口1：分页查询用户列表
     * 请求示例：GET /users?pageNum=0&pageSize=10
     */
    @GetMapping
    public Result<Page<User>> getUserPage(
            @RequestParam(defaultValue = "0") Integer pageNum,  // 默认第1页（JPA页码从0开始）
            @RequestParam(defaultValue = "10") Integer pageSize // 默认每页10条
    ) {
        // 新增：记录接口入参（INFO级别，生产环境也需保留）
        log.info("接收到用户分页查询请求，页码：{}，每页条数：{}", pageNum, pageSize);
        try {
            Page<User> userPage = userService.getUserPage(pageNum, pageSize);
            // 新增：记录响应结果（包含分页核心数据，便于排查“分页数据异常”问题）
            log.info("用户分页查询完成，总用户数：{}，总页数：{}，当前页用户数：{}",
                    userPage.getTotalElements(), userPage.getTotalPages(), userPage.getNumberOfElements());
            return Result.success(userPage);
        } catch (Exception e) {
            // 新增：记录异常（ERROR级别，必须传e保留堆栈，方便定位报错行）
            log.error("用户分页查询接口异常，页码：{}，每页条数：{}", pageNum, pageSize, e);
            return Result.error("用户分页查询失败：" + e.getMessage());
        }
    }

    /**
     * 接口2：根据ID查询单个用户
     * 请求示例：GET /users/1
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer userId) {
        // 新增：记录接口入参
        log.info("接收到单个用户查询请求，用户ID：{}", userId);
        try {
            Result<User> result = userService.getUserById(userId)
                    .map(user -> {
                        // 新增：记录查询成功（DEBUG级别，开发环境看详情，生产环境INFO级别不输出）
                        log.debug("用户ID：{} 查询成功，用户名：{}，账号：{}", userId, user.getName(), user.getAccount());
                        return Result.success(user);
                    })
                    .orElseGet(() -> {
                        // 新增：记录查询失败（WARN级别，非致命业务错误，需关注但不影响程序运行）
                        log.warn("用户ID：{} 查询失败，原因：该用户ID不存在", userId);
                        return Result.error("用户ID不存在：" + userId);
                    });
            return result;
        } catch (Exception e) {
            // 新增：记录异常
            log.error("单个用户查询接口异常，用户ID：{}", userId, e);
            return Result.error("用户查询失败：" + e.getMessage());
        }
    }
}
