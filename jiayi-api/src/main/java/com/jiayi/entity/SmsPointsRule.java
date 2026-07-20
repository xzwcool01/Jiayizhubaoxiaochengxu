package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sms_points_rule")
public class SmsPointsRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer points;
    private BigDecimal amount;
    private Integer type;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}