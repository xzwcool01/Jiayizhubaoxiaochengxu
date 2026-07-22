package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.MemberLevelVO;
import com.jiayi.dto.MemberOverviewVO;
import com.jiayi.dto.PointsLogVO;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.UmsUserMapper;
import com.jiayi.service.MemberService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final UmsUserMapper userMapper;

    public MemberController(MemberService memberService, UmsUserMapper userMapper) {
        this.memberService = memberService;
        this.userMapper = userMapper;
    }

    @GetMapping("/levels")
    public R<List<MemberLevelVO>> levels() {
        return R.ok(memberService.getAllLevels());
    }

    @GetMapping("/overview")
    public R<MemberOverviewVO> overview(@RequestParam String openid) {
        UmsUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsUser>()
                        .eq(UmsUser::getOpenid, openid));
        if (user == null) return R.error("用户不存在");
        return R.ok(memberService.getOverview(user.getId()));
    }

    @GetMapping("/points/log")
    public R<Page<PointsLogVO>> pointsLog(@RequestParam String openid,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        UmsUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsUser>()
                        .eq(UmsUser::getOpenid, openid));
        if (user == null) return R.error("用户不存在");
        return R.ok(memberService.getPointsLog(user.getId(), page, size));
    }
}
