package szy.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Excel导入的用户年度分数DTO（和Excel列映射）
 */
@Data
public class UserYearScoreExcelDTO {
    /**
     * 用户账号（Excel列名：用户账号）
     */
    @ExcelProperty(value = "用户账号", index = 0) // index=0对应Excel第一列
    private String account;

    /**
     * 年度（Excel列名：年度）
     */
    @ExcelProperty(value = "年度", index = 1) // index=1对应Excel第二列
    private String year;

    /**
     * 分数（Excel列名：分数）
     */
    @ExcelProperty(value = "分数", index = 2) // index=2对应Excel第三列
    private BigDecimal score;
}
