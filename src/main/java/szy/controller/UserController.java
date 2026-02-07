package szy.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import szy.common.Result;
import szy.entity.User;
import szy.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 分页查询用户列表（关联部门+成绩）
     * @param pageNum 页码（默认0）
     * @param pageSize 每页条数（默认10）
     * @return JSON格式分页结果
     */
    @GetMapping("/users")
    public Result<Page<User>> getUserPage(
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<User> userPage = userService.getUserPage(pageNum, pageSize);
        return Result.success(userPage);
    }
}
