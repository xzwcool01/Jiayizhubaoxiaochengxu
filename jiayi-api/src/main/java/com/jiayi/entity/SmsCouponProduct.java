package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sms_coupon_product")
public class SmsCouponProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long couponId;
    private Long productId;
}