package com.jiayi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ConfirmOrderVO {
    private List<CartItemVO> items;
    private BigDecimal totalAmount;
    private List<UserCouponVO> coupons;
    private Integer userPoints;
    private PointsRuleVO pointsRule;
}