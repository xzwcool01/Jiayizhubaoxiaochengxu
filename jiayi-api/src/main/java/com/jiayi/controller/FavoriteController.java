package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.PmsProduct;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.FavoriteService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UmsUserService userService;

    public FavoriteController(FavoriteService favoriteService, UmsUserService userService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    @PostMapping("/toggle")
    public R<Boolean> toggle(@RequestBody Map<String, Object> body) {
        String openid = (String) body.get("openid");
        Long productId = Long.valueOf(body.get("productId").toString());
        UmsUser user = userService.getUserInfo(openid);
        if (user == null) return R.error("用户未找到");
        boolean liked = favoriteService.toggle(user.getId(), productId);
        return R.ok(liked);
    }

    @GetMapping("/status")
    public R<Boolean> status(@RequestParam String openid, @RequestParam Long productId) {
        UmsUser user = userService.getUserInfo(openid);
        if (user == null) return R.ok(false);
        return R.ok(favoriteService.isFavorited(user.getId(), productId));
    }

    @GetMapping("/list")
    public R<List<PmsProduct>> list(@RequestParam String openid) {
        UmsUser user = userService.getUserInfo(openid);
        if (user == null) return R.ok(List.of());
        return R.ok(favoriteService.listByUser(user.getId()));
    }

    @PostMapping("/remove")
    public R<Void> remove(@RequestBody Map<String, Object> body) {
        String openid = (String) body.get("openid");
        Long productId = Long.valueOf(body.get("productId").toString());
        UmsUser user = userService.getUserInfo(openid);
        if (user == null) return R.error("用户未找到");
        favoriteService.toggle(user.getId(), productId);
        return R.ok(null);
    }
}
