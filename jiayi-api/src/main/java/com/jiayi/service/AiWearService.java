package com.jiayi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiayi.entity.AiWearRecord;
import com.jiayi.entity.AiWearShowcase;
import com.jiayi.entity.PmsProduct;
import com.jiayi.entity.UmsLevel;
import com.jiayi.entity.UmsUser;
import com.jiayi.mapper.AiWearRecordMapper;
import com.jiayi.mapper.AiWearShowcaseMapper;
import com.jiayi.mapper.PmsProductMapper;
import com.jiayi.mapper.UmsLevelMapper;
import com.jiayi.mapper.UmsUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AiWearService {

    private static final Logger log = LoggerFactory.getLogger(AiWearService.class);
    private static final int DEFAULT_DAILY_LIMIT = 10;

    private final AiWearRecordMapper recordMapper;
    private final AiWearShowcaseMapper showcaseMapper;
    private final UmsUserMapper userMapper;
    private final UmsLevelMapper levelMapper;
    private final PmsProductMapper productMapper;
    private final AiWearPromptService promptService;
    private final AiImageGenerator imageGenerator;
    private final String uploadDir;

    public AiWearService(AiWearRecordMapper recordMapper, AiWearShowcaseMapper showcaseMapper,
                         UmsUserMapper userMapper,
                         UmsLevelMapper levelMapper, PmsProductMapper productMapper,
                         AiWearPromptService promptService,
                         AiImageGenerator imageGenerator,
                         @Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.recordMapper = recordMapper;
        this.showcaseMapper = showcaseMapper;
        this.userMapper = userMapper;
        this.levelMapper = levelMapper;
        this.productMapper = productMapper;
        this.promptService = promptService;
        this.imageGenerator = imageGenerator;
        File dir = new File(uploadDir);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("user.dir"), uploadDir);
        }
        this.uploadDir = dir.getAbsolutePath();
    }

    public int getDailyLimit(Long userId) {
        UmsUser user = userMapper.selectById(userId);
        if (user == null || user.getLevelId() == null) return DEFAULT_DAILY_LIMIT;
        UmsLevel level = levelMapper.selectById(user.getLevelId());
        if (level == null || level.getAiWearLimit() == null) return DEFAULT_DAILY_LIMIT;
        return level.getAiWearLimit();
    }

    public long getTodayCount(Long userId) {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return recordMapper.selectCount(
                new LambdaQueryWrapper<AiWearRecord>()
                        .eq(AiWearRecord::getUserId, userId)
                        .between(AiWearRecord::getCreateTime, start, end));
    }

    public int getRemainingQuota(Long userId) {
        return Math.max(0, getDailyLimit(userId) - (int) getTodayCount(userId));
    }

    public String savePhoto(MultipartFile file, Long userId) {
        String ext = getExt(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        File dir = new File(uploadDir, "ai-wear" + File.separator + "temp" + File.separator + userId);
        if (!dir.exists() && !dir.mkdirs()) {
            log.warn("创建目录失败: {}", dir.getAbsolutePath());
            return null;
        }
        try {
            file.transferTo(new File(dir, filename));
            return "/uploads/ai-wear/temp/" + userId + "/" + filename;
        } catch (Exception e) {
            log.error("AI试戴照片保存失败: {}", e.getMessage());
            return null;
        }
    }

    public AiWearRecord generate(Long userId, Long productId, Long categoryId,
                                  String userPhotoUrl, String style) {
        int remaining = getRemainingQuota(userId);
        if (remaining <= 0) {
            throw new RuntimeException("今日AI试戴次数已用完");
        }

        PmsProduct product = productMapper.selectById(productId);
        Long catId = categoryId != null ? categoryId : (product != null ? product.getCategoryId() : null);

        String prompt = promptService.buildPrompt(catId,
                product != null ? product.getName() : "",
                product != null ? product.getDescriptionText() : "");

        log.info("AI试戴提示词: {}", prompt);

        String userPhotoPath = new File(uploadDir, userPhotoUrl.replace("/uploads/", "")).getAbsolutePath();
        String productImagePath = null;
        if (product != null && product.getMainImage() != null && !product.getMainImage().isBlank()) {
            String mainImg = product.getMainImage();
            if (mainImg.startsWith("/uploads/")) {
                productImagePath = new File(uploadDir, mainImg.replace("/uploads/", "")).getAbsolutePath();
            } else {
                productImagePath = mainImg;
            }
        }
        String resultOutputDir = new File(uploadDir, "ai-wear" + File.separator + "temp" + File.separator + userId).getAbsolutePath();
        String resultUrl = imageGenerator.generate(prompt, userPhotoPath, productImagePath, resultOutputDir);

        AiWearRecord record = new AiWearRecord();
        record.setUserId(userId);
        record.setProductId(productId);
        record.setCategoryId(catId);
        record.setUserPhoto(userPhotoUrl);
        record.setResultUrl(resultUrl);
        record.setStyle(style);
        record.setShowOnDiscovery(0);
        recordMapper.insert(record);
        return record;
    }

    public String publishRecord(Long recordId) {
        AiWearRecord record = recordMapper.selectById(recordId);
        if (record == null) throw new RuntimeException("记录不存在");
        if (record.getShowOnDiscovery() == 1) return record.getResultUrl();

        String oldUrl = record.getResultUrl();
        String relativePath = oldUrl.replace("/uploads/", "");
        File source = new File(uploadDir, relativePath);
        if (!source.exists()) throw new RuntimeException("源文件不存在");

        String filename = source.getName();
        File destDir = new File(uploadDir, "ai-wear" + File.separator + "show" + File.separator + record.getUserId());
        if (!destDir.exists() && !destDir.mkdirs()) throw new RuntimeException("创建目录失败");

        File dest = new File(destDir, filename);
        try {
            Files.move(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("移动文件失败: {}", e.getMessage());
            throw new RuntimeException("文件发布失败");
        }

        String newUrl = "/uploads/ai-wear/show/" + record.getUserId() + "/" + filename;
        record.setResultUrl(newUrl);
        record.setShowOnDiscovery(1);
        recordMapper.updateById(record);

        // Also insert into showcase table
        UmsUser user = userMapper.selectById(record.getUserId());
        AiWearShowcase sc = new AiWearShowcase();
        sc.setImageUrl(newUrl);
        sc.setTitle("");
        sc.setTag("AI试戴");
        sc.setUserId(record.getUserId());
        sc.setNickname(user != null ? user.getNickname() : "");
        sc.setSortOrder(0);
        showcaseMapper.insert(sc);

        return newUrl;
    }

    public List<AiWearRecord> getRecords(Long userId, Long productId) {
        LambdaQueryWrapper<AiWearRecord> wrapper = new LambdaQueryWrapper<AiWearRecord>()
                .eq(AiWearRecord::getUserId, userId);
        if (productId != null) {
            wrapper.eq(AiWearRecord::getProductId, productId);
        }
        wrapper.orderByDesc(AiWearRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }

    public List<AiWearShowcase> getShowcase() {
        return showcaseMapper.selectList(
                new LambdaQueryWrapper<AiWearShowcase>()
                        .orderByDesc(AiWearShowcase::getSortOrder)
                        .orderByDesc(AiWearShowcase::getCreateTime));
    }

    private String getExt(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
}
