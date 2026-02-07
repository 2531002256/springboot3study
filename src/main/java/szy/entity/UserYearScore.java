package szy.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "user_year_score")
public class UserYearScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 主键

    @Column(name = "year", length = 4)
    private String year; // 年度（char(4)）

    @Column(name = "user_id")
    private Integer userId; //

    @Column(name = "score", precision = 8, scale = 2)
    private BigDecimal score; // 成绩（numeric(8,2)）

}
