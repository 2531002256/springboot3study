package szy.entity;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserYearScore {
    private Integer id;
    private String year;
    private Integer userId;
    private BigDecimal score;
}
