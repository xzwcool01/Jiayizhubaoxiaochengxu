package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.dto.MemberLevelVO;
import com.jiayi.dto.MemberOverviewVO;
import com.jiayi.dto.PointsLogVO;
import com.jiayi.entity.UmsLevel;
import com.jiayi.entity.UmsPointsLog;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.UmsLevelMapper;
import com.jiayi.mapper.UmsPointsLogMapper;
import com.jiayi.mapper.UmsUserMapper;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final UmsLevelMapper levelMapper;
    private final UmsPointsLogMapper pointsLogMapper;
    private final UmsUserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MemberService(UmsLevelMapper levelMapper, UmsPointsLogMapper pointsLogMapper,
                         UmsUserMapper userMapper) {
        this.levelMapper = levelMapper;
        this.pointsLogMapper = pointsLogMapper;
        this.userMapper = userMapper;
    }

    public List<MemberLevelVO> getAllLevels() {
        List<UmsLevel> list = levelMapper.selectList(
                new LambdaQueryWrapper<UmsLevel>()
                        .orderByAsc(UmsLevel::getLevelOrder));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    public MemberOverviewVO getOverview(Long userId) {
        UmsUser user = userMapper.selectById(userId);
        int points = user != null && user.getPoints() != null ? user.getPoints() : 0;

        List<UmsLevel> levels = levelMapper.selectList(
                new LambdaQueryWrapper<UmsLevel>()
                        .orderByAsc(UmsLevel::getLevelOrder));

        MemberLevelVO current = null;
        MemberLevelVO next = null;
        for (int i = 0; i < levels.size(); i++) {
            UmsLevel l = levels.get(i);
            if (points >= l.getMinPoints() && points <= l.getMaxPoints()) {
                current = toVO(l);
                if (i + 1 < levels.size()) next = toVO(levels.get(i + 1));
                break;
            }
        }
        if (current == null && !levels.isEmpty()) {
            current = toVO(levels.get(0));
            if (levels.size() > 1) next = toVO(levels.get(1));
        }

        int progress = 0;
        if (current != null && next != null) {
            int range = next.getMinPoints() - current.getMinPoints();
            if (range > 0) progress = Math.min(100, (points - current.getMinPoints()) * 100 / range);
        }

        MemberOverviewVO vo = new MemberOverviewVO();
        vo.setPoints(points);
        vo.setCurrentLevel(current);
        vo.setNextLevel(next);
        vo.setAllLevels(levels.stream().map(this::toVO).collect(Collectors.toList()));
        vo.setProgress(progress);
        return vo;
    }

    public Page<PointsLogVO> getPointsLog(Long userId, int page, int size) {
        Page<UmsPointsLog> p = pointsLogMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<UmsPointsLog>()
                        .eq(UmsPointsLog::getUserId, userId)
                        .orderByDesc(UmsPointsLog::getCreateTime));
        Page<PointsLogVO> result = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        result.setRecords(p.getRecords().stream().map(log -> {
            PointsLogVO vo = new PointsLogVO();
            vo.setId(log.getId());
            vo.setActionName(log.getActionName());
            vo.setPoints(log.getPoints());
            vo.setCreateTime(log.getCreateTime() != null ? log.getCreateTime().toString().replace("T", " ") : "");
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }

    public void addPointsLog(Long userId, String actionKey, String actionName, int points) {
        UmsPointsLog log = new UmsPointsLog();
        log.setUserId(userId);
        log.setActionKey(actionKey);
        log.setActionName(actionName);
        log.setPoints(points);
        pointsLogMapper.insert(log);
    }

    private MemberLevelVO toVO(UmsLevel l) {
        MemberLevelVO vo = new MemberLevelVO();
        vo.setId(l.getId().longValue());
        vo.setName(l.getName());
        vo.setMinPoints(l.getMinPoints());
        vo.setMaxPoints(l.getMaxPoints());
        vo.setColor(l.getColor());
        vo.setSort(l.getLevelOrder());
        if (l.getPerks() != null && !l.getPerks().isEmpty()) {
            try {
                vo.setPerks(objectMapper.readValue(l.getPerks(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                vo.setPerks(Collections.singletonList(l.getPerks()));
            }
        } else {
            vo.setPerks(Collections.emptyList());
        }
        return vo;
    }
}
