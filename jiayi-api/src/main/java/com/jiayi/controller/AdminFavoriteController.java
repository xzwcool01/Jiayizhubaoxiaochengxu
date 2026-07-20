package com.jiayi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.entity.UmsUserFavorite;
import com.jiayi.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/favorite")
public class AdminFavoriteController {

    private final FavoriteService favoriteService;

    public AdminFavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/list")
    public R<Page<UmsUserFavorite>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId) {
        Page<UmsUserFavorite> result;
        if (userId != null) {
            result = favoriteService.pageByUserId(userId, page, size);
        } else if (productId != null) {
            result = favoriteService.pageByProductId(productId, page, size);
        } else {
            result = favoriteService.pageAll(page, size);
        }
        return R.ok(result);
    }

    @GetMapping("/user-list")
    public R<Page<UmsUserFavorite>> listByUser(@RequestParam Long userId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return R.ok(favoriteService.pageByUserId(userId, page, size));
    }

    @GetMapping("/product-list")
    public R<Page<UmsUserFavorite>> listByProduct(@RequestParam Long productId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return R.ok(favoriteService.pageByProductId(productId, page, size));
    }
}
