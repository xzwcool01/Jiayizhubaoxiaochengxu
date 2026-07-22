package com.jiayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.dto.CreateReviewDTO;
import com.jiayi.dto.ReviewVO;
import com.jiayi.entity.OmsOrderReview;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.OmsOrderReviewMapper;
import com.jiayi.service.ReviewService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final UmsUserService userService;
    private final OmsOrderReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, UmsUserService userService,
                            OmsOrderReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping("/create")
    public R<ReviewVO> create(@RequestBody CreateReviewDTO dto) {
        UmsUser user = userService.lambdaQuery().eq(UmsUser::getOpenid, dto.getOpenid()).one();
        if (user == null) return R.error("用户不存在");
        try {
            ReviewVO vo = reviewService.create(dto, user.getId());
            return R.ok(vo);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping("/by-order")
    public R<ReviewVO> byOrder(@RequestParam Long orderId, @RequestParam String openid) {
        UmsUser user = userService.lambdaQuery().eq(UmsUser::getOpenid, openid).one();
        if (user == null) return R.error("用户不存在");
        ReviewVO vo = reviewService.getByOrderId(orderId, user.getId());
        return vo != null ? R.ok(vo) : R.error("评价不存在");
    }

    @GetMapping("/product/{productId}/top2")
    public R<List<ReviewVO>> top2(@PathVariable Long productId) {
        return R.ok(reviewService.getTop2(productId));
    }

    @GetMapping("/product/{productId}/count")
    public R<Long> count(@PathVariable Long productId) {
        long count = reviewMapper.selectCount(
                new LambdaQueryWrapper<OmsOrderReview>()
                        .eq(OmsOrderReview::getProductId, productId)
                        .eq(OmsOrderReview::getStatus, 1));
        return R.ok(count);
    }

    @GetMapping("/product/{productId}")
    public R<Page<ReviewVO>> page(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(reviewService.pageByProduct(productId, page, size));
    }
}
