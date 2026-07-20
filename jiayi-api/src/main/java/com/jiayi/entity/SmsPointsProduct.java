package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sms_points_product")
public class SmsPointsProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ruleId;
    private Long productId;
}