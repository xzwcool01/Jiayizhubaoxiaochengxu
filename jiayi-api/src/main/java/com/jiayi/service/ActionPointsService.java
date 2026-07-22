package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiayi.dto.ActionPointsUpdateDTO;
import com.jiayi.entity.UmsActionPoints;
import com.jiayi.mapper.UmsActionPointsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionPointsService {

    private final UmsActionPointsMapper mapper;

    public ActionPointsService(UmsActionPointsMapper mapper) {
        this.mapper = mapper;
    }

    public int getPoints(String actionKey) {
        UmsActionPoints rule = mapper.selectOne(
                new LambdaQueryWrapper<UmsActionPoints>()
                        .eq(UmsActionPoints::getActionKey, actionKey)
                        .eq(UmsActionPoints::getStatus, 1));
        return rule != null ? rule.getPoints() : 0;
    }

    public void create(UmsActionPoints rule) {
        mapper.insert(rule);
    }

    public List<UmsActionPoints> listAll() {
        return mapper.selectList(null);
    }

    public void update(Long id, ActionPointsUpdateDTO dto) {
        UmsActionPoints rule = mapper.selectById(id);
        if (rule == null) return;
        if (dto.getPoints() != null) rule.setPoints(dto.getPoints());
        if (dto.getActionName() != null) rule.setActionName(dto.getActionName());
        if (dto.getStatus() != null) rule.setStatus(dto.getStatus());
        mapper.updateById(rule);
    }
}
