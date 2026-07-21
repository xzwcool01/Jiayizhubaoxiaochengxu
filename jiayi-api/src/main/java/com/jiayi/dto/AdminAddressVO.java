package com.jiayi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminAddressVO {
    private Long id;
    private Long userId;
    private String userName;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Boolean isDefault;
    private LocalDateTime createTime;
}
