package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.dto.AdminCartVO;
import com.jiayi.dto.CartItemVO;
import com.jiayi.entity.PmsProduct;
import com.jiayi.entity.UmsUser;
import com.jiayi.entity.UmsUserCart;
import com.jiayi.mapper.PmsProductMapper;
import com.jiayi.mapper.UmsUserCartMapper;
import com.jiayi.mapper.UmsUserMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final UmsUserCartMapper cartMapper;
    private final PmsProductMapper productMapper;
    private final UmsUserMapper userMapper;

    public CartService(UmsUserCartMapper cartMapper, PmsProductMapper productMapper, UmsUserMapper userMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }

    public void add(Long userId, Long productId, int quantity) {
        UmsUserCart existing = cartMapper.selectOne(new LambdaQueryWrapper<UmsUserCart>()
                .eq(UmsUserCart::getUserId, userId)
                .eq(UmsUserCart::getProductId, productId));
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartMapper.updateById(existing);
        } else {
            UmsUserCart c = new UmsUserCart();
            c.setUserId(userId);
            c.setProductId(productId);
            c.setQuantity(quantity);
            c.setSelected(true);
            cartMapper.insert(c);
        }
    }

    public List<CartItemVO> listByUser(Long userId) {
        List<UmsUserCart> carts = cartMapper.selectList(new LambdaQueryWrapper<UmsUserCart>()
                .eq(UmsUserCart::getUserId, userId)
                .orderByDesc(UmsUserCart::getCreateTime));
        if (carts.isEmpty()) return List.of();
        Set<Long> productIds = carts.stream().map(UmsUserCart::getProductId).collect(Collectors.toSet());
        Map<Long, PmsProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(PmsProduct::getId, p -> p, (a, b) -> a));
        return carts.stream().map(c -> {
            CartItemVO vo = new CartItemVO();
            vo.setId(c.getId());
            vo.setProductId(c.getProductId());
            vo.setQuantity(c.getQuantity());
            vo.setSelected(c.getSelected());
            vo.setCreateTime(c.getCreateTime());
            PmsProduct p = productMap.get(c.getProductId());
            if (p != null) {
                vo.setName(p.getName());
                vo.setMainImage(p.getMainImage());
                vo.setCategoryId(p.getCategoryId());
                vo.setSpecs(p.getSpecs());
                vo.setPrice(p.getPrice().doubleValue());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    public void updateQuantity(Long id, int quantity) {
        UmsUserCart c = cartMapper.selectById(id);
        if (c != null) {
            c.setQuantity(Math.max(quantity, 1));
            cartMapper.updateById(c);
        }
    }

    public void updateSelected(Long id, boolean selected) {
        UmsUserCart c = cartMapper.selectById(id);
        if (c != null) {
            c.setSelected(selected);
            cartMapper.updateById(c);
        }
    }

    public void toggleSelectAll(Long userId, boolean selected) {
        cartMapper.update(null, new LambdaUpdateWrapper<UmsUserCart>()
                .eq(UmsUserCart::getUserId, userId)
                .set(UmsUserCart::getSelected, selected));
    }

    public void removeById(Long id) {
        cartMapper.deleteById(id);
    }

    public void removeBatch(List<Long> ids) {
        cartMapper.deleteBatchIds(ids);
    }

    public List<UmsUserCart> getSelectedItems(Long userId, List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return cartMapper.selectList(new LambdaQueryWrapper<UmsUserCart>()
                    .eq(UmsUserCart::getUserId, userId)
                    .eq(UmsUserCart::getSelected, true));
        }
        return cartMapper.selectList(new LambdaQueryWrapper<UmsUserCart>()
                .eq(UmsUserCart::getUserId, userId)
                .in(UmsUserCart::getId, cartItemIds)
                .eq(UmsUserCart::getSelected, true));
    }

    public void removeSelected(List<Long> cartItemIds) {
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            cartMapper.deleteBatchIds(cartItemIds);
        }
    }

    public Page<AdminCartVO> pageWithNames(String userName, String productName, Long userId, Long productId, int page, int size) {
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
            LambdaQueryWrapper<UmsUserCart> wrapper = new LambdaQueryWrapper<UmsUserCart>().orderByDesc(UmsUserCart::getCreateTime);
            if (matchedUserIds != null) wrapper.in(UmsUserCart::getUserId, matchedUserIds);
            if (matchedProductIds != null) wrapper.in(UmsUserCart::getProductId, matchedProductIds);
            List<UmsUserCart> all = cartMapper.selectList(wrapper);
            return enrichPage(all, page, size);
        }
        LambdaQueryWrapper<UmsUserCart> wrapper = new LambdaQueryWrapper<UmsUserCart>().orderByDesc(UmsUserCart::getCreateTime);
        if (userId != null) wrapper.eq(UmsUserCart::getUserId, userId);
        if (productId != null) wrapper.eq(UmsUserCart::getProductId, productId);
        Page<UmsUserCart> raw = cartMapper.selectPage(new Page<>(page, size), wrapper);
        return enrichPage(raw, page, size);
    }

    private Page<AdminCartVO> enrichPage(Page<UmsUserCart> raw, int page, int size) {
        List<UmsUserCart> records = raw.getRecords();
        long total = raw.getTotal();
        return toAdminPage(records, total, page, size);
    }

    private Page<AdminCartVO> enrichPage(List<UmsUserCart> all, int page, int size) {
        long total = all.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, (int) total);
        List<UmsUserCart> pageRecords = from >= total ? List.of() : all.subList(from, to);
        return toAdminPage(pageRecords, total, page, size);
    }

    private Page<AdminCartVO> toAdminPage(List<UmsUserCart> records, long total, int page, int size) {
        Set<Long> userIds = records.stream().map(UmsUserCart::getUserId).collect(Collectors.toSet());
        Set<Long> productIds = records.stream().map(UmsUserCart::getProductId).collect(Collectors.toSet());
        Map<Long, String> userNames = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(UmsUser::getId, u -> u.getNickname() != null ? u.getNickname() : "", (a, b) -> a));
        Map<Long, String> productNames = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getName, (a, b) -> a));
        List<AdminCartVO> list = records.stream().map(c -> {
            AdminCartVO vo = new AdminCartVO();
            vo.setId(c.getId());
            vo.setUserId(c.getUserId());
            vo.setUserName(userNames.getOrDefault(c.getUserId(), ""));
            vo.setProductId(c.getProductId());
            vo.setProductName(productNames.getOrDefault(c.getProductId(), ""));
            vo.setQuantity(c.getQuantity());
            vo.setSelected(c.getSelected());
            vo.setCreateTime(c.getCreateTime());
            vo.setUpdateTime(c.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());
        Page<AdminCartVO> result = new Page<>(page, size, total);
        result.setRecords(list);
        return result;
    }
}