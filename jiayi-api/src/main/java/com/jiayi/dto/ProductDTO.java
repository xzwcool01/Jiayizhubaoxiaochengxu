package com.jiayi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDTO {
    private Long categoryId;
    private Integer productType;
    private String name;
    private String subtitle;
    private String mainImage;
    private List<String> images;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer pointsPrice;
    private Integer stock;
    private Integer flashStock;
    private LocalDateTime saleStart;
    private LocalDateTime saleEnd;
    private Integer memberLevel;
    private Integer isNew;
    private Integer isRecommend;
    private Integer sortOrder;
    private Integer status;
    private String extraAttrs;
}
