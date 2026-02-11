package szy.controller;

import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserYearScoreService userYearScoreService;

    @GetMapping
    public Result<PageInfo<UserYearScore>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        PageInfo<UserYearScore> page = userYearScoreService.getUserYearScorePage(pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<UserYearScore> get(@PathVariable Integer id) {
        UserYearScore score = userYearScoreService.getUserYearScoreById(id);
        return score != null ? Result.success(score) : Result.error("不存在");
    }

    @GetMapping("/statistics/dept")
    public Result<List<DeptTotalScoreDTO>> deptStat(@RequestParam String year) {
        return Result.success(userYearScoreService.getDeptTotalScoreByYear(year));
    }

    @GetMapping("/statistics/dept/yearly")
    public Result<List<YearScoreDTO>> yearly(
            @RequestParam Integer deptId,
            @RequestParam String startYear,
            @RequestParam String endYear
    ) {
        return Result.success(userYearScoreService.getDeptYearlyScoreByRange(deptId, startYear, endYear));
    }

    @PostMapping("/import")
    public Result<ImportResultDTO> importExcel(@RequestParam MultipartFile file) {
        return Result.success(userYearScoreService.importUserYearScore(file));
    }
}
