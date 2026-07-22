package com.jiayi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActionPointsVO {
    private Long id;
    private String actionKey;
    private String actionName;
    private Integer points;
    private Integer status;
    private LocalDateTime createTime;
}
