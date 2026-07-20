package com.jiayi.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PointsRuleVO {
    private Long id;
    private Integer points;
    private BigDecimal amount;
    private Integer type;
    private boolean productsAvailable;
}