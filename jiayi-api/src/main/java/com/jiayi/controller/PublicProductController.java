package com.jiayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.entity.PmsCategory;
import com.jiayi.entity.PmsProduct;
import com.jiayi.mapper.PmsCategoryMapper;
import com.jiayi.service.PmsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class PublicProductController {

    private static final Logger log = LoggerFactory.getLogger(PublicProductController.class);

    private final PmsProductService productService;
    private final PmsCategoryMapper categoryMapper;

    public PublicProductController(PmsProductService productService, PmsCategoryMapper categoryMapper) {
        this.productService = productService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/list")
    public R<IPage<PmsProduct>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer productType,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<PmsProduct> q = new LambdaQueryWrapper<PmsProduct>()
                .eq(PmsProduct::getStatus, 1)
                .orderByDesc(PmsProduct::getWeight)
                .orderByDesc(PmsProduct::getSortOrder)
                .orderByDesc(PmsProduct::getId);
        if (categoryId != null) q.eq(PmsProduct::getCategoryId, categoryId);
        if (productType != null) q.eq(PmsProduct::getProductType, productType);
        if (keyword != null && !keyword.isEmpty()) {
            q.and(w -> w.like(PmsProduct::getName, keyword)
                    .or().like(PmsProduct::getSubtitle, keyword));
        }
        Page<PmsProduct> result = productService.page(new Page<>(page, size), q);
        for (PmsProduct p : result.getRecords()) {
            if (p.getCategoryId() != null) {
                PmsCategory c = categoryMapper.selectById(p.getCategoryId());
                if (c != null) p.setCategoryName(c.getName());
            }
        }
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<PmsProduct> get(@PathVariable Long id) {
        PmsProduct p = productService.getById(id);
        if (p == null) return R.error("商品不存在");
        if (p.getCategoryId() != null) {
            PmsCategory c = categoryMapper.selectById(p.getCategoryId());
            if (c != null) p.setCategoryName(c.getName());
        }
        return R.ok(p);
    }

    @GetMapping("/categories")
    public R<List<PmsCategory>> categories() {
        List<PmsCategory> list = categoryMapper.selectList(
                new LambdaQueryWrapper<PmsCategory>().orderByAsc(PmsCategory::getSort));
        return R.ok(list);
    }
}