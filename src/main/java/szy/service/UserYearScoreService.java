package szy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import szy.dto.DeptTotalScoreDTO;
import szy.entity.UserYearScore;
import szy.repository.UserYearScoreRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserYearScoreService {
    private final UserYearScoreRepository userYearScoreRepository;

    // 分页查询方法
    public Page<UserYearScore> getUserYearScorePage(Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        return userYearScoreRepository.findAll(pageRequest);
    }

    // 根据id查单条数据
    public Optional<UserYearScore> getUserYearScoreById(Integer id) {
        return userYearScoreRepository.findById(id);
    }

    /**
     * 新增：根据指定年份统计每个部门的总分数
     * @param year 年份（格式：4位数字，如2024）
     * @return 各部门总分统计结果
     * @throws IllegalArgumentException 年份参数不合法时抛出
     */
    public List<DeptTotalScoreDTO> getDeptTotalScoreByYear(String year) {
        // 1. 参数校验：非空 + 4位数字格式
        if (!StringUtils.hasText(year)) {
            throw new IllegalArgumentException("年份不能为空");
        }
        if (!year.matches("^\\d{4}$")) {
            throw new IllegalArgumentException("年份格式错误，需为4位数字（如2024）");
        }

        // 2. 调用Repository查询并返回结果
        return userYearScoreRepository.findDeptTotalScoreByYear(year);
    }
}
