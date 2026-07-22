package com.jiayi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.dto.AdminReviewVO;
import com.jiayi.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/review")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/list")
    public R<Page<AdminReviewVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer rating) {
        return R.ok(reviewService.adminPage(page, size, productName, nickname, rating));
    }

    @GetMapping("/{id}")
    public R<AdminReviewVO> get(@PathVariable Long id) {
        AdminReviewVO vo = reviewService.adminGetById(id);
        return vo != null ? R.ok(vo) : R.error("评价不存在");
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer rating = body.get("rating") != null ? ((Number) body.get("rating")).intValue() : null;
        String content = (String) body.get("content");
        Integer isAnonymous = body.get("isAnonymous") != null ? ((Number) body.get("isAnonymous")).intValue() : null;
        Integer isTop = body.get("isTop") != null ? ((Number) body.get("isTop")).intValue() : null;
        Integer status = body.get("status") != null ? ((Number) body.get("status")).intValue() : null;
        reviewService.adminUpdate(id, rating, content, isAnonymous, isTop, status);
        return R.ok(null);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        reviewService.adminDelete(id);
        return R.ok(null);
    }
}
