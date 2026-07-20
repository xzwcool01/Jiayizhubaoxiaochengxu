package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("oms_order")
public class OmsOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderSn;
    private Long userId;
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}