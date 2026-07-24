package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.AiWearShowcase;
import com.jiayi.mapper.AiWearShowcaseMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/ai-wear-showcase")
public class AdminAiWearShowcaseController {

    private final AiWearShowcaseMapper showcaseMapper;
    private final String uploadDir;

    public AdminAiWearShowcaseController(AiWearShowcaseMapper showcaseMapper,
                                          @org.springframework.beans.factory.annotation.Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.showcaseMapper = showcaseMapper;
        File dir = new File(uploadDir);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("user.dir"), uploadDir);
        }
        this.uploadDir = dir.getAbsolutePath();
    }

    @GetMapping("/list")
    public R<List<AiWearShowcase>> list() {
        List<AiWearShowcase> list = showcaseMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWearShowcase>()
                        .orderByDesc(AiWearShowcase::getSortOrder)
                        .orderByDesc(AiWearShowcase::getCreateTime));
        return R.ok(list);
    }

    @PostMapping("/add")
    public R<String> add(@RequestParam(required = false) MultipartFile file,
                         @RequestParam String title,
                         @RequestParam(defaultValue = "AI生成") String tag,
                         @RequestParam(defaultValue = "0") Integer sortOrder,
                         @RequestParam(required = false) String imageUrl) {
        AiWearShowcase item = new AiWearShowcase();
        item.setTitle(title);
        item.setTag(tag);
        item.setSortOrder(sortOrder);

        if (file != null && !file.isEmpty()) {
            String ext = getExt(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            File dir = new File(uploadDir, "showcase" + File.separator + "admin");
            if (!dir.exists() && !dir.mkdirs()) return R.error("创建目录失败");
            try {
                file.transferTo(new File(dir, filename));
                item.setImageUrl("/uploads/showcase/admin/" + filename);
            } catch (IOException e) {
                return R.error("文件上传失败");
            }
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            item.setImageUrl(imageUrl);
        } else {
            return R.error("请上传图片或输入图片URL");
        }

        showcaseMapper.insert(item);
        return R.ok("添加成功");
    }

    @PostMapping("/update")
    public R<String> update(@RequestBody AiWearShowcase item) {
        AiWearShowcase exist = showcaseMapper.selectById(item.getId());
        if (exist == null) return R.error("记录不存在");
        if (item.getTitle() != null) exist.setTitle(item.getTitle());
        if (item.getTag() != null) exist.setTag(item.getTag());
        if (item.getSortOrder() != null) exist.setSortOrder(item.getSortOrder());
        if (item.getImageUrl() != null && !item.getImageUrl().isBlank()) exist.setImageUrl(item.getImageUrl());
        showcaseMapper.updateById(exist);
        return R.ok("更新成功");
    }

    @PostMapping("/delete/{id}")
    public R<String> delete(@PathVariable Long id) {
        showcaseMapper.deleteById(id);
        return R.ok("删除成功");
    }

    @PostMapping("/sort")
    public R<String> sort(@RequestBody List<Map<String, Object>> list) {
        for (Map<String, Object> item : list) {
            Long id = ((Number) item.get("id")).longValue();
            Integer sortOrder = ((Number) item.get("sortOrder")).intValue();
            AiWearShowcase exist = showcaseMapper.selectById(id);
            if (exist != null) {
                exist.setSortOrder(sortOrder);
                showcaseMapper.updateById(exist);
            }
        }
        return R.ok("排序更新成功");
    }

    private String getExt(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
}
