package com.jiayi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.common.R;
import com.jiayi.dto.UserLoginDTO;
import com.jiayi.dto.UserRegisterDTO;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.UmsUserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UmsUserService extends ServiceImpl<UmsUserMapper, UmsUser> {

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WechatService wechatService;
    private final ActionPointsService actionPointsService;
    private final MemberService memberService;

    public UmsUserService(WechatService wechatService, ActionPointsService actionPointsService,
                          MemberService memberService) {
        this.wechatService = wechatService;
        this.actionPointsService = actionPointsService;
        this.memberService = memberService;
    }

    public R<UmsUser> wxLogin(UserLoginDTO dto) {
        String openid;
        try {
            openid = wechatService.getOpenid(dto.getCode());
        } catch (Exception e) {
            return R.error("微信登录失败: " + e.getMessage());
        }

        UmsUser user = lambdaQuery().eq(UmsUser::getOpenid, openid).one();
        if (user != null) {
            return R.ok(user);
        }

        user = new UmsUser();
        user.setOpenid(openid);
        user.setNickname("微信用户");
        user.setAvatar("");
        int registerPoints = actionPointsService.getPoints("register");
        user.setPoints(registerPoints);
        user.setLevelId(1);
        user.setMemberNo(generateMemberNo());
        if (dto.getRawData() != null && !dto.getRawData().isEmpty()) {
            try {
                JsonNode json = objectMapper.readTree(dto.getRawData());
                if (json.has("nickName")) user.setNickname(json.get("nickName").asText());
                if (json.has("avatarUrl")) {
                    String avatar = json.get("avatarUrl").asText();
                    if (avatar != null && !avatar.startsWith("http://tmp")) {
                        user.setAvatar(avatar);
                    }
                }
            } catch (Exception ignored) {}
        }
        save(user);
        if (registerPoints > 0) {
            memberService.addPointsLog(user.getId(), "register", "注册赠送", registerPoints);
        }
        return R.ok("new", user);
    }

    public R<UmsUser> registerMember(UserRegisterDTO dto) {
        UmsUser user = lambdaQuery().eq(UmsUser::getOpenid, dto.getCode()).one();
        if (user == null) return R.error("用户不存在");
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setLevelId(2);
        if (user.getMemberNo() == null || user.getMemberNo().isEmpty()) {
            user.setMemberNo(generateMemberNo());
        }
        updateById(user);
        return R.ok(user);
    }

    public R<UmsUser> signin(String openid) {
        UmsUser user = lambdaQuery().eq(UmsUser::getOpenid, openid).one();
        if (user == null) return R.error("用户不存在");
        if (user.getLastSigninTime() != null
                && user.getLastSigninTime().toLocalDate().equals(LocalDate.now())) {
            return R.fail(400, "今日已签到");
        }
        int signinPoints = actionPointsService.getPoints("sign_in");
        user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + signinPoints);
        user.setLastSigninTime(LocalDateTime.now());
        updateById(user);
        memberService.addPointsLog(user.getId(), "sign_in", "每日签到", signinPoints);
        return R.ok(user);
    }

    public R<UmsUser> updateAvatar(String openid, String avatar) {
        UmsUser user = lambdaQuery().eq(UmsUser::getOpenid, openid).one();
        if (user == null) return R.error("用户不存在");
        user.setAvatar(avatar);
        updateById(user);
        return R.ok(user);
    }

    public R<UmsUser> getUserInfo(String openid) {
        UmsUser user = lambdaQuery().eq(UmsUser::getOpenid, openid).one();
        if (user == null) return R.error("用户不存在");
        return R.ok(user);
    }

    public R<Boolean> signinStatus(String openid) {
        UmsUser user = lambdaQuery().eq(UmsUser::getOpenid, openid).one();
        if (user == null) return R.ok(false);
        boolean signed = user.getLastSigninTime() != null
                && user.getLastSigninTime().toLocalDate().equals(LocalDate.now());
        return R.ok(signed);
    }

    public String generateMemberNo() {
        String no;
        int attempts = 0;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                sb.append(random.nextInt(10));
            }
            no = sb.toString();
            attempts++;
        } while (lambdaQuery().eq(UmsUser::getMemberNo, no).count() > 0 && attempts < 100);
        return no;
    }
}
