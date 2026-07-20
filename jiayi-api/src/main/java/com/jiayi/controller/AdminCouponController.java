package com.jiayi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.dto.AdminCouponVO;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/coupon")
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/list")
    public R<Page<AdminCouponVO>> list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return R.ok(couponService.pageAdmin(page, size));
    }

    @GetMapping("/{id}")
    public R<AdminCouponVO> get(@PathVariable Long id) {
        AdminCouponVO vo = couponService.getAdminVO(id);
        return vo != null ? R.ok(vo) : R.error("优惠券不存在");
    }

    @PostMapping
    public R<Void> create(@RequestBody AdminCouponVO vo) {
        couponService.saveCoupon(vo);
        return R.ok(null);
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody AdminCouponVO vo) {
        vo.setId(id);
        couponService.updateCoupon(vo);
        return R.ok(null);
    }

    @PostMapping("/issue")
    public R<Void> issue(@RequestBody Map<String, Object> body) {
        Long couponId = Long.valueOf(body.get("couponId").toString());
        boolean all = Boolean.TRUE.equals(body.get("all"));
        if (all) {
            couponService.issueToAllUsers(couponId);
        } else {
            @SuppressWarnings("unchecked")
            List<String> userIds = (List<String>) body.get("userIds");
            List<Long> ids = userIds.stream().map(Long::valueOf).collect(java.util.stream.Collectors.toList());
            couponService.issueToUser(couponId, ids);
        }
        return R.ok(null);
    }

    @GetMapping("/search-users")
    public R<List<UmsUser>> searchUsers(@RequestParam String keyword) {
        return R.ok(couponService.searchUsers(keyword));
    }
}