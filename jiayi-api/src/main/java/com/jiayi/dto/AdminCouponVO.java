package com.jiayi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminCouponVO {
    private Long id;
    private String name;
    private Integer type;
    private BigDecimal value;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalCount;
    private Integer perUserLimit;
    private Integer usedCount;
    private Integer issuedCount;
    private Integer status;
    private List<Long> productIds;
    private List<String> productNames;
}