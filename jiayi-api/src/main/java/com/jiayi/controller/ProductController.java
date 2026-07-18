package com.jiayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.common.R;
import com.jiayi.dto.ProductDTO;
import com.jiayi.dto.ProductQueryDTO;
import com.jiayi.entity.PmsCategory;
import com.jiayi.entity.PmsProduct;
import com.jiayi.mapper.PmsCategoryMapper;
import com.jiayi.service.PmsProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product")
public class ProductController {

    private final PmsProductService productService;
    private final PmsCategoryMapper categoryMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductController(PmsProductService productService, PmsCategoryMapper categoryMapper) {
        this.productService = productService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/list")
    public R<IPage<PmsProduct>> list(ProductQueryDTO dto) {
        LambdaQueryWrapper<PmsProduct> q = new LambdaQueryWrapper<>();
        q.orderByDesc(PmsProduct::getSortOrder).orderByDesc(PmsProduct::getId);
        if (dto.getProductType() != null) q.eq(PmsProduct::getProductType, dto.getProductType());
        if (dto.getCategoryId() != null) q.eq(PmsProduct::getCategoryId, dto.getCategoryId());
        if (dto.getStatus() != null) q.eq(PmsProduct::getStatus, dto.getStatus());
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            q.and(w -> w.like(PmsProduct::getName, dto.getKeyword())
                    .or().like(PmsProduct::getSubtitle, dto.getKeyword()));
        }
        Page<PmsProduct> page = productService.page(new Page<>(dto.getPage(), dto.getSize()), q);
        return R.ok(page);
    }

    @GetMapping("/{id}")
    public R<PmsProduct> get(@PathVariable Long id) {
        PmsProduct p = productService.getById(id);
        if (p == null) return R.error("商品不存在");
        return R.ok(p);
    }

    @PostMapping
    public R<PmsProduct> create(@RequestBody ProductDTO dto) {
        PmsProduct p = toEntity(dto);
        productService.save(p);
        productService.moveTempImages(p);
        productService.updateById(p);
        return R.ok(p);
    }

    @PutMapping("/{id}")
    public R<PmsProduct> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        PmsProduct p = productService.getById(id);
        if (p == null) return R.error("商品不存在");
        merge(p, dto);
        productService.moveTempImages(p);
        productService.updateById(p);
        return R.ok(p);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (!productService.removeById(id)) return R.error("删除失败");
        return R.ok(null);
    }

    @GetMapping("/categories")
    public R<List<PmsCategory>> categories() {
        return R.ok(categoryMapper.selectList(
                new LambdaQueryWrapper<PmsCategory>().orderByAsc(PmsCategory::getSort)));
    }

    private PmsProduct toEntity(ProductDTO dto) {
        PmsProduct p = new PmsProduct();
        merge(p, dto);
        return p;
    }

    private void merge(PmsProduct p, ProductDTO dto) {
        p.setCategoryId(dto.getCategoryId());
        p.setProductType(dto.getProductType());
        p.setName(dto.getName());
        p.setSubtitle(dto.getSubtitle());
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            p.setMainImage(dto.getImages().get(0));
            try { p.setImages(objectMapper.writeValueAsString(dto.getImages())); } catch (JsonProcessingException ignored) {}
        } else if (dto.getMainImage() != null) {
            p.setMainImage(dto.getMainImage());
        }
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setOriginalPrice(dto.getOriginalPrice());
        p.setPointsPrice(dto.getPointsPrice());
        p.setStock(dto.getStock());
        p.setFlashStock(dto.getFlashStock());
        p.setSaleStart(dto.getSaleStart());
        p.setSaleEnd(dto.getSaleEnd());
        p.setMemberLevel(dto.getMemberLevel());
        p.setIsNew(dto.getIsNew());
        p.setIsRecommend(dto.getIsRecommend());
        p.setSortOrder(dto.getSortOrder());
        p.setStatus(dto.getStatus());
        p.setExtraAttrs(dto.getExtraAttrs());
    }
}
