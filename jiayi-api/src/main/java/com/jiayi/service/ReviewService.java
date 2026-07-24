package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.dto.AdminReviewVO;
import com.jiayi.dto.CreateReviewDTO;
import com.jiayi.dto.ReviewVO;
import com.jiayi.entity.*;
import com.jiayi.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final OmsOrderReviewMapper reviewMapper;
    private final OmsOrderMapper orderMapper;
    private final UmsUserMapper userMapper;
    private final PmsProductMapper productMapper;
    private final UmsUserService userService;
    private final ActionPointsService actionPointsService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReviewService(OmsOrderReviewMapper reviewMapper, OmsOrderMapper orderMapper,
                         UmsUserMapper userMapper, PmsProductMapper productMapper,
                         UmsUserService umsUserService, ActionPointsService actionPointsService,
                         MemberService memberService) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.userService = umsUserService;
        this.actionPointsService = actionPointsService;
        this.memberService = memberService;
    }

    @Transactional
    public ReviewVO create(CreateReviewDTO dto, Long userId) {
        OmsOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 3) throw new RuntimeException("订单未完成，无法评价");
        long count = reviewMapper.selectCount(new LambdaQueryWrapper<OmsOrderReview>()
                .eq(OmsOrderReview::getOrderId, dto.getOrderId())
                .eq(OmsOrderReview::getUserId, userId));
        if (count > 0) throw new RuntimeException("该订单已评价");

        OmsOrderReview review = new OmsOrderReview();
        review.setOrderId(dto.getOrderId());
        review.setProductId(dto.getProductId());
        review.setUserId(userId);
        review.setRating(dto.getRating());
        review.setContent(dto.getContent() != null ? dto.getContent() : "");
        try {
            review.setImages(dto.getImages() != null && !dto.getImages().isEmpty()
                    ? objectMapper.writeValueAsString(dto.getImages()) : null);
        } catch (Exception e) {
            review.setImages(null);
        }
        review.setIsAnonymous(dto.getIsAnonymous() != null ? dto.getIsAnonymous() : 0);
        review.setStatus(1);
        reviewMapper.insert(review);

        order.setReviewed(1);
        orderMapper.updateById(order);

        int points = actionPointsService.getPoints("review");
        if (points > 0) {
            UmsUser user = userMapper.selectById(userId);
            if (user != null) {
                user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + points);
                userMapper.updateById(user);
                memberService.addPointsLog(userId, "review", "商品评价", points);
            }
        }

        return toReviewVO(review);
    }

    public ReviewVO getByOrderId(Long orderId, Long userId) {
        OmsOrderReview r = reviewMapper.selectOne(
                new LambdaQueryWrapper<OmsOrderReview>()
                        .eq(OmsOrderReview::getOrderId, orderId)
                        .eq(OmsOrderReview::getUserId, userId));
        if (r == null) return null;
        ReviewVO vo = toReviewVO(r);
        UmsUser user = userMapper.selectById(r.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }
        return vo;
    }

    public List<ReviewVO> getTop2(Long productId) {
        List<OmsOrderReview> pinned = reviewMapper.selectList(
                new LambdaQueryWrapper<OmsOrderReview>()
                        .eq(OmsOrderReview::getProductId, productId)
                        .eq(OmsOrderReview::getStatus, 1)
                        .eq(OmsOrderReview::getIsTop, 1)
                        .orderByDesc(OmsOrderReview::getCreateTime));
        if (pinned.size() >= 2) {
            return pinned.stream().limit(2).map(this::toReviewVO).collect(Collectors.toList());
        }
        int remaining = 2 - pinned.size();
        List<OmsOrderReview> topRated = reviewMapper.selectList(
                new LambdaQueryWrapper<OmsOrderReview>()
                        .eq(OmsOrderReview::getProductId, productId)
                        .eq(OmsOrderReview::getStatus, 1)
                        .ne(OmsOrderReview::getIsTop, 1)
                        .orderByDesc(OmsOrderReview::getRating)
                        .orderByDesc(OmsOrderReview::getCreateTime)
                        .last("LIMIT " + remaining));
        List<OmsOrderReview> combined = new java.util.ArrayList<>(pinned);
        combined.addAll(topRated);
        return combined.stream().limit(2).map(this::toReviewVO).collect(Collectors.toList());
    }

    public Page<ReviewVO> pageByProduct(Long productId, int page, int size) {
        LambdaQueryWrapper<OmsOrderReview> q = new LambdaQueryWrapper<OmsOrderReview>()
                .eq(OmsOrderReview::getProductId, productId)
                .eq(OmsOrderReview::getStatus, 1)
                .orderByDesc(OmsOrderReview::getCreateTime);
        Page<OmsOrderReview> p = reviewMapper.selectPage(new Page<>(page, size), q);
        Page<ReviewVO> result = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        Set<Long> uids = p.getRecords().stream().map(OmsOrderReview::getUserId).collect(Collectors.toSet());
        Map<Long, String> nameMap = userMapper.selectBatchIds(uids).stream()
                .collect(Collectors.toMap(UmsUser::getId, UmsUser::getNickname));
        Map<Long, String> avatarMap = userMapper.selectBatchIds(uids).stream()
                .collect(Collectors.toMap(UmsUser::getId, u -> u.getAvatar() != null ? u.getAvatar() : ""));
        result.setRecords(p.getRecords().stream().map(r -> {
            ReviewVO vo = toReviewVO(r);
            vo.setNickname(nameMap.getOrDefault(r.getUserId(), ""));
            vo.setAvatar(avatarMap.getOrDefault(r.getUserId(), ""));
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }

    public Page<AdminReviewVO> adminPage(int page, int size, String productName, String nickname, Integer rating) {
        LambdaQueryWrapper<OmsOrderReview> q = new LambdaQueryWrapper<OmsOrderReview>()
                .orderByDesc(OmsOrderReview::getCreateTime);
        if (rating != null) q.eq(OmsOrderReview::getRating, rating);
        if (productName != null && !productName.isBlank()) {
            List<Long> pids = productMapper.selectList(
                    new LambdaQueryWrapper<PmsProduct>().like(PmsProduct::getName, productName))
                    .stream().map(PmsProduct::getId).collect(Collectors.toList());
            if (pids.isEmpty()) {
                Page<AdminReviewVO> empty = new Page<>(page, size, 0);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
            q.in(OmsOrderReview::getProductId, pids);
        }
        Page<OmsOrderReview> p = reviewMapper.selectPage(new Page<>(page, size), q);
        Page<AdminReviewVO> result = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        if (p.getRecords().isEmpty()) {
            result.setRecords(Collections.emptyList());
            return result;
        }
        Set<Long> uids = p.getRecords().stream().map(OmsOrderReview::getUserId).collect(Collectors.toSet());
        Map<Long, String> nameMap = uids.isEmpty() ? Map.of() : userMapper.selectBatchIds(uids).stream()
                .collect(Collectors.toMap(UmsUser::getId, UmsUser::getNickname));
        Set<Long> pids = p.getRecords().stream().map(OmsOrderReview::getProductId).collect(Collectors.toSet());
        Map<Long, String> prodNameMap = pids.isEmpty() ? Map.of() : productMapper.selectBatchIds(pids).stream()
                .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getName));
        Set<Long> orderIds = p.getRecords().stream().map(OmsOrderReview::getOrderId).collect(Collectors.toSet());
        Map<Long, OmsOrder> orderMap = orderIds.isEmpty() ? Map.of() : orderMapper.selectBatchIds(orderIds).stream()
                .collect(Collectors.toMap(OmsOrder::getId, o -> o));
        if (nickname != null && !nickname.isBlank()) {
            String finalNickname = nickname;
            List<AdminReviewVO> filtered = p.getRecords().stream()
                    .filter(r -> nameMap.getOrDefault(r.getUserId(), "").contains(finalNickname))
                    .map(r -> toAdminReviewVO(r, nameMap, prodNameMap, orderMap))
                    .collect(Collectors.toList());
            result.setRecords(filtered);
            result.setTotal(filtered.size());
        } else {
            result.setRecords(p.getRecords().stream()
                    .map(r -> toAdminReviewVO(r, nameMap, prodNameMap, orderMap))
                    .collect(Collectors.toList()));
        }
        return result;
    }

    public AdminReviewVO adminGetById(Long id) {
        OmsOrderReview r = reviewMapper.selectById(id);
        if (r == null) return null;
        Set<Long> uids = Set.of(r.getUserId());
        Map<Long, String> nameMap = userMapper.selectBatchIds(uids).stream()
                .collect(Collectors.toMap(UmsUser::getId, UmsUser::getNickname));
        Set<Long> pids = Set.of(r.getProductId());
        Map<Long, String> prodNameMap = productMapper.selectBatchIds(pids).stream()
                .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getName));
        OmsOrder order = orderMapper.selectById(r.getOrderId());
        Map<Long, OmsOrder> orderMap = order != null ? Map.of(order.getId(), order) : Map.of();
        return toAdminReviewVO(r, nameMap, prodNameMap, orderMap);
    }

    public void adminUpdate(Long id, Integer rating, String content, Integer isAnonymous, Integer isTop,
                             Integer showOnExpert, Integer expertSortOrder, String expertTag, Integer expertLikes,
                             String expertNickname, Integer status) {
        OmsOrderReview r = reviewMapper.selectById(id);
        if (r == null) return;
        if (rating != null) r.setRating(rating);
        if (content != null) r.setContent(content);
        if (isAnonymous != null) r.setIsAnonymous(isAnonymous);
        if (isTop != null) r.setIsTop(isTop);
        if (showOnExpert != null) r.setShowOnExpert(showOnExpert);
        if (expertSortOrder != null) r.setExpertSortOrder(expertSortOrder);
        if (expertTag != null) r.setExpertTag(expertTag);
        if (expertLikes != null) r.setExpertLikes(expertLikes);
        if (expertNickname != null) r.setExpertNickname(expertNickname);
        if (status != null) r.setStatus(status);
        reviewMapper.updateById(r);
    }

    @Transactional
    public Long createManual(String content, String nickname, String tag, Integer likes, Integer sortOrder, List<String> imageUrls) {
        OmsOrderReview r = new OmsOrderReview();
        r.setContent(content != null ? content : "");
        r.setIsManual(1);
        r.setShowOnExpert(1);
        r.setStatus(1);
        r.setExpertNickname(nickname != null && !nickname.isBlank() ? nickname : "管理员");
        r.setExpertTag(tag != null ? tag : "");
        r.setExpertLikes(likes != null ? likes : 0);
        r.setExpertSortOrder(sortOrder != null ? sortOrder : 0);
        if (imageUrls != null && !imageUrls.isEmpty()) {
            try {
                r.setImages(objectMapper.writeValueAsString(imageUrls));
            } catch (Exception e) {
                r.setImages(null);
            }
        }
        r.setOrderId(null);
        r.setProductId(null);
        r.setRating(5);
        r.setUserId(0L);
        reviewMapper.insert(r);
        return r.getId();
    }

    public List<Map<String, Object>> getExpertPosts() {
        List<OmsOrderReview> list = reviewMapper.selectList(
                new LambdaQueryWrapper<OmsOrderReview>()
                        .eq(OmsOrderReview::getShowOnExpert, 1)
                        .eq(OmsOrderReview::getStatus, 1)
                        .orderByDesc(OmsOrderReview::getExpertSortOrder)
                        .orderByDesc(OmsOrderReview::getCreateTime));
        Set<Long> uids = list.stream().map(OmsOrderReview::getUserId).filter(id -> id != null && id > 0).collect(Collectors.toSet());
        Map<Long, UmsUser> userMap = uids.isEmpty() ? Map.of() : userMapper.selectBatchIds(uids).stream()
                .collect(Collectors.toMap(UmsUser::getId, u -> u));
        return list.stream().map(r -> toExpertPostMap(r, userMap)).collect(Collectors.toList());
    }

    public Map<String, Object> getExpertPostDetail(Long id) {
        OmsOrderReview r = reviewMapper.selectById(id);
        if (r == null || r.getShowOnExpert() != 1 || r.getStatus() != 1) return null;
        Set<Long> uids = r.getUserId() != null && r.getUserId() > 0 ? Set.of(r.getUserId()) : Set.of();
        Map<Long, UmsUser> userMap = uids.isEmpty() ? Map.of() : userMapper.selectBatchIds(uids).stream()
                .collect(Collectors.toMap(UmsUser::getId, u -> u));
        return toExpertPostMap(r, userMap);
    }

    private Map<String, Object> toExpertPostMap(OmsOrderReview r, Map<Long, UmsUser> userMap) {
        Map<String, Object> m = new java.util.HashMap<>();
        m.put("id", r.getId());
        m.put("content", r.getContent());
        m.put("images", parseImages(r.getImages()));
        m.put("expertTag", r.getExpertTag() != null ? r.getExpertTag() : "");
        m.put("expertLikes", r.getExpertLikes() != null ? r.getExpertLikes() : 0);
        m.put("createTime", r.getCreateTime());
        if (r.getIsManual() == 1) {
            m.put("nickname", r.getExpertNickname() != null && !r.getExpertNickname().isBlank() ? r.getExpertNickname() : "管理员");
            m.put("avatar", "");
        } else {
            UmsUser user = userMap.get(r.getUserId());
            m.put("nickname", user != null ? (user.getNickname() != null ? user.getNickname() : "") : "");
            m.put("avatar", user != null ? (user.getAvatar() != null ? user.getAvatar() : "") : "");
        }
        return m;
    }

    public void adminDelete(Long id) {
        reviewMapper.deleteById(id);
    }

    private ReviewVO toReviewVO(OmsOrderReview r) {
        ReviewVO vo = new ReviewVO();
        vo.setId(r.getId());
        vo.setOrderId(r.getOrderId());
        vo.setProductId(r.getProductId());
        vo.setUserId(r.getUserId());
        vo.setRating(r.getRating());
        vo.setContent(r.getContent());
        vo.setImages(parseImages(r.getImages()));
        vo.setIsAnonymous(r.getIsAnonymous());
        vo.setIsTop(r.getIsTop());
        vo.setShowOnExpert(r.getShowOnExpert());
        vo.setExpertSortOrder(r.getExpertSortOrder());
        vo.setExpertTag(r.getExpertTag());
        vo.setExpertLikes(r.getExpertLikes());
        vo.setExpertNickname(r.getExpertNickname());
        vo.setStatus(r.getStatus());
        vo.setCreateTime(r.getCreateTime());
        return vo;
    }

    private AdminReviewVO toAdminReviewVO(OmsOrderReview r, Map<Long, String> nameMap,
                                          Map<Long, String> prodNameMap, Map<Long, OmsOrder> orderMap) {
        AdminReviewVO vo = new AdminReviewVO();
        vo.setId(r.getId());
        vo.setOrderId(r.getOrderId());
        vo.setProductId(r.getProductId());
        vo.setUserId(r.getUserId());
        vo.setNickname(nameMap.getOrDefault(r.getUserId(), ""));
        vo.setProductName(prodNameMap.getOrDefault(r.getProductId(), ""));
        vo.setRating(r.getRating());
        vo.setContent(r.getContent());
        vo.setImages(parseImages(r.getImages()));
        vo.setIsAnonymous(r.getIsAnonymous());
        vo.setIsTop(r.getIsTop());
        vo.setShowOnExpert(r.getShowOnExpert());
        vo.setExpertSortOrder(r.getExpertSortOrder());
        vo.setExpertTag(r.getExpertTag());
        vo.setExpertLikes(r.getExpertLikes());
        vo.setIsManual(r.getIsManual());
        vo.setExpertNickname(r.getExpertNickname());
        vo.setStatus(r.getStatus());
        vo.setCreateTime(r.getCreateTime());
        OmsOrder order = orderMap.get(r.getOrderId());
        if (order != null) {
            vo.setTotalAmount(order.getTotalAmount());
            vo.setPayAmount(order.getPayAmount());
        }
        return vo;
    }

    private List<String> parseImages(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
