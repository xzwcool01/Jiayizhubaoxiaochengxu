package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.UmsLevel;
import com.jiayi.service.UmsLevelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/level")
public class PublicLevelController {

    private final UmsLevelService levelService;

    public PublicLevelController(UmsLevelService levelService) {
        this.levelService = levelService;
    }

    @GetMapping("/list")
    public R<List<UmsLevel>> list() {
        List<UmsLevel> list = levelService.lambdaQuery()
                .orderByAsc(UmsLevel::getLevelOrder)
                .list();
        return R.ok(list);
    }
}
