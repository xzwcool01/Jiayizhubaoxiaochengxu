package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.AiWearPrompt;
import com.jiayi.entity.PmsCategory;
import com.jiayi.mapper.PmsCategoryMapper;
import com.jiayi.service.AiWearPromptService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/ai-wear/prompt")
public class AdminAiWearPromptController {

    private final AiWearPromptService promptService;
    private final PmsCategoryMapper categoryMapper;

    public AdminAiWearPromptController(AiWearPromptService promptService, PmsCategoryMapper categoryMapper) {
        this.promptService = promptService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/list")
    public R<List<Map<String, Object>>> list() {
        List<PmsCategory> categories = categoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PmsCategory>()
                        .orderByAsc(PmsCategory::getSort));
        List<AiWearPrompt> prompts = promptService.getAll();
        Map<Long, AiWearPrompt> promptMap = new HashMap<>();
        for (AiWearPrompt p : prompts) promptMap.put(p.getCategoryId(), p);

        List<Map<String, Object>> result = new ArrayList<>();
        for (PmsCategory cat : categories) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", cat.getId());
            item.put("categoryName", cat.getName());
            AiWearPrompt p = promptMap.get(cat.getId());
            item.put("promptTemplate", p != null ? p.getPromptTemplate() : "");
            item.put("promptId", p != null ? p.getId() : null);
            result.add(item);
        }
        return R.ok(result);
    }

    @PostMapping("/save")
    public R<Void> save(@RequestBody Map<String, Object> body) {
        Long categoryId = Long.valueOf(body.get("categoryId").toString());
        String promptTemplate = (String) body.get("promptTemplate");
        promptService.saveOrUpdate(categoryId, promptTemplate);
        return R.ok(null);
    }
}
