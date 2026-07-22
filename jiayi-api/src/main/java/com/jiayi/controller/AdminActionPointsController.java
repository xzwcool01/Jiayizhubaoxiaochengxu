package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.ActionPointsUpdateDTO;
import com.jiayi.entity.UmsActionPoints;
import com.jiayi.service.ActionPointsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/action-points")
public class AdminActionPointsController {

    private final ActionPointsService actionPointsService;

    public AdminActionPointsController(ActionPointsService actionPointsService) {
        this.actionPointsService = actionPointsService;
    }

    @GetMapping("/list")
    public R<List<UmsActionPoints>> list() {
        return R.ok(actionPointsService.listAll());
    }

    @PostMapping("/create")
    public R<Void> create(@RequestBody UmsActionPoints rule) {
        actionPointsService.create(rule);
        return R.ok(null);
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody ActionPointsUpdateDTO dto) {
        actionPointsService.update(id, dto);
        return R.ok(null);
    }
}
