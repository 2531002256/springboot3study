package szy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import szy.entity.UserYearScore;
import szy.repository.UserYearScoreRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserYearScoreService {
    private final UserYearScoreRepository userYearScoreRepository;

    /**
     * 分页查询用户年度分数列表
     */
    public Page<UserYearScore> getUserYearScorePage(Integer pageNum, Integer pageSize) {
        // 封装分页条件
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        return userYearScoreRepository.findAll(pageRequest);
    }

    /**
     * 根据ID查询单个用户年度分数
     */
    public Optional<UserYearScore> getUserYearScoreById(Integer id) {

        return userYearScoreRepository.findById(id);
    }
}
