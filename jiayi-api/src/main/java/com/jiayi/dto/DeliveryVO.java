package com.jiayi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeliveryVO {
    private Long id;
    private Long orderId;
    private String expressCompany;
    private String trackingNo;
    private Integer status;
    private LocalDateTime shippedAt;
    private LocalDateTime receivedAt;
}
