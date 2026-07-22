package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.UploadBase64DTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final String uploadDir;
    private final HttpServletRequest request;

    public FileUploadController(@Value("${app.upload-dir:./uploads}") String uploadDir, HttpServletRequest request) {
        File dir = new File(uploadDir);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("user.dir"), uploadDir);
        }
        this.uploadDir = dir.getAbsolutePath();
        log.info("文件上传根目录: {}", this.uploadDir);
        this.request = request;
    }

    private String fullUrl(String path) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        return scheme + "://" + host + ":" + port + path;
    }

    @PostMapping("/upload/image")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return R.error("文件为空");
        String filename = saveFile(file, "avatars");
        if (filename == null) return R.error("上传失败");
        String url = fullUrl("/uploads/avatars/" + filename);
        log.info("头像上传成功: {}", url);
        return R.ok(url);
    }

    @PostMapping("/upload/image-base64")
    public R<String> uploadImageBase64(@RequestBody UploadBase64DTO dto) {
        if (dto.getBase64() == null || dto.getBase64().isEmpty()) return R.error("数据为空");
        String ext = dto.getExt() != null && !dto.getExt().isEmpty() ? "." + dto.getExt() : ".jpg";
        String filename = UUID.randomUUID() + ext;
        File dir = new File(uploadDir, "avatars");
        dir.mkdirs();
        try {
            byte[] bytes = Base64.getDecoder().decode(dto.getBase64());
            Files.write(new File(dir, filename).toPath(), bytes);
            String url = fullUrl("/uploads/avatars/" + filename);
            log.info("Base64图片上传成功: {}", url);
            return R.ok(url);
        } catch (Exception e) {
            log.error("Base64图片上传失败: {}", e.getMessage());
            return R.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload/review-image")
    public R<String> uploadReviewImage(@RequestParam("file") MultipartFile file,
                                       @RequestParam("productId") Long productId) {
        if (file.isEmpty()) return R.error("文件为空");
        String subDir = "reviews" + File.separator + productId;
        String filename = saveFile(file, subDir);
        if (filename == null) return R.error("上传失败");
        String url = fullUrl("/uploads/reviews/" + productId + "/" + filename);
        log.info("评价图片上传成功: productId={}, url={}", productId, url);
        return R.ok(url);
    }

    @PostMapping("/admin/product/upload-image")
    public R<String> uploadProductImage(@RequestParam("file") MultipartFile file,
                                        @RequestParam("productType") Integer productType) {
        if (file.isEmpty()) return R.error("文件为空");
        String typeDir = productType != null ? productType.toString() : "0";
        String subDir = "products" + File.separator + typeDir + File.separator + "temp";
        String filename = saveFile(file, subDir);
        if (filename == null) return R.error("上传失败");
        String url = fullUrl("/uploads/products/" + typeDir + "/temp/" + filename);
        log.info("商品图片上传成功: type={}, url={}", typeDir, url);
        return R.ok(url);
    }

    private String saveFile(MultipartFile file, String subDir) {
        String ext = getExt(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        File dir = new File(uploadDir, subDir);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建目录失败: {}", dir.getAbsolutePath());
            return null;
        }
        try {
            file.transferTo(new File(dir, filename));
            log.debug("文件保存成功: dir={}, filename={}", dir.getAbsolutePath(), filename);
            return filename;
        } catch (Exception e) {
            log.error("文件保存失败: subDir={}, error={}", subDir, e.getMessage());
            return null;
        }
    }

    private String getExt(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
}
