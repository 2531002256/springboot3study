package szy.controller;

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
        Page<User> userPage = userService.getUserPage(pageNum, pageSize);
        return Result.success(userPage);
    }

    /**
     * 接口2：根据ID查询单个用户
     * 请求示例：GET /users/1
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer userId) {
        return userService.getUserById(userId)
                .map(Result::success)
                .orElse(Result.error("用户ID不存在：" + userId));
    }
}
