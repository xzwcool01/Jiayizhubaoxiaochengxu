package com.jiayi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiayi.common.R;
import com.jiayi.dto.AdminReviewVO;
import com.jiayi.service.ReviewService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/review")
public class AdminReviewController {

    private final ReviewService reviewService;
    private final String uploadDir;

    public AdminReviewController(ReviewService reviewService,
                                  @org.springframework.beans.factory.annotation.Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.reviewService = reviewService;
        File dir = new File(uploadDir);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("user.dir"), uploadDir);
        }
        this.uploadDir = dir.getAbsolutePath();
    }

    @GetMapping("/list")
    public R<Page<AdminReviewVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer rating) {
        return R.ok(reviewService.adminPage(page, size, productName, nickname, rating));
    }

    @GetMapping("/{id}")
    public R<AdminReviewVO> get(@PathVariable Long id) {
        AdminReviewVO vo = reviewService.adminGetById(id);
        return vo != null ? R.ok(vo) : R.error("评价不存在");
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer rating = body.get("rating") != null ? ((Number) body.get("rating")).intValue() : null;
        String content = (String) body.get("content");
        Integer isAnonymous = body.get("isAnonymous") != null ? ((Number) body.get("isAnonymous")).intValue() : null;
        Integer isTop = body.get("isTop") != null ? ((Number) body.get("isTop")).intValue() : null;
        Integer showOnExpert = body.get("showOnExpert") != null ? ((Number) body.get("showOnExpert")).intValue() : null;
        Integer expertSortOrder = body.get("expertSortOrder") != null ? ((Number) body.get("expertSortOrder")).intValue() : null;
        String expertTag = (String) body.get("expertTag");
        Integer expertLikes = body.get("expertLikes") != null ? ((Number) body.get("expertLikes")).intValue() : null;
        String expertNickname = (String) body.get("expertNickname");
        Integer status = body.get("status") != null ? ((Number) body.get("status")).intValue() : null;
        reviewService.adminUpdate(id, rating, content, isAnonymous, isTop, showOnExpert, expertSortOrder, expertTag, expertLikes, expertNickname, status);
        return R.ok(null);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        reviewService.adminDelete(id);
        return R.ok(null);
    }

    @PostMapping("/expert/create")
    public R<Long> createExpert(@RequestParam(required = false) List<MultipartFile> files,
                                @RequestParam String content,
                                @RequestParam(defaultValue = "管理员") String nickname,
                                @RequestParam(defaultValue = "") String tag,
                                @RequestParam(defaultValue = "0") Integer likes,
                                @RequestParam(defaultValue = "0") Integer sortOrder) {
        List<String> imageUrls = new java.util.ArrayList<>();
        if (files != null && !files.isEmpty()) {
            File dir = new File(uploadDir, "expert-post");
            if (!dir.exists() && !dir.mkdirs()) return R.error("创建目录失败");
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String ext = getExt(file.getOriginalFilename());
                String filename = UUID.randomUUID() + ext;
                try {
                    file.transferTo(new File(dir, filename));
                    imageUrls.add("/uploads/expert-post/" + filename);
                } catch (IOException e) {
                    return R.error("文件上传失败: " + file.getOriginalFilename());
                }
            }
        }
        Long id = reviewService.createManual(content, nickname, tag, likes, sortOrder, imageUrls.isEmpty() ? null : imageUrls);
        return R.ok(id);
    }

    private String getExt(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
}
