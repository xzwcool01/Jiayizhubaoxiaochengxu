package com.jiayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.dto.MemberAddDTO;
import com.jiayi.dto.MemberQueryDTO;
import com.jiayi.dto.MemberUpdateDTO;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/member")
public class MemberController {

    private final UmsUserService userService;

    public MemberController(UmsUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public R<IPage<UmsUser>> list(MemberQueryDTO dto) {
        LambdaQueryWrapper<UmsUser> q = new LambdaQueryWrapper<>();
        q.orderByDesc(UmsUser::getId);
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            q.and(w -> w.like(UmsUser::getNickname, dto.getKeyword())
                    .or().like(UmsUser::getPhone, dto.getKeyword())
                    .or().like(UmsUser::getMemberNo, dto.getKeyword()));
        }
        Page<UmsUser> page = userService.page(new Page<>(dto.getPage(), dto.getSize()), q);
        return R.ok(page);
    }

    @GetMapping("/{id}")
    public R<UmsUser> get(@PathVariable Long id) {
        UmsUser user = userService.getById(id);
        if (user == null) return R.error("用户不存在");
        return R.ok(user);
    }

    @PostMapping
    public R<UmsUser> create(@RequestBody MemberAddDTO dto) {
        UmsUser user = new UmsUser();
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : "新会员");
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender() != null ? dto.getGender() : 0);
        user.setLevelId(dto.getLevelId() != null ? dto.getLevelId() : 1);
        user.setPoints(dto.getPoints() != null ? dto.getPoints() : 0);
        user.setAvatar("");
        user.setMemberNo(userService.generateMemberNo());
        userService.save(user);
        return R.ok(user);
    }

    @PutMapping("/{id}")
    public R<UmsUser> update(@PathVariable Long id, @RequestBody MemberUpdateDTO dto) {
        UmsUser user = userService.getById(id);
        if (user == null) return R.error("用户不存在");
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getLevelId() != null) user.setLevelId(dto.getLevelId());
        if (dto.getPoints() != null) user.setPoints(dto.getPoints());
        userService.updateById(user);
        return R.ok(user);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (!userService.removeById(id)) return R.error("删除失败");
        return R.ok(null);
    }
}
