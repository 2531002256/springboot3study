package szy.controller;

import org.slf4j.Logger;  // 新增：导入SLF4J Logger
import org.slf4j.LoggerFactory;  // 新增：导入LoggerFactory
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
    // 新增：定义Logger实例（绑定当前类，方便定位日志来源）
    private static final Logger log = LoggerFactory.getLogger(UserYearScoreController.class);

    private final UserYearScoreService userYearScoreService;

    // 分页查询
    @GetMapping
    public Result<Page<UserYearScore>> getUserYearScorePage(
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        // 新增：记录接口入参
        log.info("接收到用户年度分数分页查询请求，页码：{}，每页条数：{}", pageNum, pageSize);
        try {
            Page<UserYearScore> userYearScorePage = userYearScoreService.getUserYearScorePage(pageNum, pageSize);
            // 新增：记录响应结果（包含分页核心数据）
            log.info("用户年度分数分页查询完成，总条数：{}，总页数：{}，当前页条数：{}",
                    userYearScorePage.getTotalElements(), userYearScorePage.getTotalPages(), userYearScorePage.getNumberOfElements());
            return Result.success(userYearScorePage);
        } catch (Exception e) {
            // 新增：记录异常（保留堆栈）
            log.error("用户年度分数分页查询接口异常，页码：{}，每页条数：{}", pageNum, pageSize, e);
            return Result.error("用户年度分数分页查询失败：" + e.getMessage());
        }
    }

    // id查询
    @GetMapping("/{id}")
    public Result<UserYearScore> getUserYearScoreById(@PathVariable("id") Integer id) {
        // 新增：记录接口入参
        log.info("接收到单个用户年度分数查询请求，分数ID：{}", id);
        try {
            Result<UserYearScore> result = userYearScoreService.getUserYearScoreById(id)
                    .map(score -> {
                        // 新增：DEBUG级别记录查询成功详情
                        log.debug("分数ID：{} 查询成功，用户ID：{}，年份：{}，分数：{}",
                                id, score.getUserId(), score.getYear(), score.getScore());
                        return Result.success(score);
                    })
                    .orElseGet(() -> {
                        // 新增：WARN级别记录业务错误
                        log.warn("分数ID：{} 查询失败，原因：该分数ID不存在", id);
                        return Result.error("用户年度分数ID不存在：" + id);
                    });
            return result;
        } catch (Exception e) {
            // 新增：记录异常
            log.error("单个用户年度分数查询接口异常，分数ID：{}", id, e);
            return Result.error("用户年度分数查询失败：" + e.getMessage());
        }
    }

    // 按年份统计各部门总分
    @GetMapping("/statistics/dept")
    public Result<List<DeptTotalScoreDTO>> getDeptTotalScoreByYear(@RequestParam String year) {
        // 新增：记录接口入参
        log.info("接收到按年份统计部门总分请求，统计年份：{}", year);
        try {
            List<DeptTotalScoreDTO> result = userYearScoreService.getDeptTotalScoreByYear(year);
            // 新增：记录响应结果（统计结果条数）
            log.info("按年份{}统计部门总分完成，共统计到{}个部门的分数", year, result.size());
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            // 新增：WARN级别记录参数校验异常
            log.warn("按年份{}统计部门总分失败：参数校验异常 - {}", year, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 新增：ERROR级别记录系统异常
            log.error("按年份{}统计部门总分接口异常", year, e);
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
        // 新增：记录接口入参（核心业务参数）
        log.info("接收到部门历年总分统计请求，部门ID：{}，统计年份范围：{} - {}", deptId, startYear, endYear);
        try {
            List<YearScoreDTO> result = userYearScoreService.getDeptYearlyScoreByRange(deptId, startYear, endYear);
            // 新增：记录响应结果（统计年份数）
            log.info("部门ID{}历年总分统计完成，统计年份数：{}（{} - {}）",
                    deptId, result.size(), startYear, endYear);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            // 新增：WARN级别记录参数校验异常
            log.warn("部门ID{}历年总分统计失败：参数校验异常 - {}（年份范围：{} - {}）",
                    deptId, e.getMessage(), startYear, endYear);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 新增：ERROR级别记录系统异常
            log.error("部门ID{}历年总分统计接口异常（年份范围：{} - {}）", deptId, startYear, endYear, e);
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
        // 新增：记录接口入参（文件名、文件大小，核心业务参数）
        String fileName = file.getOriginalFilename();
        long fileSizeKb = file.getSize() / 1024;
        log.info("接收到Excel导入用户年度分数请求，文件名：{}，文件大小：{}KB", fileName, fileSizeKb);

        try {
            ImportResultDTO result = userYearScoreService.importUserYearScore(file);
            // 新增：记录响应结果（导入成功/失败条数，核心业务结果）
            log.info("Excel导入用户年度分数完成，成功条数：{}，失败条数：{}",
                    result.getSuccessCount(), result.getFailCount());
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            // 新增：WARN级别记录参数/文件校验异常
            log.warn("Excel导入用户年度分数失败：文件/参数校验异常 - {}（文件名：{}）", e.getMessage(), fileName);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 新增：ERROR级别记录系统异常（保留堆栈）
            log.error("Excel导入用户年度分数接口异常（文件名：{}）", fileName, e);
            return Result.error("Excel导入失败：" + e.getMessage());
        }
    }

}
