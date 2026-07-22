package com.jiayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.UmsUserMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/member")
public class AdminMemberController {

    private final UmsUserMapper userMapper;

    public AdminMemberController(UmsUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/list")
    public R<Page<UmsUser>> list(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<UmsUser> wrapper = new LambdaQueryWrapper<UmsUser>()
                .orderByDesc(UmsUser::getCreateTime);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(UmsUser::getNickname, keyword)
                    .or().like(UmsUser::getPhone, keyword)
                    .or().like(UmsUser::getMemberNo, keyword));
        }
        return R.ok(userMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @GetMapping("/{id}")
    public R<UmsUser> get(@PathVariable Long id) {
        UmsUser user = userMapper.selectById(id);
        if (user == null) return R.error("用户不存在");
        return R.ok(user);
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody UmsUser user) {
        user.setId(id);
        userMapper.updateById(user);
        return R.ok(null);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return R.ok(null);
    }

    @PostMapping
    public R<Void> create(@RequestBody UmsUser user) {
        userMapper.insert(user);
        return R.ok(null);
    }
}
