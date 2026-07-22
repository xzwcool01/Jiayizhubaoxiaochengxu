package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.dto.AdminCouponVO;
import com.jiayi.dto.UserCouponVO;
import com.jiayi.entity.*;
import com.jiayi.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final SmsCouponMapper couponMapper;
    private final SmsCouponProductMapper couponProductMapper;
    private final SmsUserCouponMapper userCouponMapper;
    private final PmsProductMapper productMapper;
    private final UmsUserMapper userMapper;

    public CouponService(SmsCouponMapper couponMapper, SmsCouponProductMapper couponProductMapper,
                         SmsUserCouponMapper userCouponMapper, PmsProductMapper productMapper,
                         UmsUserMapper userMapper) {
        this.couponMapper = couponMapper;
        this.couponProductMapper = couponProductMapper;
        this.userCouponMapper = userCouponMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }

    public List<UserCouponVO> getUserCoupons(Long userId) {
        List<SmsUserCoupon> ucList = userCouponMapper.selectList(new LambdaQueryWrapper<SmsUserCoupon>()
                .eq(SmsUserCoupon::getUserId, userId)
                .eq(SmsUserCoupon::getUsed, false));
        if (ucList.isEmpty()) return List.of();
        Set<Long> couponIds = ucList.stream().map(SmsUserCoupon::getCouponId).collect(Collectors.toSet());
        Map<Long, SmsCoupon> couponMap = couponMapper.selectBatchIds(couponIds).stream()
                .filter(c -> c.getStatus() == 1 && c.getEndTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toMap(SmsCoupon::getId, c -> c));
        return ucList.stream()
                .filter(uc -> couponMap.containsKey(uc.getCouponId()))
                .map(uc -> {
                    SmsCoupon c = couponMap.get(uc.getCouponId());
                    UserCouponVO vo = new UserCouponVO();
                    vo.setId(uc.getId());
                    vo.setCouponId(c.getId());
                    vo.setName(c.getName());
                    vo.setType(c.getType());
                    vo.setValue(c.getValue());
                    vo.setMinAmount(c.getMinAmount());
                    vo.setMaxAmount(c.getMaxAmount());
                    return vo;
                }).collect(Collectors.toList());
    }

    public List<UserCouponVO> getUserApplicableCoupons(Long userId, List<Long> productIds) {
        List<UserCouponVO> all = getUserCoupons(userId);
        if (all.isEmpty() || productIds == null || productIds.isEmpty()) return all;
        Set<Long> couponIds = all.stream().map(UserCouponVO::getCouponId).collect(Collectors.toSet());
        Map<Long, Set<Long>> productMap = couponProductMapper.selectList(
                new LambdaQueryWrapper<SmsCouponProduct>().in(SmsCouponProduct::getCouponId, couponIds))
                .stream()
                .collect(Collectors.groupingBy(
                        SmsCouponProduct::getCouponId,
                        Collectors.mapping(SmsCouponProduct::getProductId, Collectors.toSet())));
        return all.stream().filter(vo -> {
            Set<Long> allowed = productMap.get(vo.getCouponId());
            if (allowed == null || allowed.isEmpty()) return true;
            if (allowed.contains(null)) return true;
            for (Long pid : productIds) {
                if (allowed.contains(pid)) return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    public List<SmsCoupon> getApplicableCoupons(List<Long> productIds) {
        List<SmsCoupon> all = couponMapper.selectList(new LambdaQueryWrapper<SmsCoupon>()
                .eq(SmsCoupon::getStatus, 1)
                .ge(SmsCoupon::getEndTime, LocalDateTime.now())
                .le(SmsCoupon::getStartTime, LocalDateTime.now()));
        if (all.isEmpty()) return List.of();
        List<SmsCoupon> result = new ArrayList<>();
        for (SmsCoupon c : all) {
            List<SmsCouponProduct> links = couponProductMapper.selectList(
                    new LambdaQueryWrapper<SmsCouponProduct>().eq(SmsCouponProduct::getCouponId, c.getId()));
            if (links.isEmpty()) { result.add(c); continue; }
            Set<Long> allowed = links.stream().map(SmsCouponProduct::getProductId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (allowed.isEmpty()) { result.add(c); continue; }
            for (Long pid : productIds) {
                if (allowed.contains(pid)) { result.add(c); break; }
            }
        }
        return result;
    }

    @Transactional
    public int issueToUser(Long couponId, List<Long> userIds) {
        SmsCoupon c = couponMapper.selectById(couponId);
        if (c == null) return 0;
        int total = c.getTotalCount() != null ? c.getTotalCount() : Integer.MAX_VALUE;
        int issued = c.getIssuedCount() != null ? c.getIssuedCount() : 0;
        int remaining = total - issued;
        if (remaining <= 0) return 0;
        int count = 0;
        for (Long uid : userIds) {
            if (count >= remaining) break;
            long exist = userCouponMapper.selectCount(new LambdaQueryWrapper<SmsUserCoupon>()
                    .eq(SmsUserCoupon::getUserId, uid)
                    .eq(SmsUserCoupon::getCouponId, couponId));
            if (exist >= c.getPerUserLimit()) continue;
            SmsUserCoupon uc = new SmsUserCoupon();
            uc.setUserId(uid);
            uc.setCouponId(couponId);
            uc.setUsed(false);
            userCouponMapper.insert(uc);
            count++;
        }
        if (count > 0) {
            c.setIssuedCount(issued + count);
            couponMapper.updateById(c);
        }
        return count;
    }

    @Transactional
    public int issueToAllUsers(Long couponId) {
        SmsCoupon c = couponMapper.selectById(couponId);
        if (c == null) return 0;
        int total = c.getTotalCount() != null ? c.getTotalCount() : Integer.MAX_VALUE;
        int issued = c.getIssuedCount() != null ? c.getIssuedCount() : 0;
        int remaining = total - issued;
        if (remaining <= 0) return 0;
        List<UmsUser> allUsers = userMapper.selectList(null);
        List<Long> ids = allUsers.stream().map(UmsUser::getId).collect(Collectors.toList());
        if (ids.size() > remaining) ids = ids.subList(0, remaining);
        return issueToUser(couponId, ids);
    }

    @Transactional
    public void useCoupon(Long userCouponId, Long orderId) {
        SmsUserCoupon uc = userCouponMapper.selectById(userCouponId);
        if (uc == null) return;
        uc.setUsed(true);
        uc.setOrderId(orderId);
        uc.setUsedTime(LocalDateTime.now());
        userCouponMapper.updateById(uc);
        SmsCoupon c = couponMapper.selectById(uc.getCouponId());
        if (c != null) {
            c.setUsedCount(c.getUsedCount() + 1);
            couponMapper.updateById(c);
        }
    }

    public AdminCouponVO getAdminVO(Long id) {
        SmsCoupon c = couponMapper.selectById(id);
        if (c == null) return null;
        return toAdminVO(c);
    }

    public Page<AdminCouponVO> pageAdmin(int page, int size) {
        Page<SmsCoupon> p = couponMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<SmsCoupon>().orderByDesc(SmsCoupon::getCreateTime));
        Page<AdminCouponVO> result = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        result.setRecords(p.getRecords().stream().map(this::toAdminVO).collect(Collectors.toList()));
        return result;
    }

    private AdminCouponVO toAdminVO(SmsCoupon c) {
        AdminCouponVO vo = new AdminCouponVO();
        vo.setId(c.getId());
        vo.setName(c.getName());
        vo.setType(c.getType());
        vo.setValue(c.getValue());
        vo.setMinAmount(c.getMinAmount());
        vo.setMaxAmount(c.getMaxAmount());
        vo.setStartTime(c.getStartTime());
        vo.setEndTime(c.getEndTime());
        vo.setTotalCount(c.getTotalCount());
        vo.setPerUserLimit(c.getPerUserLimit());
        vo.setUsedCount(c.getUsedCount());
        vo.setIssuedCount(c.getIssuedCount());
        vo.setStatus(c.getStatus());
        List<SmsCouponProduct> links = couponProductMapper.selectList(
                new LambdaQueryWrapper<SmsCouponProduct>().eq(SmsCouponProduct::getCouponId, c.getId()));
        List<Long> pids = links.stream().map(SmsCouponProduct::getProductId).filter(Objects::nonNull).collect(Collectors.toList());
        vo.setProductIds(pids);
        if (!pids.isEmpty()) {
            Map<Long, String> names = productMapper.selectBatchIds(pids).stream()
                    .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getName));
            vo.setProductNames(pids.stream().map(pid -> names.getOrDefault(pid, "")).collect(Collectors.toList()));
        }
        return vo;
    }

    @Transactional
    public void saveCoupon(AdminCouponVO vo) {
        SmsCoupon c = new SmsCoupon();
        c.setName(vo.getName());
        c.setType(vo.getType());
        c.setValue(vo.getValue());
        c.setMinAmount(vo.getMinAmount());
        c.setMaxAmount(vo.getMaxAmount());
        c.setStartTime(vo.getStartTime());
        c.setEndTime(vo.getEndTime());
        c.setTotalCount(vo.getTotalCount());
        c.setPerUserLimit(vo.getPerUserLimit());
        c.setUsedCount(0);
        c.setIssuedCount(0);
        c.setStatus(vo.getStatus());
        couponMapper.insert(c);
        saveProductLinks(c.getId(), vo.getProductIds());
    }

    @Transactional
    public void updateCoupon(AdminCouponVO vo) {
        SmsCoupon c = couponMapper.selectById(vo.getId());
        if (c == null) return;
        c.setName(vo.getName());
        c.setType(vo.getType());
        c.setValue(vo.getValue());
        c.setMinAmount(vo.getMinAmount());
        c.setMaxAmount(vo.getMaxAmount());
        c.setStartTime(vo.getStartTime());
        c.setEndTime(vo.getEndTime());
        c.setTotalCount(vo.getTotalCount());
        c.setPerUserLimit(vo.getPerUserLimit());
        c.setStatus(vo.getStatus());
        couponMapper.updateById(c);
        couponProductMapper.delete(new LambdaQueryWrapper<SmsCouponProduct>().eq(SmsCouponProduct::getCouponId, vo.getId()));
        saveProductLinks(vo.getId(), vo.getProductIds());
    }

    private void saveProductLinks(Long couponId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            couponProductMapper.insert(new SmsCouponProduct() {{ setCouponId(couponId); }});
            return;
        }
        for (Long pid : productIds) {
            SmsCouponProduct link = new SmsCouponProduct();
            link.setCouponId(couponId);
            link.setProductId(pid);
            couponProductMapper.insert(link);
        }
    }

    public List<UmsUser> searchUsers(String keyword) {
        return userMapper.selectList(new LambdaQueryWrapper<UmsUser>()
                .like(UmsUser::getNickname, keyword)
                .or().like(UmsUser::getPhone, keyword));
    }
}