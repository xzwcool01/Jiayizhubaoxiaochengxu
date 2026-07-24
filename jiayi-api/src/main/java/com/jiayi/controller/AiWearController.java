package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.AiWearRecord;
import com.jiayi.entity.AiWearShowcase;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.UmsUserMapper;
import com.jiayi.service.AiWearService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-wear")
public class AiWearController {

    private static final Logger log = LoggerFactory.getLogger(AiWearController.class);

    private final AiWearService aiWearService;
    private final UmsUserMapper userMapper;

    public AiWearController(AiWearService aiWearService, UmsUserMapper userMapper) {
        this.aiWearService = aiWearService;
        this.userMapper = userMapper;
    }

    @GetMapping("/quota")
    public R<Map<String, Object>> quota(@RequestParam String openid) {
        UmsUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsUser>()
                        .eq(UmsUser::getOpenid, openid));
        if (user == null) return R.error("用户不存在");
        int remaining = aiWearService.getRemainingQuota(user.getId());
        int total = aiWearService.getDailyLimit(user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("remaining", remaining);
        data.put("total", total);
        return R.ok(data);
    }

    @PostMapping("/generate")
    public R<Map<String, Object>> generate(
            @RequestParam String openid,
            @RequestParam Long productId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String style,
            @RequestParam("file") MultipartFile file) {
        UmsUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsUser>()
                        .eq(UmsUser::getOpenid, openid));
        if (user == null) return R.error("用户不存在");

        String photoUrl = aiWearService.savePhoto(file, user.getId());
        if (photoUrl == null) return R.error("照片保存失败");

        try {
            AiWearRecord record = aiWearService.generate(user.getId(), productId, categoryId,
                    photoUrl, style);
            int remaining = aiWearService.getRemainingQuota(user.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("recordId", record.getId());
            data.put("resultUrl", record.getResultUrl());
            data.put("remaining", remaining);
            return R.ok(data);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/publish")
    public R<Map<String, Object>> publish(@RequestBody Map<String, Object> body) {
        String openid = (String) body.get("openid");
        Long recordId = body.get("recordId") instanceof Number
                ? ((Number) body.get("recordId")).longValue() : null;
        if (openid == null || recordId == null) return R.error("参数缺失");
        UmsUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsUser>()
                        .eq(UmsUser::getOpenid, openid));
        if (user == null) return R.error("用户不存在");
        try {
            String newUrl = aiWearService.publishRecord(recordId);
            Map<String, Object> data = new HashMap<>();
            data.put("resultUrl", newUrl);
            return R.ok(data);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public R<List<AiWearRecord>> list(@RequestParam String openid,
                                      @RequestParam(required = false) Long productId) {
        UmsUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsUser>()
                        .eq(UmsUser::getOpenid, openid));
        if (user == null) return R.error("用户不存在");
        return R.ok(aiWearService.getRecords(user.getId(), productId));
    }

    @GetMapping("/showcase")
    public R<List<AiWearShowcase>> showcase() {
        return R.ok(aiWearService.getShowcase());
    }
}
