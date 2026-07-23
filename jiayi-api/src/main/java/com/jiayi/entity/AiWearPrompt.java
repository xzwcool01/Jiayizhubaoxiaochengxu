package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_wear_prompt")
public class AiWearPrompt {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String promptTemplate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
