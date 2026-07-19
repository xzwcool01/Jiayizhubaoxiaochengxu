package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pms_product")
public class PmsProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    private Integer productType;
    private String name;
    private String subtitle;
    private String mainImage;
    private String images;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer pointsPrice;
    private Integer stock;
    private Integer sales;
    private Integer flashStock;
    private Integer flashSold;
    private LocalDateTime saleStart;
    private LocalDateTime saleEnd;
    private Integer memberLevel;
    private Integer isNew;
    private Integer isRecommend;
    private Integer sortOrder;
    private Integer weight;
    private Integer status;
    private String extraAttrs;
    private String videoUrl;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
