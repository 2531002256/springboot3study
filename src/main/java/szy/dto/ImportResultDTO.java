package szy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Excel导入结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDTO {
    /**
     * 成功导入条数
     */
    private Integer successCount;

    /**
     * 失败条数
     */
    private Integer failCount;

    /**
     * 失败原因（行号+原因）
     */
    private List<String> failReasons;
}
