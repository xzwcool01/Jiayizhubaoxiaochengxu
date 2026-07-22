package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.UmsLevel;
import com.jiayi.service.UmsLevelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/level")
public class LevelController {

    private final UmsLevelService levelService;

    public LevelController(UmsLevelService levelService) {
        this.levelService = levelService;
    }

    @GetMapping("/list")
    public R<List<UmsLevel>> list() {
        List<UmsLevel> list = levelService.lambdaQuery()
                .orderByAsc(UmsLevel::getLevelOrder)
                .list();
        return R.ok(list);
    }

    @GetMapping("/{id}")
    public R<UmsLevel> get(@PathVariable Integer id) {
        UmsLevel level = levelService.getById(id);
        if (level == null) return R.error("等级不存在");
        return R.ok(level);
    }

    @PostMapping
    public R<Void> create(@RequestBody UmsLevel level) {
        levelService.save(level);
        return R.ok(null);
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Integer id, @RequestBody UmsLevel level) {
        level.setId(id);
        levelService.updateById(level);
        return R.ok(null);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        levelService.removeById(id);
        return R.ok(null);
    }
}
