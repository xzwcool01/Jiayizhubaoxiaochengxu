package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiayi.dto.PointsRuleVO;
import com.jiayi.entity.*;
import com.jiayi.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PointsService {

    private final SmsPointsRuleMapper ruleMapper;
    private final SmsPointsProductMapper pointsProductMapper;
    private final UmsUserMapper userMapper;

    public PointsService(SmsPointsRuleMapper ruleMapper, SmsPointsProductMapper pointsProductMapper,
                         UmsUserMapper userMapper) {
        this.ruleMapper = ruleMapper;
        this.pointsProductMapper = pointsProductMapper;
        this.userMapper = userMapper;
    }

    public List<PointsRuleVO> getRulesForProducts(List<Long> productIds) {
        List<SmsPointsRule> allRules = ruleMapper.selectList(new LambdaQueryWrapper<SmsPointsRule>()
                .eq(SmsPointsRule::getStatus, 1));
        if (allRules.isEmpty()) return List.of();
        if (productIds == null || productIds.isEmpty()) {
            PointsRuleVO vo = toVO(allRules.get(0), true);
            return List.of(vo);
        }
        List<PointsRuleVO> result = new ArrayList<>();
        for (SmsPointsRule rule : allRules) {
            List<SmsPointsProduct> links = pointsProductMapper.selectList(
                    new LambdaQueryWrapper<SmsPointsProduct>().eq(SmsPointsProduct::getRuleId, rule.getId()));
            boolean available;
            if (links.isEmpty()) {
                available = true;
            } else {
                Set<Long> allowed = links.stream().map(SmsPointsProduct::getProductId).filter(Objects::nonNull).collect(Collectors.toSet());
                if (allowed.isEmpty()) { available = true; }
                else {
                    available = false;
                    for (Long pid : productIds) {
                        if (allowed.contains(pid)) { available = true; break; }
                    }
                }
            }
            if (available) result.add(toVO(rule, true));
        }
        return result;
    }

    private PointsRuleVO toVO(SmsPointsRule rule, boolean available) {
        PointsRuleVO vo = new PointsRuleVO();
        vo.setId(rule.getId());
        vo.setPoints(rule.getPoints());
        vo.setAmount(rule.getAmount());
        vo.setType(rule.getType());
        vo.setProductsAvailable(available);
        return vo;
    }

    public Integer getUserPoints(Long userId) {
        UmsUser user = userMapper.selectById(userId);
        return user != null ? user.getPoints() : 0;
    }

    @Transactional
    public void deductPoints(Long userId, int points) {
        UmsUser user = userMapper.selectById(userId);
        if (user != null && user.getPoints() >= points) {
            user.setPoints(user.getPoints() - points);
            userMapper.updateById(user);
        }
    }
}