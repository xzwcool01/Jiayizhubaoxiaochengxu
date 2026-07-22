package com.jiayi.dto;

import lombok.Data;
import java.util.List;

@Data
public class MemberOverviewVO {
    private Integer points;
    private MemberLevelVO currentLevel;
    private MemberLevelVO nextLevel;
    private List<MemberLevelVO> allLevels;
    private int progress;
}
