package szy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    // 新增unique = true，标注账号唯一
    @Column(name = "account", unique = true, nullable = false)
    private String account;

    @Column(name = "name")
    private String name;

    @Column(name = "dept_id")
    private Integer deptId;
}
