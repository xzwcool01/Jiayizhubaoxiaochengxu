package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.UserLoginDTO;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UmsUserService userService;

    public UserController(UmsUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/wx-login")
    public R<UmsUser> wxLogin(@RequestBody UserLoginDTO dto) {
        return userService.wxLogin(dto);
    }

    @PostMapping("/signin")
    public R<UmsUser> signin(@RequestBody Map<String, String> body) {
        return userService.signin(body.get("openid"));
    }

    @GetMapping("/signin/status")
    public R<Boolean> signinStatus(@RequestParam String openid) {
        return userService.signinStatus(openid);
    }

    @GetMapping("/info")
    public R<UmsUser> info(@RequestParam String openid) {
        return userService.getUserInfo(openid);
    }

    @PostMapping("/avatar")
    public R<UmsUser> updateAvatar(@RequestBody Map<String, String> body) {
        return userService.updateAvatar(body.get("openid"), body.get("avatar"));
    }
}
