package com.jiayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.entity.SmsPointsProduct;
import com.jiayi.entity.SmsPointsRule;
import com.jiayi.mapper.SmsPointsProductMapper;
import com.jiayi.mapper.SmsPointsRuleMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/points")
public class AdminPointsController {

    private final SmsPointsRuleMapper ruleMapper;
    private final SmsPointsProductMapper pointsProductMapper;

    public AdminPointsController(SmsPointsRuleMapper ruleMapper, SmsPointsProductMapper pointsProductMapper) {
        this.ruleMapper = ruleMapper;
        this.pointsProductMapper = pointsProductMapper;
    }

    @GetMapping("/rules")
    public R<List<SmsPointsRule>> rules() {
        return R.ok(ruleMapper.selectList(new LambdaQueryWrapper<SmsPointsRule>().orderByDesc(SmsPointsRule::getCreateTime)));
    }

    @PostMapping("/rule")
    public R<Void> saveRule(@RequestBody SmsPointsRule rule) {
        if (rule.getId() != null) ruleMapper.updateById(rule);
        else ruleMapper.insert(rule);
        return R.ok(null);
    }

    @GetMapping("/products")
    public R<List<Long>> products(@RequestParam Long ruleId) {
        List<SmsPointsProduct> links = pointsProductMapper.selectList(
                new LambdaQueryWrapper<SmsPointsProduct>().eq(SmsPointsProduct::getRuleId, ruleId));
        return R.ok(links.stream().map(SmsPointsProduct::getProductId).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @PostMapping("/products")
    public R<Void> saveProducts(@RequestBody Map<String, Object> body) {
        Long ruleId = Long.valueOf(body.get("ruleId").toString());
        pointsProductMapper.delete(new LambdaQueryWrapper<SmsPointsProduct>().eq(SmsPointsProduct::getRuleId, ruleId));
        @SuppressWarnings("unchecked")
        List<String> pids = (List<String>) body.get("productIds");
        if (pids != null) {
            for (String pid : pids) {
                SmsPointsProduct p = new SmsPointsProduct();
                p.setRuleId(ruleId);
                p.setProductId(Long.valueOf(pid));
                pointsProductMapper.insert(p);
            }
        }
        return R.ok(null);
    }
}