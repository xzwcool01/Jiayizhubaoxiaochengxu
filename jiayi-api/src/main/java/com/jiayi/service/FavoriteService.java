package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.entity.UmsUserFavorite;
import com.jiayi.entity.PmsProduct;
import com.jiayi.mapper.UmsUserFavoriteMapper;
import com.jiayi.mapper.PmsProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final UmsUserFavoriteMapper favoriteMapper;
    private final PmsProductMapper productMapper;

    public FavoriteService(UmsUserFavoriteMapper favoriteMapper, PmsProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
    }

    public UmsUserFavorite getByUserAndProduct(Long userId, Long productId) {
        return favoriteMapper.selectOne(new LambdaQueryWrapper<UmsUserFavorite>()
                .eq(UmsUserFavorite::getUserId, userId)
                .eq(UmsUserFavorite::getProductId, productId));
    }

    public boolean toggle(Long userId, Long productId) {
        UmsUserFavorite existing = getByUserAndProduct(userId, productId);
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            return false;
        }
        UmsUserFavorite f = new UmsUserFavorite();
        f.setUserId(userId);
        f.setProductId(productId);
        favoriteMapper.insert(f);
        return true;
    }

    public boolean isFavorited(Long userId, Long productId) {
        return favoriteMapper.selectCount(new LambdaQueryWrapper<UmsUserFavorite>()
                .eq(UmsUserFavorite::getUserId, userId)
                .eq(UmsUserFavorite::getProductId, productId)) > 0;
    }

    public List<PmsProduct> listByUser(Long userId) {
        List<UmsUserFavorite> favs = favoriteMapper.selectList(new LambdaQueryWrapper<UmsUserFavorite>()
                .eq(UmsUserFavorite::getUserId, userId)
                .orderByDesc(UmsUserFavorite::getCreateTime));
        if (favs.isEmpty()) return List.of();
        List<Long> ids = favs.stream().map(UmsUserFavorite::getProductId).collect(Collectors.toList());
        return productMapper.selectBatchIds(ids);
    }

    public Page<UmsUserFavorite> pageByUserId(Long userId, int page, int size) {
        return favoriteMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UmsUserFavorite>()
                        .eq(UmsUserFavorite::getUserId, userId)
                        .orderByDesc(UmsUserFavorite::getCreateTime));
    }

    public Page<UmsUserFavorite> pageByProductId(Long productId, int page, int size) {
        return favoriteMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UmsUserFavorite>()
                        .eq(UmsUserFavorite::getProductId, productId)
                        .orderByDesc(UmsUserFavorite::getCreateTime));
    }

    public Page<UmsUserFavorite> pageAll(int page, int size) {
        return favoriteMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UmsUserFavorite>().orderByDesc(UmsUserFavorite::getCreateTime));
    }
}
