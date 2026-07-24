package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.entity.CmsGuideArticle;
import com.jiayi.mapper.CmsGuideArticleMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/guide-article")
public class AdminCmsGuideArticleController {

    private final CmsGuideArticleMapper articleMapper;
    private final String uploadDir;

    public AdminCmsGuideArticleController(CmsGuideArticleMapper articleMapper,
                                           @org.springframework.beans.factory.annotation.Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.articleMapper = articleMapper;
        File dir = new File(uploadDir);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("user.dir"), uploadDir);
        }
        this.uploadDir = dir.getAbsolutePath();
    }

    @GetMapping("/list")
    public R<List<CmsGuideArticle>> list() {
        List<CmsGuideArticle> list = articleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CmsGuideArticle>()
                        .orderByDesc(CmsGuideArticle::getSortOrder)
                        .orderByDesc(CmsGuideArticle::getCreateTime));
        return R.ok(list);
    }

    @PostMapping("/add")
    public R<String> add(@RequestParam(required = false) MultipartFile file,
                         @RequestParam String title,
                         @RequestParam(defaultValue = "") String summary,
                         @RequestParam(defaultValue = "0") Integer isHero,
                         @RequestParam(defaultValue = "") String author,
                         @RequestParam(required = false) String publishDate,
                         @RequestParam(defaultValue = "1") Integer status,
                         @RequestParam(defaultValue = "0") Integer sortOrder,
                         @RequestParam(defaultValue = "") String content,
                         @RequestParam(required = false) String coverImage,
                         @RequestParam(defaultValue = "0") Integer views) {
        CmsGuideArticle item = new CmsGuideArticle();
        item.setTitle(title);
        item.setSummary(summary);
        item.setIsHero(isHero);
        item.setAuthor(author);
        item.setPublishDate(publishDate != null && !publishDate.isBlank() ? LocalDate.parse(publishDate) : null);
        item.setStatus(status);
        item.setSortOrder(sortOrder);
        item.setContent(content);
        item.setViews(views);

        if (file != null && !file.isEmpty()) {
            String ext = getExt(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            File dir = new File(uploadDir, "guide-article");
            if (!dir.exists() && !dir.mkdirs()) return R.error("创建目录失败");
            try {
                file.transferTo(new File(dir, filename));
                item.setCoverImage("/uploads/guide-article/" + filename);
            } catch (IOException e) {
                return R.error("文件上传失败");
            }
        } else if (coverImage != null && !coverImage.isBlank()) {
            item.setCoverImage(coverImage);
        }

        articleMapper.insert(item);
        return R.ok("添加成功");
    }

    @PostMapping("/update")
    public R<String> update(@RequestBody CmsGuideArticle item) {
        CmsGuideArticle exist = articleMapper.selectById(item.getId());
        if (exist == null) return R.error("记录不存在");
        if (item.getTitle() != null) exist.setTitle(item.getTitle());
        if (item.getSummary() != null) exist.setSummary(item.getSummary());
        if (item.getCoverImage() != null && !item.getCoverImage().isBlank()) exist.setCoverImage(item.getCoverImage());
        if (item.getContent() != null) exist.setContent(item.getContent());
        if (item.getAuthor() != null) exist.setAuthor(item.getAuthor());
        if (item.getPublishDate() != null) exist.setPublishDate(item.getPublishDate());
        if (item.getIsHero() != null) exist.setIsHero(item.getIsHero());
        if (item.getStatus() != null) exist.setStatus(item.getStatus());
        if (item.getSortOrder() != null) exist.setSortOrder(item.getSortOrder());
        if (item.getViews() != null) exist.setViews(item.getViews());
        articleMapper.updateById(exist);
        return R.ok("更新成功");
    }

    @PostMapping("/upload-image")
    public R<String> uploadImage(@RequestParam MultipartFile file) {
        String ext = getExt(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        File dir = new File(uploadDir, "guide-article" + File.separator + "content");
        if (!dir.exists() && !dir.mkdirs()) return R.error("创建目录失败");
        try {
            file.transferTo(new File(dir, filename));
            return R.ok("/uploads/guide-article/content/" + filename);
        } catch (IOException e) {
            return R.error("文件上传失败");
        }
    }

    @PostMapping("/delete/{id}")
    public R<String> delete(@PathVariable Long id) {
        articleMapper.deleteById(id);
        return R.ok("删除成功");
    }

    private String getExt(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
}
