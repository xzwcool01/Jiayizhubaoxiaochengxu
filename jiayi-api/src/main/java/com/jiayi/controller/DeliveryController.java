package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.DeliveryVO;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.DeliveryService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final UmsUserService userService;

    public DeliveryController(DeliveryService deliveryService, UmsUserService userService) {
        this.deliveryService = deliveryService;
        this.userService = userService;
    }

    @GetMapping("/info")
    public R<DeliveryVO> info(@RequestParam Long orderId) {
        DeliveryVO vo = deliveryService.getDelivery(orderId);
        return vo != null ? R.ok(vo) : R.error("暂无物流信息");
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
