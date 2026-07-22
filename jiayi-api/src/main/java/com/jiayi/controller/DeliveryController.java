package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.DeliveryVO;
import com.jiayi.entity.OmsOrderDelivery;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.OmsOrderDeliveryMapper;
import com.jiayi.service.DeliveryService;
import com.jiayi.service.SfExpressService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final UmsUserService userService;
    private final SfExpressService sfExpressService;
    private final OmsOrderDeliveryMapper deliveryMapper;

    public DeliveryController(DeliveryService deliveryService, UmsUserService userService,
                              SfExpressService sfExpressService, OmsOrderDeliveryMapper deliveryMapper) {
        this.deliveryService = deliveryService;
        this.userService = userService;
        this.sfExpressService = sfExpressService;
        this.deliveryMapper = deliveryMapper;
    }

    @GetMapping("/info")
    public R<DeliveryVO> info(@RequestParam Long orderId) {
        DeliveryVO vo = deliveryService.getDelivery(orderId);
        return vo != null ? R.ok(vo) : R.error("暂无物流信息");
    }

    @GetMapping("/track")
    public R<?> track(@RequestParam Long orderId) {
        OmsOrderDelivery d = deliveryMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OmsOrderDelivery>()
                        .eq(OmsOrderDelivery::getOrderId, orderId));
        if (d == null || d.getTrackingNo() == null) return R.error("暂无物流信息");

        try {
            // 从 address_snapshot 中提取手机号末4位
            String phoneLast4 = "";
            Map<String, Object> result = sfExpressService.queryRoute(d.getTrackingNo(), phoneLast4);
            return R.ok(result);
        } catch (Exception e) {
            return R.error("查询物流失败");
        }
    }

    @PostMapping("/receive")
    public R<Void> receive(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("id").toString());
        String openid = (String) body.get("openid");
        UmsUser user = userService.getUserInfo(openid).getData();
        if (user == null) return R.error("用户未找到");
        try {
            deliveryService.receive(orderId, user.getId());
            return R.ok(null);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }
}
