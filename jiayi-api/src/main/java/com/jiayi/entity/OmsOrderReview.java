package com.jiayi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("oms_order_review")
public class OmsOrderReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String content;
    private String images;
    private Integer isAnonymous;
    private Integer isTop;
    private Integer showOnExpert;
    private Integer expertSortOrder;
    private String expertTag;
    private Integer expertLikes;
    private Integer isManual;
    private String expertNickname;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
