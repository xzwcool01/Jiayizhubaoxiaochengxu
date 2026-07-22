package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.config.SfExpressConfig;
import com.jiayi.dto.DeliveryVO;
import com.jiayi.dto.ShipDTO;
import com.jiayi.entity.OmsOrder;
import com.jiayi.entity.OmsOrderDelivery;
import com.jiayi.entity.OmsOrderItem;
import com.jiayi.mapper.OmsOrderDeliveryMapper;
import com.jiayi.mapper.OmsOrderItemMapper;
import com.jiayi.mapper.OmsOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final OmsOrderDeliveryMapper deliveryMapper;
    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final SfExpressService sfExpressService;
    private final SfExpressConfig sfExpressConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    public DeliveryService(OmsOrderDeliveryMapper deliveryMapper, OmsOrderMapper orderMapper,
                           OmsOrderItemMapper orderItemMapper, SfExpressService sfExpressService,
                           SfExpressConfig sfExpressConfig) {
        this.deliveryMapper = deliveryMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.sfExpressService = sfExpressService;
        this.sfExpressConfig = sfExpressConfig;
    }

    @Transactional
    public Map<String, Object> ship(ShipDTO dto) throws Exception {
        OmsOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 1) throw new RuntimeException("订单状态不是待发货");

        String trackingNo = dto.getTrackingNo();
        String expressCompany = dto.getExpressCompany();
        Map<String, Object> sfResult = null;

        if ((trackingNo == null || trackingNo.isBlank()) && sfExpressService.isEnabled()) {
            Map<String, Object> addrMap = parseAddressSnapshot(order.getAddressSnapshot());
            List<OmsOrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, order.getId()));

            String itemName = items.stream().map(OmsOrderItem::getProductName).collect(Collectors.joining(", "));
            int totalQty = items.stream().mapToInt(OmsOrderItem::getQuantity).sum();
            double totalWeight = totalQty * 0.3;

            sfResult = sfExpressService.createOrder(
                    order.getOrderSn(),
                    (String) addrMap.get("name"), (String) addrMap.get("phone"),
                    (String) addrMap.get("province"), (String) addrMap.get("city"),
                    (String) addrMap.get("district"), (String) addrMap.get("detail"),
                    itemName, totalQty, totalWeight
            );

            List<Map<String, Object>> waybillList = (List<Map<String, Object>>) sfResult.get("waybillNoInfoList");
            if (waybillList != null && !waybillList.isEmpty()) {
                trackingNo = (String) waybillList.get(0).get("waybillNo");
            }
            expressCompany = "顺丰速运";
            log.info("顺丰自动生成运单号: orderSn={}, waybillNo={}", order.getOrderSn(), trackingNo);
        }

        if (trackingNo == null || trackingNo.isBlank()) {
            throw new RuntimeException("运单号不能为空");
        }

        OmsOrderDelivery delivery = new OmsOrderDelivery();
        delivery.setOrderId(dto.getOrderId());
        delivery.setExpressCompany(expressCompany);
        delivery.setTrackingNo(trackingNo);
        if (sfResult != null && !sfResult.isEmpty()) {
            Map<String, Object> waybillExtra = new HashMap<>(sfResult);
            waybillExtra.put("_sender", Map.of(
                    "name", sfExpressConfig.getSenderName(),
                    "mobile", sfExpressConfig.getSenderPhone(),
                    "province", sfExpressConfig.getSenderProvince(),
                    "city", sfExpressConfig.getSenderCity(),
                    "county", sfExpressConfig.getSenderDistrict(),
                    "address", sfExpressConfig.getSenderAddress()
            ));
            Map<String, Object> addrMap = parseAddressSnapshot(order.getAddressSnapshot());
            waybillExtra.put("_receiver", Map.of(
                    "name", addrMap.getOrDefault("name", ""),
                    "mobile", addrMap.getOrDefault("phone", ""),
                    "province", addrMap.getOrDefault("province", ""),
                    "city", addrMap.getOrDefault("city", ""),
                    "county", addrMap.getOrDefault("district", ""),
                    "address", addrMap.getOrDefault("detail", "")
            ));
            delivery.setWaybillData(objectMapper.writeValueAsString(waybillExtra));
        }
        delivery.setStatus(0);
        delivery.setShippedAt(LocalDateTime.now());
        deliveryMapper.insert(delivery);
        order.setStatus(2);
        orderMapper.updateById(order);

        return sfResult;
    }

    public DeliveryVO getDelivery(Long orderId) {
        OmsOrderDelivery d = deliveryMapper.selectOne(
                new LambdaQueryWrapper<OmsOrderDelivery>().eq(OmsOrderDelivery::getOrderId, orderId));
        if (d == null) return null;
        DeliveryVO vo = new DeliveryVO();
        vo.setId(d.getId());
        vo.setOrderId(d.getOrderId());
        vo.setExpressCompany(d.getExpressCompany());
        vo.setTrackingNo(d.getTrackingNo());
        vo.setStatus(d.getStatus());
        vo.setShippedAt(d.getShippedAt());
        vo.setReceivedAt(d.getReceivedAt());
        return vo;
    }

    @Transactional
    public void receive(Long orderId, Long userId) {
        OmsOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<OmsOrder>()
                        .eq(OmsOrder::getId, orderId)
                        .eq(OmsOrder::getUserId, userId));
        if (order == null) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 2) throw new RuntimeException("订单状态不是待收货");
        OmsOrderDelivery delivery = deliveryMapper.selectOne(
                new LambdaQueryWrapper<OmsOrderDelivery>().eq(OmsOrderDelivery::getOrderId, orderId));
        if (delivery == null) throw new RuntimeException("发货信息不存在");
        delivery.setStatus(1);
        delivery.setReceivedAt(LocalDateTime.now());
        deliveryMapper.updateById(delivery);
        order.setStatus(3);
        order.setReviewed(0);
        orderMapper.updateById(order);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseAddressSnapshot(String json) {
        if (json == null || json.isBlank() || "{}".equals(json)) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("解析地址快照失败: {}", e.getMessage());
            return Map.of();
        }
    }
}
