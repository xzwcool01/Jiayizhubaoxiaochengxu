package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.entity.UmsUserAddress;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.UmsUserAddressMapper;
import com.jiayi.mapper.UmsUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final UmsUserAddressMapper addressMapper;
    private final UmsUserMapper userMapper;

    public AddressService(UmsUserAddressMapper addressMapper, UmsUserMapper userMapper) {
        this.addressMapper = addressMapper;
        this.userMapper = userMapper;
    }

    public List<UmsUserAddress> listByUser(Long userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<UmsUserAddress>()
                .eq(UmsUserAddress::getUserId, userId)
                .orderByDesc(UmsUserAddress::getIsDefault)
                .orderByDesc(UmsUserAddress::getCreateTime));
    }

    @Transactional
    public void add(UmsUserAddress addr) {
        if (Boolean.TRUE.equals(addr.getIsDefault())) {
            clearDefault(addr.getUserId());
        }
        long count = addressMapper.selectCount(new LambdaQueryWrapper<UmsUserAddress>()
                .eq(UmsUserAddress::getUserId, addr.getUserId()));
        if (count == 0) addr.setIsDefault(true);
        addressMapper.insert(addr);
    }

    @Transactional
    public void update(UmsUserAddress addr) {
        if (Boolean.TRUE.equals(addr.getIsDefault())) {
            clearDefault(addr.getUserId());
        }
        addressMapper.updateById(addr);
    }

    @Transactional
    public void setDefault(Long id, Long userId) {
        clearDefault(userId);
        UmsUserAddress a = new UmsUserAddress();
        a.setId(id);
        a.setIsDefault(true);
        addressMapper.updateById(a);
    }

    private void clearDefault(Long userId) {
        addressMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UmsUserAddress>()
                .eq(UmsUserAddress::getUserId, userId)
                .set(UmsUserAddress::getIsDefault, false));
    }

    public void remove(Long id, Long userId) {
        addressMapper.delete(new LambdaQueryWrapper<UmsUserAddress>()
                .eq(UmsUserAddress::getId, id)
                .eq(UmsUserAddress::getUserId, userId));
    }

    public UmsUserAddress getById(Long id) {
        return addressMapper.selectById(id);
    }

    public Page<UmsUserAddress> pageWithUser(int page, int size, String userName) {
        LambdaQueryWrapper<UmsUserAddress> q = new LambdaQueryWrapper<UmsUserAddress>().orderByDesc(UmsUserAddress::getCreateTime);
        if (userName != null && !userName.isBlank()) {
            List<Long> userIds = userMapper.selectList(new LambdaQueryWrapper<UmsUser>()
                    .like(UmsUser::getNickname, userName)
                    .or().like(UmsUser::getPhone, userName))
                    .stream().map(UmsUser::getId).collect(Collectors.toList());
            if (userIds.isEmpty()) {
                Page<UmsUserAddress> empty = new Page<>(page, size, 0);
                empty.setRecords(List.of());
                return empty;
            }
            q.in(UmsUserAddress::getUserId, userIds);
        }
        return addressMapper.selectPage(new Page<>(page, size), q);
    }
}