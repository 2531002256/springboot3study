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
    private Integer userId; // 用户ID（主键）

    @Column(name = "account")
    private String account; // 账号

    @Column(name = "name")
    private String name; // 用户名

    @Column(name = "dept_id")
    private Integer deptId; // 用户名

}
