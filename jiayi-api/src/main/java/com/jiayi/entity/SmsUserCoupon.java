package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sms_user_coupon")
public class SmsUserCoupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long couponId;
    private Boolean used;
    private Long orderId;
    private LocalDateTime usedTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}