package com.jiayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.common.R;
import com.jiayi.dto.ShipDTO;
import com.jiayi.entity.OmsOrderDelivery;
import com.jiayi.mapper.OmsOrderDeliveryMapper;
import com.jiayi.service.DeliveryService;
import com.jiayi.service.SfExpressService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/delivery")
public class AdminDeliveryController {

    private final DeliveryService deliveryService;
    private final SfExpressService sfExpressService;
    private final OmsOrderDeliveryMapper deliveryMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminDeliveryController(DeliveryService deliveryService, SfExpressService sfExpressService,
                                   OmsOrderDeliveryMapper deliveryMapper) {
        this.deliveryService = deliveryService;
        this.sfExpressService = sfExpressService;
        this.deliveryMapper = deliveryMapper;
    }

    @PostMapping("/ship")
    public R<?> ship(@RequestBody ShipDTO dto) {
        try {
            Map<String, Object> sfResult = deliveryService.ship(dto);
            if (sfResult != null && !sfResult.isEmpty()) {
                return R.ok(sfResult);
            }
            return R.ok(null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping("/track")
    public R<?> track(@RequestParam String trackingNo, @RequestParam(defaultValue = "") String phone) {
        try {
            Map<String, Object> result = sfExpressService.queryRoute(trackingNo, "");
            return R.ok(result);
        } catch (Exception e) {
            return R.error("查询物流失败: " + e.getMessage());
        }
    }

    @GetMapping("/preview-waybill")
    public R<?> previewWaybill(@RequestParam Long orderId) {
        try {
            OmsOrderDelivery d = deliveryMapper.selectOne(
                    new LambdaQueryWrapper<OmsOrderDelivery>()
                            .eq(OmsOrderDelivery::getOrderId, orderId));
            if (d == null || d.getTrackingNo() == null) return R.error("发货记录不存在");

            // 优先返回本地存储的面单数据(含寄收件人信息)
            if (d.getWaybillData() != null && !d.getWaybillData().isBlank()) {
                Map<String, Object> data = objectMapper.readValue(d.getWaybillData(),
                        new TypeReference<Map<String, Object>>() {});
                // 确保前端能取到运单号
                if (!data.containsKey("_waybillNo")) data.put("_waybillNo", d.getTrackingNo());
                return R.ok(data);
            }

            return R.error("暂无面单数据");
        } catch (Exception e) {
            return R.error("获取面单失败: " + e.getMessage());
        }
    }
}
