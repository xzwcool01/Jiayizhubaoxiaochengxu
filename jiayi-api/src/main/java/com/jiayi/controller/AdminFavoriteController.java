package com.jiayi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.dto.AdminFavoriteVO;
import com.jiayi.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/favorite")
public class AdminFavoriteController {

    private final FavoriteService favoriteService;

    public AdminFavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/list")
    public R<Page<AdminFavoriteVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String productName) {
        return R.ok(favoriteService.pageWithNames(userName, productName, userId, productId, page, size));
    }
}
