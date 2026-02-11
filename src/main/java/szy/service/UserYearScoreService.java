package szy.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import szy.dto.DeptTotalScoreDTO;
import szy.dto.ImportResultDTO;
import szy.dto.UserYearScoreExcelDTO;
import szy.dto.YearScoreDTO;
import szy.entity.User;
import szy.entity.UserYearScore;
import szy.mapper.UserMapper;
import szy.mapper.UserYearScoreMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户年度分数业务层
 * 改造说明：
 * 1. 移除JPA Repository依赖，替换为MyBatis Mapper
 * 2. 分页逻辑从JPA PageRequest改为PageHelper
 * 3. 移除Optional封装，改用null判断
 * 4. 保留所有原有业务逻辑、日志和异常处理
 */
@Service
@RequiredArgsConstructor
public class UserYearScoreService {
    // 日志实例
    private static final Logger log = LoggerFactory.getLogger(UserYearScoreService.class);

    // MyBatis Mapper注入（替换原JPA Repository）
    private final UserYearScoreMapper userYearScoreMapper;
    private final UserMapper userMapper;

    /**
     * 分页查询用户年度分数
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    public PageInfo<UserYearScore> getUserYearScorePage(Integer pageNum, Integer pageSize) {
        log.info("开始处理用户年度分数分页查询，页码：{}，每页条数：{}", pageNum, pageSize);
        try {
            // MyBatis分页核心：PageHelper.startPage
            PageHelper.startPage(pageNum, pageSize);
            // 查询数据（PageHelper自动分页）
            List<UserYearScore> scoreList = userYearScoreMapper.selectAll();
            // 封装分页结果
            PageInfo<UserYearScore> scorePage = new PageInfo<>(scoreList);

            log.info("用户年度分数分页查询完成，总条数：{}，总页数：{}",
                    scorePage.getTotal(), scorePage.getPages());
            return scorePage;
        } catch (Exception e) {
            log.error("用户年度分数分页查询失败，页码：{}，每页条数：{}", pageNum, pageSize, e);
            throw e; // 抛出异常，由Controller统一处理响应
        }
    }

    /**
     * 根据ID查询单个用户年度分数
     * @param id 分数ID
     * @return 分数信息（null表示不存在）
     */
    public UserYearScore getUserYearScoreById(Integer id) {
        log.info("开始处理单个用户年度分数查询，分数ID：{}", id);
        try {
            // 调用Mapper查询，无数据返回null
            UserYearScore score = userYearScoreMapper.selectById(id);

            if (score != null) {
                log.debug("分数ID：{} 查询成功，用户ID：{}，年份：{}，分数：{}",
                        id, score.getUserId(), score.getYear(), score.getScore());
            } else {
                log.warn("分数ID：{} 查询失败，原因：该分数ID不存在", id);
            }
            return score;
        } catch (Exception e) {
            log.error("单个用户年度分数查询失败，分数ID：{}", id, e);
            throw e;
        }
    }

