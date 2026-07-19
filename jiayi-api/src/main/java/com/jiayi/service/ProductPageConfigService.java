package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiayi.dto.ProductPageConfigDTO;
import com.jiayi.entity.PmsProductPageConfig;
import com.jiayi.mapper.PmsProductPageConfigMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductPageConfigService {

    private static final Logger log = LoggerFactory.getLogger(ProductPageConfigService.class);

    private final PmsProductPageConfigMapper mapper;
    private final ObjectMapper objectMapper;

    public ProductPageConfigService(PmsProductPageConfigMapper mapper, ObjectMapper objectMapper) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    public PmsProductPageConfig getByProductId(Long productId) {
        return mapper.selectOne(new LambdaQueryWrapper<PmsProductPageConfig>()
                .eq(PmsProductPageConfig::getProductId, productId));
    }

    public void saveOrUpdate(Long productId, ProductPageConfigDTO dto) {
        log.info("saveOrUpdate 开始 productId={}, aiEnabled={}, videoEnabled={}, videoUrl={}, galleryEnabled={}, galleryImages={}, disclaimerEnabled={}",
                productId, dto.getAiEnabled(), dto.getVideoEnabled(), dto.getVideoUrl(),
                dto.getGalleryEnabled(), dto.getGalleryImages(), dto.getDisclaimerEnabled());
        PmsProductPageConfig entity = getByProductId(productId);
        if (entity == null) {
            entity = new PmsProductPageConfig();
            entity.setProductId(productId);
            log.info("新建配置 entity");
        } else {
            log.info("更新现有配置 entity.id={}", entity.getId());
        }
        entity.setAiEnabled(dto.getAiEnabled());
        entity.setVideoEnabled(dto.getVideoEnabled());
        entity.setVideoCover(dto.getVideoCover());
        entity.setVideoUrl(dto.getVideoUrl());
        entity.setGalleryEnabled(dto.getGalleryEnabled());
        if (dto.getGalleryImages() != null) {
            try {
                String json = objectMapper.writeValueAsString(dto.getGalleryImages());
                entity.setGalleryImages(json);
                log.info("galleryImages JSON序列化成功: {}", json);
            } catch (Exception e) {
                log.warn("galleryImages JSON序列化失败", e);
            }
        } else {
            log.warn("galleryImages 为 null，跳过设置");
        }
        entity.setDisclaimerEnabled(dto.getDisclaimerEnabled());
        entity.setDisclaimerText(dto.getDisclaimerText());
        entity.setDisclaimerColor(dto.getDisclaimerColor());
        if (entity.getId() == null) {
            int rows = mapper.insert(entity);
            log.info("insert 结果 rows={}, 生成id={}", rows, entity.getId());
        } else {
            int rows = mapper.updateById(entity);
            log.info("updateById 结果 rows={}", rows);
        }
        log.info("saveOrUpdate 完成");
    }

    public ProductPageConfigDTO getDTO(Long productId) {
        PmsProductPageConfig entity = getByProductId(productId);
        if (entity == null) {
            ProductPageConfigDTO d = new ProductPageConfigDTO();
            d.setAiEnabled(1);
            d.setVideoEnabled(0);
            d.setGalleryEnabled(0);
            d.setDisclaimerEnabled(0);
            d.setDisclaimerColor("#999");
            return d;
        }
        ProductPageConfigDTO dto = new ProductPageConfigDTO();
        dto.setAiEnabled(entity.getAiEnabled());
        dto.setVideoEnabled(entity.getVideoEnabled());
        dto.setVideoCover(entity.getVideoCover());
        dto.setVideoUrl(entity.getVideoUrl());
        dto.setGalleryEnabled(entity.getGalleryEnabled());
        dto.setDisclaimerEnabled(entity.getDisclaimerEnabled());
        dto.setDisclaimerText(entity.getDisclaimerText());
        dto.setDisclaimerColor(entity.getDisclaimerColor());
        if (entity.getGalleryImages() != null) {
            try {
                List<String> list = objectMapper.readValue(entity.getGalleryImages(), new TypeReference<List<String>>() {});
                dto.setGalleryImages(list);
            } catch (Exception e) {
                log.warn("galleryImages JSON反序列化失败", e);
            }
        }
        return dto;
    }
}
