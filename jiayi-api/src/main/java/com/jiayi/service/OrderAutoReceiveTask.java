package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiayi.entity.OmsOrder;
import com.jiayi.entity.OmsOrderDelivery;
import com.jiayi.mapper.OmsOrderDeliveryMapper;
import com.jiayi.mapper.OmsOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderAutoReceiveTask {

    private final OmsOrderDeliveryMapper deliveryMapper;
    private final OmsOrderMapper orderMapper;
    private static final Logger log = LoggerFactory.getLogger(OrderAutoReceiveTask.class);

    public OrderAutoReceiveTask(OmsOrderDeliveryMapper deliveryMapper, OmsOrderMapper orderMapper) {
        this.deliveryMapper = deliveryMapper;
        this.orderMapper = orderMapper;
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void autoReceive() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(14);
        List<OmsOrderDelivery> list = deliveryMapper.selectList(
                new LambdaQueryWrapper<OmsOrderDelivery>()
                        .eq(OmsOrderDelivery::getStatus, 0)
                        .isNotNull(OmsOrderDelivery::getShippedAt)
                        .lt(OmsOrderDelivery::getShippedAt, deadline));

        for (OmsOrderDelivery d : list) {
            OmsOrder order = orderMapper.selectById(d.getOrderId());
            if (order == null || order.getStatus() != 2) continue;

            d.setStatus(1);
            d.setReceivedAt(LocalDateTime.now());
            deliveryMapper.updateById(d);

            order.setStatus(3);
            orderMapper.updateById(order);

            log.info("自动确认收货 orderId={}, orderSn={}", order.getId(), order.getOrderSn());
        }
    }
}