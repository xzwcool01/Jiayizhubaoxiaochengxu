package com.jiayi.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    private String openid;
    private Long addressId;
    private Long couponId;
    private Boolean usePoints;
    private String note;
    private List<Long> cartItemIds;
    private Long productId;
    private Integer quantity;
}