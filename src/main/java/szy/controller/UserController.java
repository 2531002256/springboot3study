package szy.controller;

import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import szy.common.Result;
import szy.entity.User;
import szy.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping
    public Result<PageInfo<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("用户分页：{} {}", pageNum, pageSize);
        try {
            PageInfo<User> page = userService.getUserPage(pageNum, pageSize);
            log.info("总用户：{}", page.getTotal());
            return Result.success(page);
        } catch (Exception e) {
            log.error("分页异常", e);
            return Result.error("失败");
        }
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        log.info("查询用户：{}", id);
        try {
            User user = userService.getUserById(id);
            return user != null ? Result.success(user) : Result.error("用户不存在");
        } catch (Exception e) {
            log.error("异常", e);
            return Result.error("失败");
        }
    }
}
