package szy.controller;

import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import szy.common.Result;
import szy.entity.Dept;
import szy.service.DeptService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/depts")
public class DeptController {
    private static final Logger log = LoggerFactory.getLogger(DeptController.class);
    private final DeptService deptService;

    @GetMapping
    public Result<PageInfo<Dept>> getDeptPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("接收到部门分页查询请求，页码：{}，每页条数：{}", pageNum, pageSize);
        try {
            PageInfo<Dept> deptPage = deptService.getDeptPage(pageNum, pageSize);
            log.info("部门分页查询完成，总条数：{}，总页数：{}",
                    deptPage.getTotal(), deptPage.getPages());
            return Result.success(deptPage);
        } catch (Exception e) {
            log.error("部门分页查询异常", e);
            return Result.error("查询失败");
        }
    }

    @GetMapping("/{id}")
    public Result<Dept> getDeptById(@PathVariable("id") Integer deptId) {
        log.info("查询部门：{}", deptId);
        try {
            Dept dept = deptService.getDeptById(deptId);
            if (dept != null) {
                return Result.success(dept);
            } else {
                return Result.error("部门不存在");
            }
        } catch (Exception e) {
            log.error("查询异常", e);
            return Result.error("查询失败");
        }
    }
}
