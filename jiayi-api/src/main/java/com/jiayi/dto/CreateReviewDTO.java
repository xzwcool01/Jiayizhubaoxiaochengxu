package com.jiayi.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateReviewDTO {
    private Long orderId;
    private Long productId;
    private Integer rating;
    private String content;
    private List<String> images;
    private Integer isAnonymous;
    private String openid;
}
