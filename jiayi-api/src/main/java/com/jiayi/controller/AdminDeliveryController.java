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
            String phoneLast4 = phone.length() >= 4 ? phone.substring(phone.length() - 4) : phone;
            Map<String, Object> result = sfExpressService.queryRoute(trackingNo, phoneLast4);
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

            // 优先调顺丰云打印解析 API 获取完整面单数据
            try {
                Map<String, Object> sfData = sfExpressService.previewWaybill(
                        d.getTrackingNo(), "fm_150_standard_YAL1P7QG");
                return R.ok(sfData);
            } catch (Exception ignored) {
                // 顺丰API失败时回退本地数据
            }

            if (d.getWaybillData() != null && !d.getWaybillData().isBlank()) {
                Map<String, Object> data = objectMapper.readValue(d.getWaybillData(),
                        new TypeReference<Map<String, Object>>() {});
                return R.ok(data);
            }

            return R.error("暂无面单数据");
        } catch (Exception e) {
            return R.error("获取面单失败: " + e.getMessage());
        }
    }
}
