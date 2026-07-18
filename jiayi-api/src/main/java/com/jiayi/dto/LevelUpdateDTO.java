package com.jiayi.dto;

import lombok.Data;

@Data
public class LevelUpdateDTO {
    private String name;
    private Integer minPoints;
    private Integer maxPoints;
    private Integer levelOrder;
}
