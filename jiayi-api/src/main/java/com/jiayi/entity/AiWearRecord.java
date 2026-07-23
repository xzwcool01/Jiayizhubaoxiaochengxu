package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_wear_record")
public class AiWearRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;
    private Long categoryId;
    private String userPhoto;
    private String resultUrl;
    private String style;
    private Integer showOnDiscovery;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
