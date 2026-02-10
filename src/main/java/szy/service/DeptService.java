package szy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import szy.entity.Dept;
import szy.repository.DeptRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeptService {
    private static final Logger log = LoggerFactory.getLogger(DeptService.class);

    private final DeptRepository deptRepository;

    /**
     * 分页查询部门列表
     */
    public Page<Dept> getDeptPage(Integer pageNum, Integer pageSize) {
        log.info("开始处理部门分页查询，页码：{}，每页条数：{}", pageNum, pageSize);
        try {
            log.debug("封装分页条件：PageRequest.of({}, {})", pageNum, pageSize);
            PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
            Page<Dept> deptPage = deptRepository.findAll(pageRequest);

            log.info("部门分页查询完成，总部门数：{}，总页数：{}",
                    deptPage.getTotalElements(), deptPage.getTotalPages());
            return deptPage;
        } catch (Exception e) {
            log.error("部门分页查询失败，页码：{}，每页条数：{}", pageNum, pageSize, e);
            throw e;
        }
    }

    /**
     * 根据ID查询单个部门
     */
    public Optional<Dept> getDeptById(Integer deptId) {
        log.info("开始处理单个部门查询，部门ID：{}", deptId);
        try {
            Optional<Dept> deptOptional = deptRepository.findById(deptId);

            if (deptOptional.isPresent()) {
                Dept dept = deptOptional.get();
                // 仅保留存在的 deptName 字段，删除 deptCode 相关内容
                log.debug("部门ID：{} 查询成功，部门名称：{}", deptId, dept.getDeptName());
            } else {
                log.warn("部门ID：{} 查询失败，原因：该部门ID不存在", deptId);
            }
            return deptOptional;
        } catch (Exception e) {
            log.error("单个部门查询失败，部门ID：{}", deptId, e);
            throw e;
        }
    }
}
