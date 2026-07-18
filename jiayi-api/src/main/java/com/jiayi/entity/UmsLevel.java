package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ums_level")
public class UmsLevel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer minPoints;
    private Integer maxPoints;
    private Integer levelOrder;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
