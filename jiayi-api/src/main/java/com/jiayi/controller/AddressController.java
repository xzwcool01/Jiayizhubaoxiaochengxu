package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.UmsUser;
import com.jiayi.entity.UmsUserAddress;
import com.jiayi.service.AddressService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;
    private final UmsUserService userService;

    public AddressController(AddressService addressService, UmsUserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    private UmsUser getUserOrThrow(String openid) {
        R<UmsUser> r = userService.getUserInfo(openid);
        if (r.getCode() != 200 || r.getData() == null) return null;
        return r.getData();
    }

    @GetMapping("/list")
    public R<List<UmsUserAddress>> list(@RequestParam String openid) {
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.ok(List.of());
        return R.ok(addressService.listByUser(user.getId()));
    }

    @PostMapping("/add")
    public R<Void> add(@RequestBody Map<String, Object> body) {
        String openid = (String) body.get("openid");
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.error("用户未登录");
        UmsUserAddress addr = new UmsUserAddress();
        addr.setUserId(user.getId());
        addr.setName((String) body.get("name"));
        addr.setPhone((String) body.get("phone"));
        addr.setProvince((String) body.getOrDefault("province", ""));
        addr.setCity((String) body.getOrDefault("city", ""));
        addr.setDistrict((String) body.getOrDefault("district", ""));
        addr.setDetail((String) body.get("detail"));
        Object isDef = body.get("isDefault");
        addr.setIsDefault(isDef instanceof Boolean ? (Boolean) isDef : false);
        addressService.add(addr);
        return R.ok(null);
    }

    @PutMapping("/update")
    public R<Void> update(@RequestBody UmsUserAddress addr) {
        addressService.update(addr);
        return R.ok(null);
    }

    @PutMapping("/default")
    public R<Void> setDefault(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String openid = (String) body.get("openid");
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.error("用户未找到");
        addressService.setDefault(id, user.getId());
        return R.ok(null);
    }

    @DeleteMapping("/remove")
    public R<Void> remove(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String openid = (String) body.get("openid");
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.error("用户未找到");
        addressService.remove(id, user.getId());
        return R.ok(null);
    }
}