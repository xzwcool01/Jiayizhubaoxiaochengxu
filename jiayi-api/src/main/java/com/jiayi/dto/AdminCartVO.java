package com.jiayi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminCartVO {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Boolean selected;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}