package com.jiayi.dto;

import lombok.Data;

@Data
public class MemberUpdateDTO {
    private String nickname;
    private String phone;
    private Integer gender;
    private Integer levelId;
    private Integer points;
}
