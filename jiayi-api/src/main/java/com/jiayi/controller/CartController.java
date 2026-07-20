package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.CartItemVO;
import com.jiayi.entity.UmsUser;
import com.jiayi.service.CartService;
import com.jiayi.service.UmsUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UmsUserService userService;

    public CartController(CartService cartService, UmsUserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private UmsUser getUserOrThrow(String openid) {
        R<UmsUser> r = userService.getUserInfo(openid);
        if (r.getCode() != 200 || r.getData() == null) return null;
        return r.getData();
    }

    @PostMapping("/add")
    public R<Void> add(@RequestBody Map<String, Object> body) {
        String openid = (String) body.get("openid");
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity") ? Integer.parseInt(body.get("quantity").toString()) : 1;
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.error("用户未找到");
        cartService.add(user.getId(), productId, quantity);
        return R.ok(null);
    }

    @GetMapping("/list")
    public R<List<CartItemVO>> list(@RequestParam String openid) {
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.ok(List.of());
        return R.ok(cartService.listByUser(user.getId()));
    }

    @PutMapping("/quantity")
    public R<Void> updateQuantity(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        int quantity = Integer.parseInt(body.get("quantity").toString());
        cartService.updateQuantity(id, quantity);
        return R.ok(null);
    }

    @PutMapping("/selected")
    public R<Void> updateSelected(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        boolean selected = Boolean.parseBoolean(body.get("selected").toString());
        cartService.updateSelected(id, selected);
        return R.ok(null);
    }

    @PutMapping("/selectAll")
    public R<Void> selectAll(@RequestBody Map<String, Object> body) {
        String openid = (String) body.get("openid");
        boolean selected = Boolean.parseBoolean(body.get("selected").toString());
        UmsUser user = getUserOrThrow(openid);
        if (user == null) return R.error("用户未找到");
        cartService.toggleSelectAll(user.getId(), selected);
        return R.ok(null);
    }

    @DeleteMapping("/remove")
    public R<Void> remove(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        cartService.removeById(id);
        return R.ok(null);
    }

    @PostMapping("/removeBatch")
    public R<Void> removeBatch(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> ids = (List<String>) body.get("ids");
        List<Long> idList = ids.stream().map(Long::valueOf).collect(java.util.stream.Collectors.toList());
        cartService.removeBatch(idList);
        return R.ok(null);
    }
}