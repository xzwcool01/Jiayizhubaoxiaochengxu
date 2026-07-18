package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ums_member_level")
public class UmsMemberLevel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer minPoints;
    private Integer maxPoints;
    private BigDecimal discountRate;
    private String icon;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
