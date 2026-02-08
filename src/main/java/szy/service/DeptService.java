package szy.service;

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
    private final DeptRepository deptRepository;

    /**
     * 分页查询部门列表
     */
    public Page<Dept> getDeptPage(Integer pageNum, Integer pageSize) {
        // 封装分页条件
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        return deptRepository.findAll(pageRequest);
    }

    /**
     * 根据ID查询单个部门
     */
    public Optional<Dept> getDeptById(Integer deptId) {
        return deptRepository.findById(deptId);
    }
}
