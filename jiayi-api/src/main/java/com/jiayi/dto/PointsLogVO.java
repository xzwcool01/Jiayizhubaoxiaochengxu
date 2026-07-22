package com.jiayi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsLogVO {
    private Long id;
    private String actionName;
    private Integer points;
    private String createTime;
}
