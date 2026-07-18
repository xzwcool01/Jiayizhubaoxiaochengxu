package com.jiayi.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String code;
    private String nickname;
    private String phone;
    private Integer gender;
}
