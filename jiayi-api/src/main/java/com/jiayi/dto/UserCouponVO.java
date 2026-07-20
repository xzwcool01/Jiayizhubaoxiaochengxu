package com.jiayi.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserCouponVO {
    private Long id;
    private Long couponId;
    private String name;
    private Integer type;
    private BigDecimal value;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}