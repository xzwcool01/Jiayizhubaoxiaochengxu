package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.UserCouponVO;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.CouponService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    private final CouponService couponService;
    private final UmsUserService userService;

    public CouponController(CouponService couponService, UmsUserService userService) {
        this.couponService = couponService;
        this.userService = userService;
    }

    @GetMapping("/my")
    public R<List<UserCouponVO>> my(@RequestParam String openid) {
        R<UmsUser> r = userService.getUserInfo(openid);
        if (r.getCode() != 200 || r.getData() == null) return R.ok(List.of());
        return R.ok(couponService.getUserCoupons(r.getData().getId()));
    }

    @GetMapping("/my-applicable")
    public R<List<UserCouponVO>> myApplicable(@RequestParam String openid, @RequestParam String productIds) {
        R<UmsUser> r = userService.getUserInfo(openid);
        if (r.getCode() != 200 || r.getData() == null) return R.ok(List.of());
        List<Long> pids = Arrays.stream(productIds.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).map(Long::parseLong).collect(Collectors.toList());
        return R.ok(couponService.getUserApplicableCoupons(r.getData().getId(), pids));
    }
}