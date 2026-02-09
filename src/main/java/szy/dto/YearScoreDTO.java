package szy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 单年份分数统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearScoreDTO {
    /**
     * 年份（4位数字，如2024）
     */
    private String year;

    /**
     * 该年份总分（无数据则为0）
     */
    private BigDecimal totalScore;
}
