package szy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import szy.common.Result;
import szy.entity.UserYearScore;
import szy.service.UserYearScoreService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userYearScores") // 统一接口前缀
public class UserYearScoreController {
    private final UserYearScoreService userYearScoreService;

    /**
     * 接口1：分页查询用户年度分数列表
     * 请求示例：GET /userYearScores?pageNum=0&pageSize=10
     */
    @GetMapping
    public Result<Page<UserYearScore>> getUserYearScorePage(
            @RequestParam(defaultValue = "0") Integer pageNum,  // 默认第1页（JPA页码从0开始）
            @RequestParam(defaultValue = "10") Integer pageSize // 默认每页10条
    ) {
        Page<UserYearScore> userYearScorePage = userYearScoreService.getUserYearScorePage(pageNum, pageSize);
        return Result.success(userYearScorePage);
    }

    /**
     * 接口2：根据ID查询单个用户年度分数
     * 请求示例：GET /userYearScores/1
     */
    @GetMapping("/{id}")
    public Result<UserYearScore> getUserYearScoreById(@PathVariable("id") Integer id) {
        return userYearScoreService.getUserYearScoreById(id)
                .map(Result::success)
                .orElse(Result.error("用户年度分数ID不存在：" + id));
    }
}
