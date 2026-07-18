package com.jiayi.dto;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private int page = 1;
    private int size = 20;
    private Integer productType;
    private Long categoryId;
    private String keyword;
    private Integer status;
}
