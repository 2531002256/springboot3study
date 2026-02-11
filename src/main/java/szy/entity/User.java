package szy.entity;
import lombok.Data;

@Data
public class User {
    private Integer userId;
    private String account;
    private String name;
    private Integer deptId;
}
