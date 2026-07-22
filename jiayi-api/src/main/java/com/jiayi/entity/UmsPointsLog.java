package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ums_points_log")
public class UmsPointsLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String actionKey;
    private String actionName;
    private Integer points;
    private LocalDateTime createTime;
}
