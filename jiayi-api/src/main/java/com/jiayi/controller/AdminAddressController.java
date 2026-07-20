package com.jiayi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.entity.UmsUserAddress;
import com.jiayi.service.AddressService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/address")
public class AdminAddressController {

    private final AddressService addressService;

    public AdminAddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public R<Page<UmsUserAddress>> list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size,
                                        @RequestParam(required = false) String userName) {
        return R.ok(addressService.pageWithUser(page, size, userName));
    }
}