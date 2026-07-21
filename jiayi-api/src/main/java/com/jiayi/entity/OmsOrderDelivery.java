package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("oms_order_delivery")
public class OmsOrderDelivery {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String expressCompany;
    private String trackingNo;
    private String waybillData;
    private Integer status;
    private LocalDateTime shippedAt;
    private LocalDateTime receivedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
