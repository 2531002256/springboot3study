package szy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dept")
public class Dept {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Integer deptId; // 部门ID（主键）

    @Column(name = "dept_name")
    private String deptName; // 部门名称

}
