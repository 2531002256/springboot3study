package szy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import szy.common.Result;
import szy.dto.DeptTotalScoreDTO;
import szy.entity.UserYearScore;
import szy.service.UserYearScoreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userYearScores")
public class UserYearScoreController {
    private final UserYearScoreService userYearScoreService;

    // 分页查询
    @GetMapping
    public Result<Page<UserYearScore>> getUserYearScorePage(
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<UserYearScore> userYearScorePage = userYearScoreService.getUserYearScorePage(pageNum, pageSize);
        return Result.success(userYearScorePage);
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Result<UserYearScore> getUserYearScoreById(@PathVariable("id") Integer id) {
        return userYearScoreService.getUserYearScoreById(id)
                .map(Result::success)
                .orElse(Result.error("用户年度分数ID不存在：" + id));
    }

    /**
     * 新增：根据年份统计每个部门的总分数
     * 请求示例：GET /userYearScores/statistics/dept?year=2024
     * @param year 年份（4位数字）
     * @return 部门总分统计结果
     */
    @GetMapping("/statistics/dept")
    public Result<List<DeptTotalScoreDTO>> getDeptTotalScoreByYear(@RequestParam String year) {
        try {
            List<DeptTotalScoreDTO> result = userYearScoreService.getDeptTotalScoreByYear(year);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            // 捕获参数校验异常，返回友好提示
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回通用错误
            return Result.error("统计部门总分失败：" + e.getMessage());
        }
    }
}
