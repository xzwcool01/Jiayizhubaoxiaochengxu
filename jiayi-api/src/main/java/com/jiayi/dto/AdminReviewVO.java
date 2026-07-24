package com.jiayi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminReviewVO {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long userId;
    private String nickname;
    private String productName;
    private Integer rating;
    private String content;
    private List<String> images;
    private Integer isAnonymous;
    private Integer isTop;
    private Integer showOnExpert;
    private Integer expertSortOrder;
    private String expertTag;
    private Integer expertLikes;
    private Integer isManual;
    private String expertNickname;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private LocalDateTime createTime;
}
