package com.jiayi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CartItemVO {
    private Long id;
    private Long productId;
    private String name;
    private String mainImage;
    private Long categoryId;
    private String specs;
    private Double price;
    private Integer quantity;
    private Boolean selected;
    private LocalDateTime createTime;
}