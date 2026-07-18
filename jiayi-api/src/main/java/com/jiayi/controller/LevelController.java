package com.jiayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiayi.common.R;
import com.jiayi.dto.LevelUpdateDTO;
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
    public R<UmsLevel> create(@RequestBody UmsLevel level) {
        levelService.save(level);
        return R.ok(level);
    }

    @PutMapping("/{id}")
    public R<UmsLevel> update(@PathVariable Integer id, @RequestBody LevelUpdateDTO dto) {
        UmsLevel level = levelService.getById(id);
        if (level == null) return R.error("等级不存在");
        if (dto.getName() != null) level.setName(dto.getName());
        if (dto.getMinPoints() != null) level.setMinPoints(dto.getMinPoints());
        if (dto.getMaxPoints() != null) level.setMaxPoints(dto.getMaxPoints());
        if (dto.getLevelOrder() != null) level.setLevelOrder(dto.getLevelOrder());
        levelService.updateById(level);
        return R.ok(level);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        if (!levelService.removeById(id)) return R.error("删除失败");
        return R.ok(null);
    }
}
