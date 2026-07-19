package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.ProductPageConfigDTO;
import com.jiayi.service.ProductPageConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/product")
public class ProductPageConfigController {

    private static final Logger log = LoggerFactory.getLogger(ProductPageConfigController.class);

    private final ProductPageConfigService configService;

    public ProductPageConfigController(ProductPageConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/{productId}/page-config")
    public R<ProductPageConfigDTO> getConfig(@PathVariable Long productId) {
        return R.ok(configService.getDTO(productId));
    }

    @PutMapping("/{productId}/page-config")
    public R<Void> updateConfig(@PathVariable Long productId, @RequestBody ProductPageConfigDTO dto) {
        log.info("收到页面配置更新请求 productId={}, aiEnabled={}, videoEnabled={}, galleryEnabled={}",
                productId, dto.getAiEnabled(), dto.getVideoEnabled(), dto.getGalleryEnabled());
        configService.saveOrUpdate(productId, dto);
        log.info("商品页面配置更新完成 productId={}", productId);
        return R.ok(null);
    }
}
