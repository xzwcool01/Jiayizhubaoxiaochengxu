package com.jiayi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminOrderVO {
    private Long id;
    private String orderSn;
    private Long userId;
    private String userName;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal couponAmount;
    private BigDecimal pointsAmount;
    private Integer pointsDeduct;
    private BigDecimal freightAmount;
    private Integer paymentMethod;
    private Integer status;
    private String addressSnapshot;
    private String note;
    private LocalDateTime paidAt;
    private Long couponId;
    private Integer reviewed;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
    private String trackingNo;
    private String expressCompany;
}
