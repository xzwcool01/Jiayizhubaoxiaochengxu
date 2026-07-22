package com.jiayi.dto;

import lombok.Data;
import java.util.List;

@Data
public class MemberLevelVO {
    private Long id;
    private String name;
    private Integer minPoints;
    private Integer maxPoints;
    private List<String> perks;
    private String icon;
    private String color;
    private Integer sort;
}
