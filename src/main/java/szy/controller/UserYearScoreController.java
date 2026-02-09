package szy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import szy.common.Result;
import szy.dto.DeptTotalScoreDTO;
import szy.dto.ImportResultDTO;
import szy.dto.YearScoreDTO;
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

    // id查询
    @GetMapping("/{id}")
    public Result<UserYearScore> getUserYearScoreById(@PathVariable("id") Integer id) {
        return userYearScoreService.getUserYearScoreById(id)
                .map(Result::success)
                .orElse(Result.error("用户年度分数ID不存在：" + id));
    }

    // 按年份统计各部门总分
    @GetMapping("/statistics/dept")
    public Result<List<DeptTotalScoreDTO>> getDeptTotalScoreByYear(@RequestParam String year) {
        try {
            List<DeptTotalScoreDTO> result = userYearScoreService.getDeptTotalScoreByYear(year);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("统计部门总分失败：" + e.getMessage());
        }
    }

    /**
     * 新增：根据部门ID、开始年份、结束年份统计历年总分（自动补全无数据年份为0）
     * 请求示例：GET /userYearScores/statistics/dept/yearly?deptId=1&startYear=2021&endYear=2025
     * @param deptId 部门ID（必传）
     * @param startYear 开始年份（4位数字，必传）
     * @param endYear 结束年份（4位数字，必传）
     * @return 连续年份的分数列表（无数据年份分数为0）
     */
    @GetMapping("/statistics/dept/yearly")
    public Result<List<YearScoreDTO>> getDeptYearlyScoreByRange(
            @RequestParam Integer deptId,
            @RequestParam String startYear,
            @RequestParam String endYear
    ) {
        try {
            List<YearScoreDTO> result = userYearScoreService.getDeptYearlyScoreByRange(deptId, startYear, endYear);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            // 参数校验异常，返回友好提示
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 通用异常，返回兜底提示
            return Result.error("统计部门历年总分失败：" + e.getMessage());
        }
    }

    /**
     * Excel上传导入用户年度分数
     * 请求示例：POST /userYearScores/import （form-data格式，key=file，value=Excel文件）
     * @param file 上传的Excel文件
     * @return 导入结果
     */
    @PostMapping("/import")
    public Result<ImportResultDTO> importUserYearScore(@RequestParam("file") MultipartFile file) {
        try {
            ImportResultDTO result = userYearScoreService.importUserYearScore(file);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            // 参数/文件校验异常
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 通用异常
            return Result.error("Excel导入失败：" + e.getMessage());
        }
    }

}
