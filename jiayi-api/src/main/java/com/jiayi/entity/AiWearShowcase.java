package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_wear_showcase")
public class AiWearShowcase {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String imageUrl;
    private String title;
    private String tag;
    private Long userId;
    private String nickname;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
