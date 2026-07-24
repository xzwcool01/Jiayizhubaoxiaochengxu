package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expert-post")
public class ExpertPostController {

    private final ReviewService reviewService;

    public ExpertPostController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/list")
    public R<List<Map<String, Object>>> list() {
        return R.ok(reviewService.getExpertPosts());
    }

    @GetMapping("/detail")
    public R<Map<String, Object>> detail(@RequestParam Long id) {
        Map<String, Object> post = reviewService.getExpertPostDetail(id);
        return post != null ? R.ok(post) : R.error("晒单不存在");
    }
}
