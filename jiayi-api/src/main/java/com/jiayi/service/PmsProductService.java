package com.jiayi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiayi.entity.PmsProduct;

public interface PmsProductService extends IService<PmsProduct> {
    void moveTempImages(PmsProduct product);
}
