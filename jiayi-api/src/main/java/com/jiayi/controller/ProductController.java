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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final PmsProductService productService;
    private final PmsCategoryMapper categoryMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductController(PmsProductService productService, PmsCategoryMapper categoryMapper) {
        this.productService = productService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/list")
    public R<IPage<PmsProduct>> list(ProductQueryDTO dto) {
        log.info("查询商品列表: type={}, categoryId={}, keyword={}, status={}, page={}, size={}",
                dto.getProductType(), dto.getCategoryId(), dto.getKeyword(), dto.getStatus(), dto.getPage(), dto.getSize());
        LambdaQueryWrapper<PmsProduct> q = new LambdaQueryWrapper<>();
        q.orderByDesc(PmsProduct::getWeight).orderByDesc(PmsProduct::getSortOrder).orderByDesc(PmsProduct::getId);
        if (dto.getProductType() != null) q.eq(PmsProduct::getProductType, dto.getProductType());
        if (dto.getCategoryId() != null) q.eq(PmsProduct::getCategoryId, dto.getCategoryId());
        if (dto.getStatus() != null) q.eq(PmsProduct::getStatus, dto.getStatus());
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            q.and(w -> w.like(PmsProduct::getName, dto.getKeyword())
                    .or().like(PmsProduct::getSubtitle, dto.getKeyword()));
        }
        Page<PmsProduct> page = productService.page(new Page<>(dto.getPage(), dto.getSize()), q);
        log.info("查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return R.ok(page);
    }

    @GetMapping("/{id}")
    public R<PmsProduct> get(@PathVariable Long id) {
        PmsProduct p = productService.getById(id);
        if (p == null) {
            log.warn("商品不存在: id={}", id);
            return R.error("商品不存在");
        }
        return R.ok(p);
    }

    @PostMapping
    public R<PmsProduct> create(@RequestBody ProductDTO dto) {
        log.info("===== 开始新增商品: name={}, type={}, categoryId={}, price={}", dto.getName(), dto.getProductType(), dto.getCategoryId(), dto.getPrice());
        PmsProduct p = toEntity(dto);
        try {
            boolean saved = productService.save(p);
            log.info("step1 - save结果: saved={}, id={}", saved, p.getId());
            if (!saved) {
                log.error("step1 - 保存失败, 无id生成");
                return R.error("保存失败");
            }
        } catch (Exception e) {
            log.error("step1 - save异常: {}", e.getMessage(), e);
            return R.error("保存失败: " + e.getMessage());
        }

        try {
            productService.moveTempImages(p);
            log.info("step2 - moveTempImages 完成");
        } catch (Exception e) {
            log.error("step2 - moveTempImages 异常: {}", e.getMessage(), e);
        }

        try {
            boolean updated = productService.updateById(p);
            log.info("step3 - updateById结果: updated={}", updated);
        } catch (Exception e) {
            log.error("step3 - updateById异常: {}", e.getMessage(), e);
        }

        log.info("===== 商品新增流程结束, id={}", p.getId());
        return R.ok(p);
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<PmsProduct> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        log.info("更新商品: id={}, name={}", id, dto.getName());
        PmsProduct p = productService.getById(id);
        if (p == null) {
            log.warn("更新商品不存在: id={}", id);
            return R.error("商品不存在");
        }
        merge(p, dto);
        try {
            productService.moveTempImages(p);
            productService.updateById(p);
            log.info("商品更新成功: id={}", id);
        } catch (Exception e) {
            log.error("商品更新失败: id={}, error={}", id, e.getMessage(), e);
            return R.error("更新失败: " + e.getMessage());
        }
        return R.ok(p);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        log.info("删除商品: id={}", id);
        if (!productService.removeById(id)) {
            log.warn("删除商品失败: id={}", id);
            return R.error("删除失败");
        }
        log.info("商品删除成功: id={}", id);
        return R.ok(null);
    }

    @GetMapping("/categories")
    public R<List<PmsCategory>> categories() {
        List<PmsCategory> list = categoryMapper.selectList(
                new LambdaQueryWrapper<PmsCategory>().orderByAsc(PmsCategory::getSort));
        log.debug("查询类目列表: 总数={}", list.size());
        return R.ok(list);
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
            try { p.setImages(objectMapper.writeValueAsString(dto.getImages())); } catch (JsonProcessingException e) {
                log.error("序列化图片列表失败: {}", e.getMessage());
            }
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
        p.setWeight(dto.getWeight());
        p.setStatus(dto.getStatus());
        p.setExtraAttrs(dto.getExtraAttrs());
    }
}
