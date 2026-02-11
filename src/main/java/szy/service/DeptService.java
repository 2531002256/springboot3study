package szy.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import szy.entity.Dept;
import szy.mapper.DeptMapper;

@Service
@RequiredArgsConstructor
public class DeptService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DeptMapper deptMapper;

    public PageInfo<Dept> getDeptPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(deptMapper.selectAll());
    }

    public Dept getDeptById(Integer deptId) {
        return deptMapper.selectById(deptId);
    }
}
