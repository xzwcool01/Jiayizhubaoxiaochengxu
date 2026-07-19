package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.ProductPageConfigDTO;
import com.jiayi.service.ProductPageConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/{productId}/page-config")
public class PublicPageConfigController {

    private final ProductPageConfigService configService;

    public PublicPageConfigController(ProductPageConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public R<ProductPageConfigDTO> getPageConfig(@PathVariable Long productId) {
        return R.ok(configService.getDTO(productId));
    }
}
