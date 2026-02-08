package szy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import szy.common.Result;
import szy.entity.Dept;
import szy.service.DeptService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/depts") // 统一接口前缀
public class DeptController {
    private final DeptService deptService;

    /**
     * 接口1：分页查询部门列表
     * 请求示例：GET /depts?pageNum=0&pageSize=10
     */
    @GetMapping
    public Result<Page<Dept>> getDeptPage(
            @RequestParam(defaultValue = "0") Integer pageNum,  // 默认第1页（JPA页码从0开始）
            @RequestParam(defaultValue = "10") Integer pageSize // 默认每页10条
    ) {
        Page<Dept> deptPage = deptService.getDeptPage(pageNum, pageSize);
        return Result.success(deptPage);
    }

    /**
     * 接口2：根据ID查询单个部门
     * 请求示例：GET /depts/1
     */
    @GetMapping("/{id}")
    public Result<Dept> getDeptById(@PathVariable("id") Integer deptId) {
        return deptService.getDeptById(deptId)
                .map(Result::success)
                .orElse(Result.error("部门ID不存在：" + deptId));
    }
}
