package com.jiayi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiayi.entity.PmsProduct;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PmsProductMapper extends BaseMapper<PmsProduct> {
    @Delete("DELETE FROM pms_product WHERE id = #{id}")
    int hardDeleteById(Long id);
}