    /**
     * 按年份统计各部门总分
     * @param year 统计年份（4位数字）
     * @return 各部门总分列表
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    public List<DeptTotalScoreDTO> getDeptTotalScoreByYear(String year) {
        log.info("开始处理按年份统计部门总分请求，统计年份：{}", year);

        // 1. 严格参数校验
        if (!StringUtils.hasText(year)) {
            log.warn("按年份统计部门总分失败：年份参数为空");
            throw new IllegalArgumentException("年份不能为空");
        }
        if (!year.matches("^\\d{4}$")) {
            log.warn("按年份统计部门总分失败：年份格式错误，当前值：{}（需为4位数字）", year);
            throw new IllegalArgumentException("年份格式错误，需为4位数字（如2024）");
        }

        try {
            // 调用Mapper查询统计结果
            List<DeptTotalScoreDTO> result = userYearScoreMapper.selectDeptTotalScoreByYear(year);
            log.info("按年份{}统计部门总分完成，共统计到{}个部门的总分数据", year, result.size());
            return result;
        } catch (Exception e) {
            log.error("按年份{}统计部门总分失败", year, e);
            throw e;
        }
    }

    /**
     * 根据部门ID、年份范围统计历年总分（自动补全无数据年份为0）
     * @param deptId 部门ID（正整数）
     * @param startYear 开始年份（4位数字）
     * @param endYear 结束年份（4位数字）
     * @return 连续年份的分数列表
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    public List<YearScoreDTO> getDeptYearlyScoreByRange(Integer deptId, String startYear, String endYear) {
        log.info("开始处理部门历年总分统计请求，部门ID：{}，统计年份范围：{} - {}", deptId, startYear, endYear);

        try {
            // 1. 核心参数校验
            if (deptId == null || deptId <= 0) {
                log.warn("部门历年总分统计失败：部门ID不合法，当前值：{}（需为正整数）", deptId);
                throw new IllegalArgumentException("部门ID不能为空且必须为正整数");
            }
            if (!StringUtils.hasText(startYear) || !startYear.matches("^\\d{4}$")) {
                log.warn("部门历年总分统计失败：开始年份不合法，当前值：{}（需为4位数字）", startYear);
                throw new IllegalArgumentException("开始年份不能为空且需为4位数字（如2021）");
            }
            if (!StringUtils.hasText(endYear) || !endYear.matches("^\\d{4}$")) {
                log.warn("部门历年总分统计失败：结束年份不合法，当前值：{}（需为4位数字）", endYear);
                throw new IllegalArgumentException("结束年份不能为空且需为4位数字（如2025）");
            }

            // 2. 校验年份范围
            int start = Integer.parseInt(startYear);
            int end = Integer.parseInt(endYear);
            if (start > end) {
                log.warn("部门历年总分统计失败：开始年份大于结束年份，开始年份：{}，结束年份：{}", startYear, endYear);
                throw new IllegalArgumentException("开始年份不能大于结束年份");
            }

            // 3. 生成连续年份列表
            List<String> allYears = new ArrayList<>();
            for (int year = start; year <= end; year++) {
                allYears.add(String.valueOf(year));
            }
            log.debug("部门ID{}：生成连续年份列表完成，年份范围{} - {}，共{}个年份",
                    deptId, startYear, endYear, allYears.size());

            // 4. 查询数据库中有数据的年份总分
            List<YearScoreDTO> dbScores = userYearScoreMapper.selectDeptYearlyScore(deptId, startYear, endYear);
            log.debug("部门ID{}：查询数据库获取有数据的年份总分，共{}条记录", deptId, dbScores.size());

            // 5. 转换为Map，方便快速匹配
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
            log.info("部门ID{}：历年总分统计完成，补全后共{}个年份数据（{} - {}）",
                    deptId, result.size(), startYear, endYear);

            return result;
        } catch (IllegalArgumentException e) {
            throw e; // 业务异常直接抛出
        } catch (Exception e) {
            log.error("部门ID{}历年总分统计异常，年份范围：{} - {}", deptId, startYear, endYear, e);
            throw e; // 系统异常抛出
        }
    }

    /**
     * Excel导入用户年度分数（核心业务）
     * @param file 上传的Excel文件（.xlsx/.xls）
     * @return 导入结果（成功/失败条数、失败原因）
     */
    public ImportResultDTO importUserYearScore(MultipartFile file) {
        // 基础信息日志
        String fileName = file != null ? file.getOriginalFilename() : "空文件";
        long fileSizeKb = file != null ? file.getSize() / 1024 : 0;
        log.info("开始处理Excel导入用户年度分数请求，文件名：{}，文件大小：{}KB", fileName, fileSizeKb);

        // 1. 文件合法性校验
        if (file == null || file.isEmpty()) {
            log.warn("Excel导入用户年度分数失败：上传的文件为空");
            throw new IllegalArgumentException("上传的Excel文件不能为空");
        }
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            log.warn("Excel导入用户年度分数失败：文件格式不支持，文件名：{}（仅支持.xlsx/.xls）", fileName);
            throw new IllegalArgumentException("仅支持.xlsx/.xls格式的Excel文件");
        }

        // 2. 定义导入结果变量（原子类保证线程安全）
        AtomicInteger successCount = new AtomicInteger(0); // 成功条数
        List<String> failReasons = new ArrayList<>();       // 失败原因
        AtomicInteger rowNum = new AtomicInteger(1);        // Excel行号（表头行从1开始）

