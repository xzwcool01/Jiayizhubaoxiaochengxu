package com.jiayi.dto;

import lombok.Data;

@Data
public class ShipDTO {
    private Long orderId;
    private String expressCompany;
    private String trackingNo;
}
