package szy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 部门年度总分统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptTotalScoreDTO {
    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 年度总分数
     */
    private BigDecimal totalScore;
}