        try {
            // 3. EasyExcel分页读取Excel（避免大文件内存溢出）
            log.debug("开始解析Excel文件：{}，使用PageReadListener分页读取", fileName);
            EasyExcel.read(file.getInputStream(), UserYearScoreExcelDTO.class,
                    new PageReadListener<UserYearScoreExcelDTO>(dataList -> {
                        log.debug("解析Excel文件{}：读取到一页数据，共{}条记录", fileName, dataList.size());
                        for (UserYearScoreExcelDTO dto : dataList) {
                            rowNum.incrementAndGet(); // 行号+1（跳过表头）
                            try {
                                // 4. 单条数据校验
                                validateExcelData(dto, rowNum.get(), failReasons);

                                // 5. 根据账号查询用户（关联userId）
                                log.debug("解析Excel第{}行：开始匹配用户账号{}", rowNum.get(), dto.getAccount());
                                User user = userMapper.selectByAccount(dto.getAccount());
                                if (user == null) {
                                    log.warn("解析Excel第{}行失败：用户账号[{}]不存在", rowNum.get(), dto.getAccount());
                                    failReasons.add("第" + rowNum.get() + "行：用户账号[" + dto.getAccount() + "]不存在");
                                    continue;
                                }
                                Integer userId = user.getUserId();

                                // 6. 校验重复数据（同一用户+年度）
                                log.debug("解析Excel第{}行：校验用户ID{} {}年度数据是否重复", rowNum.get(), userId, dto.getYear());
                                UserYearScore existScore = userYearScoreMapper.selectByUserIdAndYear(userId, dto.getYear());
                                if (existScore != null) {
                                    log.warn("解析Excel第{}行失败：用户账号[{}]{}年度数据已存在", rowNum.get(), dto.getAccount(), dto.getYear());
                                    failReasons.add("第" + rowNum.get() + "行：用户账号[" + dto.getAccount() + "]" + dto.getYear() + "年度数据已存在，无法重复导入");
                                    continue;
                                }

                                // 7. 封装数据并保存
                                UserYearScore score = new UserYearScore();
                                score.setUserId(userId);
                                score.setYear(dto.getYear());
                                score.setScore(dto.getScore());
                                userYearScoreMapper.insert(score); // MyBatis新增数据

                                log.debug("解析Excel第{}行成功：用户账号[{}]{}年度分数{}已保存",
                                        rowNum.get(), dto.getAccount(), dto.getYear(), dto.getScore());
                                successCount.incrementAndGet(); // 成功条数+1
                            } catch (Exception e) {
                                log.error("解析Excel第{}行异常：{}", rowNum.get(), e.getMessage(), e);
                                failReasons.add("第" + rowNum.get() + "行：导入失败，原因：" + e.getMessage());
                            }
                        }
                    })).sheet().doRead(); // 读取第一个sheet

            // 8. 封装导入结果
            log.info("Excel导入用户年度分数处理完成，文件名：{}，成功条数：{}，失败条数：{}",
                    fileName, successCount.get(), failReasons.size());
            return new ImportResultDTO(
                    successCount.get(),
                    failReasons.size(),
                    failReasons
            );
        } catch (IOException e) {
            log.error("解析Excel文件失败：文件名{}，IO异常原因：{}", fileName, e.getMessage(), e);
            throw new RuntimeException("解析Excel文件失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Excel导入用户年度分数全局异常：文件名{}", fileName, e);
            throw e;
        }
    }

    /**
     * 校验Excel单条数据合法性
     * @param dto Excel解析的DTO
     * @param row Excel行号
     * @param failReasons 失败原因列表（直接添加）
     */
    private void validateExcelData(UserYearScoreExcelDTO dto, int row, List<String> failReasons) {
        log.debug("开始校验Excel第{}行数据：账号={}，年份={}，分数={}", row, dto.getAccount(), dto.getYear(), dto.getScore());

        // ① 账号非空校验
        if (!StringUtils.hasText(dto.getAccount())) {
            log.warn("Excel第{}行校验失败：用户账号为空", row);
            failReasons.add("第" + row + "行：用户账号不能为空");
        }

        // ② 年度校验（非空+4位数字）
        if (!StringUtils.hasText(dto.getYear())) {
            log.warn("Excel第{}行校验失败：年度为空", row);
            failReasons.add("第" + row + "行：年度不能为空");
        } else if (!dto.getYear().matches("^\\d{4}$")) {
            log.warn("Excel第{}行校验失败：年度格式错误，当前值：{}", row, dto.getYear());
            failReasons.add("第" + row + "行：年度格式错误，需为4位数字（如2024）");
        }

        // ③ 分数校验（非空+非负）
        if (dto.getScore() == null) {
            log.warn("Excel第{}行校验失败：分数为空", row);
            failReasons.add("第" + row + "行：分数不能为空");
        } else if (dto.getScore().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Excel第{}行校验失败：分数为负数，当前值：{}", row, dto.getScore());
            failReasons.add("第" + row + "行：分数不能为负数，当前值：" + dto.getScore());
        }
    }
}
