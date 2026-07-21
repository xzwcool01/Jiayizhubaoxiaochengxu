package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.ShipDTO;
import com.jiayi.service.DeliveryService;
import com.jiayi.service.SfExpressService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/delivery")
public class AdminDeliveryController {

    private final DeliveryService deliveryService;
    private final SfExpressService sfExpressService;

    public AdminDeliveryController(DeliveryService deliveryService, SfExpressService sfExpressService) {
        this.deliveryService = deliveryService;
        this.sfExpressService = sfExpressService;
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
    public R<?> previewWaybill(@RequestParam String waybillNo) {
        try {
            Map<String, Object> result = sfExpressService.previewWaybill(waybillNo);
            return R.ok(result);
        } catch (Exception e) {
            return R.error("获取面单失败: " + e.getMessage());
        }
    }
}
