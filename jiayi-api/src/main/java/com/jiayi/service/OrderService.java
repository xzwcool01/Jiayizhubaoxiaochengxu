package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.dto.CreateOrderDTO;
import com.jiayi.dto.OrderVO;
import com.jiayi.dto.OrderItemVO;
import com.jiayi.dto.AdminOrderVO;
import com.jiayi.entity.*;
import com.jiayi.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final UmsUserAddressMapper addressMapper;
    private final SmsCouponMapper couponMapper;
    private final SmsUserCouponMapper userCouponMapper;
    private final PmsProductMapper productMapper;
    private final UmsUserMapper userMapper;
    private final CartService cartService;
    private final CouponService couponService;
    private final PointsService pointsService;
    private final OmsOrderDeliveryMapper deliveryMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final DateTimeFormatter SN_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public OrderService(OmsOrderMapper orderMapper, OmsOrderItemMapper orderItemMapper,
                        UmsUserAddressMapper addressMapper, SmsCouponMapper couponMapper,
                        SmsUserCouponMapper userCouponMapper,
                        PmsProductMapper productMapper, UmsUserMapper userMapper,
                        CartService cartService, CouponService couponService, PointsService pointsService,
                        OmsOrderDeliveryMapper deliveryMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.addressMapper = addressMapper;
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.cartService = cartService;
        this.couponService = couponService;
        this.pointsService = pointsService;
        this.deliveryMapper = deliveryMapper;
    }

    @Transactional
    public OmsOrder createOrder(CreateOrderDTO dto, Long userId) {
        List<UmsUserCart> cartItems = cartService.getSelectedItems(userId, dto.getCartItemIds());
        if (cartItems.isEmpty()) throw new RuntimeException("未选择商品");

        UmsUserAddress addr = addressMapper.selectById(dto.getAddressId());
        if (addr == null) throw new RuntimeException("请选择收货地址");

        Set<Long> productIds = cartItems.stream().map(UmsUserCart::getProductId).collect(Collectors.toSet());
        Map<Long, PmsProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(PmsProduct::getId, p -> p));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OmsOrderItem> orderItems = new ArrayList<>();
        for (UmsUserCart c : cartItems) {
            PmsProduct p = productMap.get(c.getProductId());
            if (p == null) continue;
            BigDecimal subtotal = p.getPrice().multiply(BigDecimal.valueOf(c.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
            OmsOrderItem item = new OmsOrderItem();
            item.setProductId(p.getId());
            item.setProductName(p.getName());
            item.setProductSpecs(p.getSpecs());
            item.setProductImage(p.getMainImage());
            item.setPrice(p.getPrice());
            item.setQuantity(c.getQuantity());
            item.setSubtotal(subtotal);
            orderItems.add(item);
        }
        if (orderItems.isEmpty()) throw new RuntimeException("无可下单商品");

        BigDecimal couponAmount = BigDecimal.ZERO;
        if (dto.getCouponId() != null) {
            SmsUserCoupon uc = userCouponMapper.selectById(dto.getCouponId());
            if (uc != null && !uc.getUsed()) {
                SmsCoupon c = couponMapper.selectById(uc.getCouponId());
                if (c != null && totalAmount.compareTo(c.getMinAmount()) >= 0) {
                    if (c.getType() == 0) {
                        couponAmount = c.getValue().min(totalAmount);
                        if (c.getMaxAmount().compareTo(BigDecimal.ZERO) > 0)
                            couponAmount = couponAmount.min(c.getMaxAmount());
                    } else {
                        couponAmount = totalAmount.multiply(c.getValue()).divide(BigDecimal.valueOf(100));
                        if (c.getMaxAmount().compareTo(BigDecimal.ZERO) > 0)
                            couponAmount = couponAmount.min(c.getMaxAmount());
                    }
                }
            }
        }

        BigDecimal pointsAmount = BigDecimal.ZERO;
        int pointsDeduct = 0;
        if (Boolean.TRUE.equals(dto.getUsePoints())) {
            UmsUser user = userMapper.selectById(userId);
            if (user != null && user.getPoints() > 0) {
                Set<Long> pids = cartItems.stream().map(UmsUserCart::getProductId).collect(Collectors.toSet());
                List<com.jiayi.dto.PointsRuleVO> rules = pointsService.getRulesForProducts(new ArrayList<>(pids));
                if (!rules.isEmpty()) {
                    com.jiayi.dto.PointsRuleVO rule = rules.get(0);
                    BigDecimal orderAfterCoupon = totalAmount.subtract(couponAmount);
                    if (Integer.valueOf(1).equals(rule.getType())) {
                        int maxPoints = user.getPoints() - (user.getPoints() % rule.getPoints());
                        int maxPointsFromOrder = orderAfterCoupon.divide(rule.getAmount(), 0, java.math.RoundingMode.DOWN).intValue() * rule.getPoints();
                        int usePoints = Math.min(maxPoints, maxPointsFromOrder);
                        if (usePoints >= rule.getPoints()) {
                            pointsDeduct = usePoints;
                            pointsAmount = BigDecimal.valueOf((long) usePoints / rule.getPoints()).multiply(rule.getAmount());
                            if (pointsAmount.compareTo(orderAfterCoupon) > 0)
                                pointsAmount = orderAfterCoupon;
                        }
                    } else {
                        if (user.getPoints() >= rule.getPoints()) {
                            pointsDeduct = rule.getPoints();
                            pointsAmount = rule.getAmount().min(orderAfterCoupon);
                        }
                    }
                }
            }
        }

        BigDecimal payAmount = totalAmount.subtract(couponAmount).subtract(pointsAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) payAmount = BigDecimal.ZERO;

        String orderSn = "JY" + LocalDateTime.now().format(SN_FORMAT) + String.format("%04d", new Random().nextInt(10000));

        OmsOrder order = new OmsOrder();
        order.setOrderSn(orderSn);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setCouponAmount(couponAmount);
        order.setPointsAmount(pointsAmount);
        order.setPointsDeduct(pointsDeduct);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPaymentMethod(0);
        order.setStatus(0);
        String snapshotJson;
        try {
            Map<String, Object> addrMap = new LinkedHashMap<>();
            addrMap.put("name", addr.getName());
            addrMap.put("phone", addr.getPhone());
            addrMap.put("province", addr.getProvince());
            addrMap.put("city", addr.getCity());
            addrMap.put("district", addr.getDistrict());
            addrMap.put("detail", addr.getDetail());
            snapshotJson = objectMapper.writeValueAsString(addrMap);
        } catch (Exception e) {
            log.warn("地址序列化失败, addr={}, err={}", addr, e.getMessage());
            snapshotJson = "{}";
        }
        log.info("address_snapshot: {}", snapshotJson);
        order.setAddressSnapshot(snapshotJson);
        order.setNote(dto.getNote() != null ? dto.getNote() : "");
        order.setCouponId(dto.getCouponId());
        orderMapper.insert(order);

        for (OmsOrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        cartService.removeSelected(cartItems.stream().map(UmsUserCart::getId).collect(Collectors.toList()));

        if (dto.getCouponId() != null && couponAmount.compareTo(BigDecimal.ZERO) > 0) {
            couponService.useCoupon(dto.getCouponId(), order.getId());
        }
        if (pointsDeduct > 0) {
            pointsService.deductPoints(userId, pointsDeduct);
        }

        return order;
    }

    public OrderVO getOrderDetail(Long orderId, Long userId) {
        OmsOrder order = orderMapper.selectOne(new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getId, orderId).eq(OmsOrder::getUserId, userId));
        if (order == null) return null;
        return toOrderVO(order);
    }

    public List<OrderVO> listByUser(Long userId, Integer status) {
        LambdaQueryWrapper<OmsOrder> q = new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getUserId, userId);
        if (status != null) q.eq(OmsOrder::getStatus, status);
        q.orderByDesc(OmsOrder::getCreateTime);
        return orderMapper.selectList(q).stream().map(this::toOrderVO).collect(Collectors.toList());
    }

    @Transactional
    public void payOrder(Long orderId) {
        OmsOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 0) return;
        order.setStatus(1);
        order.setPaymentMethod(1);
        order.setPaidAt(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        OmsOrder order = orderMapper.selectOne(new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getId, orderId).eq(OmsOrder::getUserId, userId));
        if (order == null || order.getStatus() != 0) return;
        order.setStatus(4);
        orderMapper.updateById(order);
    }

    public Page<AdminOrderVO> adminPage(int page, int size, Long userId, Integer status, String userName) {
        LambdaQueryWrapper<OmsOrder> q = new LambdaQueryWrapper<OmsOrder>().orderByDesc(OmsOrder::getCreateTime);
        if (userId != null) q.eq(OmsOrder::getUserId, userId);
        if (status != null) q.eq(OmsOrder::getStatus, status);
        if (userName != null && !userName.isBlank()) {
            List<Long> ids = userMapper.selectList(new LambdaQueryWrapper<UmsUser>()
                    .like(UmsUser::getNickname, userName)
                    .or().like(UmsUser::getPhone, userName))
                    .stream().map(UmsUser::getId).collect(Collectors.toList());
            if (ids.isEmpty()) {
                Page<AdminOrderVO> empty = new Page<>(page, size, 0);
                empty.setRecords(List.of());
                return empty;
            }
            q.in(OmsOrder::getUserId, ids);
        }
        Page<OmsOrder> p = orderMapper.selectPage(new Page<>(page, size), q);
        Page<AdminOrderVO> result = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        Set<Long> uids = p.getRecords().stream().map(OmsOrder::getUserId).collect(Collectors.toSet());
        Map<Long, String> nameMap = userMapper.selectBatchIds(uids).stream()
                .collect(Collectors.toMap(UmsUser::getId, UmsUser::getNickname));
        Set<Long> orderIds = p.getRecords().stream().map(OmsOrder::getId).collect(Collectors.toSet());
        Map<Long, List<OmsOrderItem>> itemMap = orderItemMapper.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().in(OmsOrderItem::getOrderId, orderIds))
                .stream().collect(Collectors.groupingBy(OmsOrderItem::getOrderId));
        Map<Long, OmsOrderDelivery> deliveryMap = deliveryMapper.selectList(
                new LambdaQueryWrapper<OmsOrderDelivery>().in(OmsOrderDelivery::getOrderId, orderIds))
                .stream().collect(Collectors.toMap(OmsOrderDelivery::getOrderId, d -> d, (a, b) -> a));
        result.setRecords(p.getRecords().stream().map(o -> {
            AdminOrderVO vo = new AdminOrderVO();
            vo.setId(o.getId());
            vo.setOrderSn(o.getOrderSn());
            vo.setUserId(o.getUserId());
            vo.setUserName(nameMap.getOrDefault(o.getUserId(), ""));
            vo.setTotalAmount(o.getTotalAmount());
            vo.setPayAmount(o.getPayAmount());
            vo.setCouponAmount(o.getCouponAmount());
            vo.setPointsAmount(o.getPointsAmount());
            vo.setPointsDeduct(o.getPointsDeduct());
            vo.setFreightAmount(o.getFreightAmount());
            vo.setPaymentMethod(o.getPaymentMethod());
            vo.setStatus(o.getStatus());
            vo.setAddressSnapshot(o.getAddressSnapshot());
            vo.setNote(o.getNote());
            vo.setPaidAt(o.getPaidAt());
            vo.setCouponId(o.getCouponId());
            vo.setCreateTime(o.getCreateTime());
            List<OmsOrderItem> orderItems = itemMap.getOrDefault(o.getId(), List.of());
            vo.setItems(orderItems.stream().map(i -> {
                OrderItemVO iov = new OrderItemVO();
                iov.setId(i.getId());
                iov.setProductId(i.getProductId());
                iov.setProductName(i.getProductName());
                iov.setProductSpecs(i.getProductSpecs());
                iov.setProductImage(i.getProductImage());
                iov.setPrice(i.getPrice());
                iov.setQuantity(i.getQuantity());
                iov.setSubtotal(i.getSubtotal());
                return iov;
            }).collect(Collectors.toList()));
            OmsOrderDelivery delivery = deliveryMap.get(o.getId());
            if (delivery != null) {
                vo.setTrackingNo(delivery.getTrackingNo());
                vo.setExpressCompany(delivery.getExpressCompany());
            }
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }

    public long countUnpaid(Long userId) {
        return orderMapper.selectCount(
                new LambdaQueryWrapper<OmsOrder>()
                        .eq(OmsOrder::getUserId, userId)
                        .eq(OmsOrder::getStatus, 0));
    }

    private OrderVO toOrderVO(OmsOrder order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderSn(order.getOrderSn());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setCouponAmount(order.getCouponAmount());
        vo.setPointsAmount(order.getPointsAmount());
        vo.setPointsDeduct(order.getPointsDeduct());
        vo.setPaymentMethod(order.getPaymentMethod());
        vo.setStatus(order.getStatus());
        vo.setAddressSnapshot(order.getAddressSnapshot());
        vo.setNote(order.getNote());
        vo.setPaidAt(order.getPaidAt());
        vo.setCreateTime(order.getCreateTime());
        vo.setMockPay(true);
        List<OmsOrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, order.getId()));
        vo.setItems(items.stream().map(i -> {
            OrderItemVO iov = new OrderItemVO();
            iov.setId(i.getId());
            iov.setProductId(i.getProductId());
            iov.setProductName(i.getProductName());
            iov.setProductSpecs(i.getProductSpecs());
            iov.setProductImage(i.getProductImage());
            iov.setPrice(i.getPrice());
            iov.setQuantity(i.getQuantity());
            iov.setSubtotal(i.getSubtotal());
            return iov;
        }).collect(Collectors.toList()));
        return vo;
    }
}