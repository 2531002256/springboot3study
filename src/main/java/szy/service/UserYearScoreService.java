package szy.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import szy.dto.DeptTotalScoreDTO;
import szy.dto.ImportResultDTO;
import szy.dto.UserYearScoreExcelDTO;
import szy.dto.YearScoreDTO;
import szy.entity.User;
import szy.entity.UserYearScore;
import szy.repository.UserRepository;
import szy.repository.UserYearScoreRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class UserYearScoreService {
    private final UserYearScoreRepository userYearScoreRepository;
    private final UserRepository userRepository;

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

    /**
     * Excel导入用户年度分数（核心方法）
     * @param file 上传的Excel文件
     * @return 导入结果（成功/失败条数、失败原因）
     */
    public ImportResultDTO importUserYearScore(MultipartFile file) {
        // 1. 校验文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的Excel文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new IllegalArgumentException("仅支持.xlsx/.xls格式的Excel文件");
        }

        // 2. 定义变量存储导入结果
        AtomicInteger successCount = new AtomicInteger(0); // 成功条数（原子类保证线程安全）
        List<String> failReasons = new ArrayList<>(); // 失败原因（行号+原因）
        AtomicInteger rowNum = new AtomicInteger(1); // Excel行号（从1开始，表头行）

        try {
            // 3. EasyExcel解析Excel（PageReadListener：分页读取，避免内存溢出）
            EasyExcel.read(file.getInputStream(), UserYearScoreExcelDTO.class, new PageReadListener<UserYearScoreExcelDTO>(dataList -> {
                for (UserYearScoreExcelDTO dto : dataList) {
                    rowNum.incrementAndGet(); // 行号+1（跳过表头，从数据行开始）
                    try {
                        // 4. 单条数据校验
                        validateExcelData(dto, rowNum.get(), failReasons);
                        // 5. 关联用户：根据账号查userId
                        Optional<User> userOptional = userRepository.findByAccount(dto.getAccount());
                        if (userOptional.isEmpty()) {
                            failReasons.add("第" + rowNum.get() + "行：用户账号[" + dto.getAccount() + "]不存在");
                            continue;
                        }
                        Integer userId = userOptional.get().getUserId();

                        // 6. 校验重复：同一用户+年度不能重复
                        Optional<UserYearScore> existScore = userYearScoreRepository.findByUserIdAndYear(userId, dto.getYear());
                        if (existScore.isPresent()) {
                            failReasons.add("第" + rowNum.get() + "行：用户账号[" + dto.getAccount() + "]" + dto.getYear() + "年度数据已存在，无法重复导入");
                            continue;
                        }

                        // 7. 封装数据并保存
                        UserYearScore score = new UserYearScore();
                        score.setUserId(userId);
                        score.setYear(dto.getYear());
                        score.setScore(dto.getScore());
                        userYearScoreRepository.save(score);

                        successCount.incrementAndGet(); // 成功条数+1
                    } catch (Exception e) {
                        failReasons.add("第" + rowNum.get() + "行：导入失败，原因：" + e.getMessage());
                    }
                }
            })).sheet().doRead(); // 读取第一个sheet

            // 8. 封装导入结果
            return new ImportResultDTO(
                    successCount.get(),
                    failReasons.size(),
                    failReasons
            );
        } catch (IOException e) {
            throw new RuntimeException("解析Excel文件失败：" + e.getMessage());
        }
    }

    /**
     * 校验Excel单条数据合法性
     * @param dto Excel解析的DTO
     * @param row 行号
     * @param failReasons 失败原因列表
     */
    private void validateExcelData(UserYearScoreExcelDTO dto, int row, List<String> failReasons) {
        // ① 账号非空
        if (!StringUtils.hasText(dto.getAccount())) {
            failReasons.add("第" + row + "行：用户账号不能为空");
        }
        // ② 年度非空+4位数字
        if (!StringUtils.hasText(dto.getYear())) {
            failReasons.add("第" + row + "行：年度不能为空");
        } else if (!dto.getYear().matches("^\\d{4}$")) {
            failReasons.add("第" + row + "行：年度格式错误，需为4位数字（如2024）");
        }
        // ③ 分数非空+非负
        if (dto.getScore() == null) {
            failReasons.add("第" + row + "行：分数不能为空");
        } else if (dto.getScore().compareTo(BigDecimal.ZERO) < 0) {
            failReasons.add("第" + row + "行：分数不能为负数，当前值：" + dto.getScore());
        }
    }

}
