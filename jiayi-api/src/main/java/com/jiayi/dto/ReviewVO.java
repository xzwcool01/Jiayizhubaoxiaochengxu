package com.jiayi.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewVO {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String content;
    private List<String> images;
    private Integer isAnonymous;
    private Integer isTop;
    private Integer showOnExpert;
    private Integer expertSortOrder;
    private String expertTag;
    private Integer expertLikes;
    private String expertNickname;
    private Integer status;
    private LocalDateTime createTime;
    private String nickname;
    private String avatar;
    private String productName;
}
