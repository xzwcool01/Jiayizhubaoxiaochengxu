package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.PointsRuleVO;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.PointsService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointsController {

    private final PointsService pointsService;
    private final UmsUserService userService;

    public PointsController(PointsService pointsService, UmsUserService userService) {
        this.pointsService = pointsService;
        this.userService = userService;
    }

    @GetMapping("/info")
    public R<Map<String, Object>> info(@RequestParam String openid, @RequestParam(required = false) String productIds) {
        R<UmsUser> r = userService.getUserInfo(openid);
        if (r.getCode() != 200 || r.getData() == null) return R.ok(Map.of("points", 0, "rules", List.of()));
        List<Long> pids = productIds != null && !productIds.isBlank()
                ? java.util.Arrays.stream(productIds.split(",")).map(Long::valueOf).collect(java.util.stream.Collectors.toList())
                : List.of();
        List<PointsRuleVO> rules = pointsService.getRulesForProducts(pids);
        return R.ok(Map.of("points", r.getData().getPoints(), "rules", rules));
    }
}