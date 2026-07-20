package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.dto.AdminFavoriteVO;
import com.jiayi.entity.UmsUserFavorite;
import com.jiayi.entity.PmsProduct;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.UmsUserFavoriteMapper;
import com.jiayi.mapper.PmsProductMapper;
import com.jiayi.mapper.UmsUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final UmsUserFavoriteMapper favoriteMapper;
    private final PmsProductMapper productMapper;
    private final UmsUserMapper userMapper;

    public FavoriteService(UmsUserFavoriteMapper favoriteMapper, PmsProductMapper productMapper, UmsUserMapper userMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }

    public Page<AdminFavoriteVO> pageWithNames(String userName, String productName, Long userId, Long productId, int page, int size) {
        boolean hasUserName = userName != null && !userName.isBlank();
        boolean hasProductName = productName != null && !productName.isBlank();
        if (hasUserName || hasProductName) {
            List<Long> matchedUserIds = null;
            List<Long> matchedProductIds = null;
            if (hasUserName) {
                List<UmsUser> users = userMapper.selectList(new LambdaQueryWrapper<UmsUser>()
                        .like(UmsUser::getNickname, userName));
                if (users.isEmpty()) return new Page<>(page, size);
                matchedUserIds = users.stream().map(UmsUser::getId).collect(Collectors.toList());
            }
            if (hasProductName) {
                List<PmsProduct> products = productMapper.selectList(new LambdaQueryWrapper<PmsProduct>()
                        .like(PmsProduct::getName, productName));
                if (products.isEmpty()) return new Page<>(page, size);
                matchedProductIds = products.stream().map(PmsProduct::getId).collect(Collectors.toList());
            }
            LambdaQueryWrapper<UmsUserFavorite> wrapper = new LambdaQueryWrapper<UmsUserFavorite>().orderByDesc(UmsUserFavorite::getCreateTime);
            if (matchedUserIds != null) wrapper.in(UmsUserFavorite::getUserId, matchedUserIds);
            if (matchedProductIds != null) wrapper.in(UmsUserFavorite::getProductId, matchedProductIds);
            List<UmsUserFavorite> all = favoriteMapper.selectList(wrapper);
            return enrichPage(all, page, size);
        }
        LambdaQueryWrapper<UmsUserFavorite> wrapper = new LambdaQueryWrapper<UmsUserFavorite>().orderByDesc(UmsUserFavorite::getCreateTime);
        if (userId != null) wrapper.eq(UmsUserFavorite::getUserId, userId);
        if (productId != null) wrapper.eq(UmsUserFavorite::getProductId, productId);
        Page<UmsUserFavorite> raw = favoriteMapper.selectPage(new Page<>(page, size), wrapper);
        return enrichPage(raw, page, size);
    }

    private Page<AdminFavoriteVO> enrichPage(Page<UmsUserFavorite> raw, int page, int size) {
        List<UmsUserFavorite> records = raw.getRecords();
        long total = raw.getTotal();
        Set<Long> userIds = records.stream().map(UmsUserFavorite::getUserId).collect(Collectors.toSet());
        Set<Long> productIds = records.stream().map(UmsUserFavorite::getProductId).collect(Collectors.toSet());
        Map<Long, String> userNames = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(UmsUser::getId, u -> u.getNickname() != null ? u.getNickname() : "", (a, b) -> a));
        Map<Long, String> productNames = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getName, (a, b) -> a));
        List<AdminFavoriteVO> list = records.stream().map(f -> {
            AdminFavoriteVO vo = new AdminFavoriteVO();
            vo.setId(f.getId());
            vo.setUserId(f.getUserId());
            vo.setUserName(userNames.getOrDefault(f.getUserId(), ""));
            vo.setProductId(f.getProductId());
            vo.setProductName(productNames.getOrDefault(f.getProductId(), ""));
            vo.setCreateTime(f.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
        Page<AdminFavoriteVO> result = new Page<>(page, size, total);
        result.setRecords(list);
        return result;
    }

    private Page<AdminFavoriteVO> enrichPage(List<UmsUserFavorite> all, int page, int size) {
        long total = all.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, (int) total);
        List<UmsUserFavorite> pageRecords = from >= total ? List.of() : all.subList(from, to);
        Set<Long> userIds = pageRecords.stream().map(UmsUserFavorite::getUserId).collect(Collectors.toSet());
        Set<Long> productIds = pageRecords.stream().map(UmsUserFavorite::getProductId).collect(Collectors.toSet());
        Map<Long, String> userNames = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(UmsUser::getId, u -> u.getNickname() != null ? u.getNickname() : "", (a, b) -> a));
        Map<Long, String> productNames = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getName, (a, b) -> a));
        List<AdminFavoriteVO> list = pageRecords.stream().map(f -> {
            AdminFavoriteVO vo = new AdminFavoriteVO();
            vo.setId(f.getId());
            vo.setUserId(f.getUserId());
            vo.setUserName(userNames.getOrDefault(f.getUserId(), ""));
            vo.setProductId(f.getProductId());
            vo.setProductName(productNames.getOrDefault(f.getProductId(), ""));
            vo.setCreateTime(f.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
        Page<AdminFavoriteVO> result = new Page<>(page, size, total);
        result.setRecords(list);
        return result;
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
