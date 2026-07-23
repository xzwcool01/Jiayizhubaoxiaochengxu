package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiayi.entity.AiWearPrompt;
import com.jiayi.entity.PmsCategory;
import com.jiayi.mapper.AiWearPromptMapper;
import com.jiayi.mapper.PmsCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiWearPromptService {

    private final AiWearPromptMapper promptMapper;
    private final PmsCategoryMapper categoryMapper;

    public AiWearPromptService(AiWearPromptMapper promptMapper, PmsCategoryMapper categoryMapper) {
        this.promptMapper = promptMapper;
        this.categoryMapper = categoryMapper;
    }

    public List<AiWearPrompt> getAll() {
        return promptMapper.selectList(null);
    }

    public AiWearPrompt getByCategoryId(Long categoryId) {
        return promptMapper.selectOne(
                new LambdaQueryWrapper<AiWearPrompt>()
                        .eq(AiWearPrompt::getCategoryId, categoryId));
    }

    public void saveOrUpdate(Long categoryId, String promptTemplate) {
        AiWearPrompt existing = getByCategoryId(categoryId);
        if (existing == null) {
            AiWearPrompt p = new AiWearPrompt();
            p.setCategoryId(categoryId);
            p.setPromptTemplate(promptTemplate);
            promptMapper.insert(p);
        } else {
            existing.setPromptTemplate(promptTemplate);
            promptMapper.updateById(existing);
        }
    }

    public String buildPrompt(Long categoryId, String productName, String productDesc) {
        AiWearPrompt prompt = getByCategoryId(categoryId);
        if (prompt == null) {
            PmsCategory cat = categoryMapper.selectById(categoryId);
            return "一位女性佩戴" + productName + "，" + (productDesc != null ? productDesc : "") + "，自然光线，高清写实";
        }
        String template = prompt.getPromptTemplate();
        if (template == null) return productName;
        return template
                .replace("{name}", productName != null ? productName : "")
                .replace("{goodsName}", productName != null ? productName : "")
                .replace("{desc}", productDesc != null ? productDesc : "");
    }
}
