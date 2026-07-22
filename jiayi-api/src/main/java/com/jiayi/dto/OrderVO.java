package com.jiayi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderSn;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal couponAmount;
    private BigDecimal pointsAmount;
    private Integer pointsDeduct;
    private Integer paymentMethod;
    private Integer status;
    private String addressSnapshot;
    private String note;
    private LocalDateTime paidAt;
    private LocalDateTime createTime;
    private Boolean mockPay;
    private Integer reviewed;
    private List<OrderItemVO> items;
}

