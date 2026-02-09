package szy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import szy.dto.DeptTotalScoreDTO;
import szy.dto.YearScoreDTO;
import szy.entity.User;
import szy.entity.UserYearScore;
import szy.repository.UserYearScoreRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserYearScoreService {
    private final UserYearScoreRepository userYearScoreRepository;

    // 分页查询
    public Page<UserYearScore> getUserYearScorePage(Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        return userYearScoreRepository.findAll(pageRequest);
    }

    //根据ID查询单个用户年度分数
    public Optional<UserYearScore> getUserYearScoreById(Integer id) {

        return userYearScoreRepository.findById(id);
    }

    // 查询特定年份的各部门总分数
    public List<DeptTotalScoreDTO> getDeptTotalScoreByYear(String year) {
        if (!StringUtils.hasText(year)) {
            throw new IllegalArgumentException("年份不能为空");
        }
        if (!year.matches("^\\d{4}$")) {
            throw new IllegalArgumentException("年份格式错误，需为4位数字（如2024）");
        }
        return userYearScoreRepository.findDeptTotalScoreByYear(year);
    }

    /**
     * 新增：根据部门ID、开始年份、结束年份统计历年总分，自动补全无数据年份分数为0
     * @param deptId 部门ID（必传）
     * @param startYear 开始年份（4位数字，如2021）
     * @param endYear 结束年份（4位数字，如2025）
     * @return 连续年份的分数列表（无数据年份分数为0）
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    public List<YearScoreDTO> getDeptYearlyScoreByRange(Integer deptId, String startYear, String endYear) {
        // 1. 核心参数校验
        if (deptId == null || deptId <= 0) {
            throw new IllegalArgumentException("部门ID不能为空且必须为正整数");
        }
        if (!StringUtils.hasText(startYear) || !startYear.matches("^\\d{4}$")) {
            throw new IllegalArgumentException("开始年份不能为空且需为4位数字（如2021）");
        }
        if (!StringUtils.hasText(endYear) || !endYear.matches("^\\d{4}$")) {
            throw new IllegalArgumentException("结束年份不能为空且需为4位数字（如2025）");
        }

        // 2. 转换年份为整数，校验开始年份≤结束年份
        int start = Integer.parseInt(startYear);
        int end = Integer.parseInt(endYear);
        if (start > end) {
            throw new IllegalArgumentException("开始年份不能大于结束年份");
        }

        // 3. 生成start到end的连续年份列表（如2021~2025 → ["2021","2022","2023","2024","2025"]）
        List<String> allYears = new ArrayList<>();
        for (int year = start; year <= end; year++) {
            allYears.add(String.valueOf(year));
        }

        // 4. 查询数据库中该部门在年份区间内的有数据的年份总分
        List<YearScoreDTO> dbScores = userYearScoreRepository.findDeptYearlyScore(deptId, startYear, endYear);

        // 5. 将数据库结果转为Map（年份→总分），方便快速匹配
        Map<String, BigDecimal> yearScoreMap = new HashMap<>();
        for (YearScoreDTO dto : dbScores) {
            yearScoreMap.put(dto.getYear(), dto.getTotalScore() == null ? BigDecimal.ZERO : dto.getTotalScore());
        }

        // 6. 补全无数据年份，分数设为0
        List<YearScoreDTO> result = new ArrayList<>();
        for (String year : allYears) {
            BigDecimal score = yearScoreMap.getOrDefault(year, BigDecimal.ZERO);
            result.add(new YearScoreDTO(year, score));
        }

        return result;
    }
}
