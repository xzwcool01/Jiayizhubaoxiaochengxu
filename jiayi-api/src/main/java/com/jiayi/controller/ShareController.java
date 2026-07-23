package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.UmsUserMapper;
import com.jiayi.service.ActionPointsService;
import com.jiayi.service.MemberService;
import com.jiayi.service.ShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    private static final Logger log = LoggerFactory.getLogger(ShareController.class);

    private final ShareService shareService;
    private final ActionPointsService actionPointsService;
    private final MemberService memberService;
    private final UmsUserMapper userMapper;

    public ShareController(ShareService shareService, ActionPointsService actionPointsService,
                           MemberService memberService, UmsUserMapper userMapper) {
        this.shareService = shareService;
        this.actionPointsService = actionPointsService;
        this.memberService = memberService;
        this.userMapper = userMapper;
    }

    @GetMapping("/poster/{productId}")
    public R<String> getPoster(@PathVariable Long productId) {
        try {
            String url = shareService.generatePoster(productId);
            return R.ok(url);
        } catch (Exception e) {
            log.error("海报生成失败 productId={}: {}", productId, e.getMessage());
            return R.error("海报生成失败: " + e.getMessage());
        }
    }

    @PostMapping("/reward")
    public R<Void> reward(@RequestBody Map<String, String> body) {
        String openid = body.get("openid");
        if (openid == null) return R.error("缺少openid");
        UmsUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsUser>()
                        .eq(UmsUser::getOpenid, openid));
        if (user == null) return R.error("用户不存在");
        int points = actionPointsService.getPoints("share");
        if (points <= 0) return R.ok(null);
        user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + points);
        userMapper.updateById(user);
        memberService.addPointsLog(user.getId(), "share", "分享商品", points);
        return R.ok(null);
    }
}
