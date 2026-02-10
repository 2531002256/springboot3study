package szy.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;  // 新增：导入SLF4J Logger
import org.slf4j.LoggerFactory;  // 新增：导入LoggerFactory
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import szy.common.Result;
import szy.entity.Dept;
import szy.service.DeptService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/depts") // 统一接口前缀
public class DeptController {
    // 新增：定义Logger实例（参数为当前类Class，方便定位日志来源）
    private static final Logger log = LoggerFactory.getLogger(DeptController.class);

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
        // 新增：记录请求入参（INFO级别，生产环境也需保留）
        log.info("接收到部门分页查询请求，页码：{}，每页条数：{}", pageNum, pageSize);
        try {
            Page<Dept> deptPage = deptService.getDeptPage(pageNum, pageSize);
            // 新增：记录响应结果（包含总条数、总页数，便于排查“分页数据异常”问题）
            log.info("部门分页查询完成，总条数：{}，总页数：{}，当前页条数：{}",
                    deptPage.getTotalElements(), deptPage.getTotalPages(), deptPage.getNumberOfElements());
            return Result.success(deptPage);
        } catch (Exception e) {
            // 新增：记录异常（ERROR级别，必须传e保留堆栈）
            log.error("部门分页查询接口异常，页码：{}，每页条数：{}", pageNum, pageSize, e);
            return Result.error("部门分页查询失败：" + e.getMessage());
        }
    }

    /**
     * 接口2：根据ID查询单个部门
     * 请求示例：GET /depts/1
     */
    @GetMapping("/{id}")
    public Result<Dept> getDeptById(@PathVariable("id") Integer deptId) {
        // 新增：记录请求入参
        log.info("接收到单个部门查询请求，部门ID：{}", deptId);
        try {
            Result<Dept> result = deptService.getDeptById(deptId)
                    .map(dept -> {
                        // 新增：记录查询成功（DEBUG级别，开发环境看详情，生产可保留）
                        log.debug("部门ID：{} 查询成功，部门名称：{}", deptId, dept.getDeptName());
                        return Result.success(dept);
                    })
                    .orElseGet(() -> {
                        // 新增：记录查询失败（WARN级别，非致命错误，需关注）
                        log.warn("部门ID：{} 查询失败，原因：该部门ID不存在", deptId);
                        return Result.error("部门ID不存在：" + deptId);
                    });
            return result;
        } catch (Exception e) {
            // 新增：记录异常
            log.error("单个部门查询接口异常，部门ID：{}", deptId, e);
            return Result.error("部门查询失败：" + e.getMessage());
        }
    }
}
