package com.jiayi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminFavoriteVO {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private LocalDateTime createTime;
}
