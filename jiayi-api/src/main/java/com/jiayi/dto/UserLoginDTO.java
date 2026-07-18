package com.jiayi.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String code;
    private String rawData;
    private String signature;
}
