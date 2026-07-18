package com.jiayi.dto;

import lombok.Data;

@Data
public class MemberQueryDTO {
    private int page = 1;
    private int size = 10;
    private String keyword;
}
