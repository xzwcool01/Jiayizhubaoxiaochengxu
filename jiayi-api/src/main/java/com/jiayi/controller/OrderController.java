package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.CreateOrderDTO;
import com.jiayi.dto.OrderVO;
import com.jiayi.entity.OmsOrder;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.OrderService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final UmsUserService userService;

    public OrderController(OrderService orderService, UmsUserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    private UmsUser getUserOrThrow(String openid) {
        R<UmsUser> r = userService.getUserInfo(openid);
        if (r.getCode() != 200 || r.getData() == null) return null;
        return r.getData();
    }

    @PostMapping("/create")
    public R<OrderVO> create(@RequestBody CreateOrderDTO dto) {
        UmsUser user = getUserOrThrow(dto.getOpenid());
        if (user == null) return R.error("用户未找到");
        try {
            OmsOrder order = orderService.createOrder(dto, user.getId());
            OrderVO vo = orderService.getOrderDetail(order.getId(), user.getId());
            return R.ok(vo);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping("/detail")
    public R<OrderVO> detail(@RequestParam Long id, @RequestParam String openid) {
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.error("用户未找到");
        OrderVO vo = orderService.getOrderDetail(id, user.getId());
        return vo != null ? R.ok(vo) : R.error("订单不存在");
    }

    @GetMapping("/list")
    public R<List<OrderVO>> list(@RequestParam String openid, @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) Integer reviewed) {
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.ok(List.of());
        return R.ok(orderService.listByUser(user.getId(), status, reviewed));
    }

    @PostMapping("/pay")
    public R<OrderVO> pay(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("id").toString());
        String openid = (String) body.get("openid");
        orderService.payOrder(orderId);
        UmsUser user = getUserOrThrow(openid);
        OrderVO vo = orderService.getOrderDetail(orderId, user.getId());
        return R.ok(vo);
    }

    @PostMapping("/cancel")
    public R<Void> cancel(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("id").toString());
        String openid = (String) body.get("openid");
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.error("用户未找到");
        orderService.cancelOrder(orderId, user.getId());
        return R.ok(null);
    }

    @GetMapping("/unpaid-count")
    public R<Map<String, Long>> unpaidCount(@RequestParam String openid) {
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.ok(Map.of("count", 0L));
        long count = orderService.countUnpaid(user.getId());
        return R.ok(Map.of("count", count));
    }
}