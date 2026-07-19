package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.service.ShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    private static final Logger log = LoggerFactory.getLogger(ShareController.class);

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @GetMapping("/poster/{productId}")
    public R<String> getPoster(@PathVariable Long productId) {
        try {
            String url = shareService.generatePoster(productId);
            return R.ok(url);
        } catch (Exception e) {
            log.error("海报生成失败 productId={}: {}", productId, e.getMessage());
            return R.error("海报生成失败: " + e.getMessage());
        }
    }
}
