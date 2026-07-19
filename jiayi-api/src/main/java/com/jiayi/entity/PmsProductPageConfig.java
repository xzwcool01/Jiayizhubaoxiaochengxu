package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pms_product_page_config")
public class PmsProductPageConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Integer aiEnabled;
    private Integer videoEnabled;
    private String videoCover;
    private String videoUrl;
    private Integer galleryEnabled;
    private String galleryImages;
    private Integer disclaimerEnabled;
    private String disclaimerText;
    private String disclaimerColor;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
