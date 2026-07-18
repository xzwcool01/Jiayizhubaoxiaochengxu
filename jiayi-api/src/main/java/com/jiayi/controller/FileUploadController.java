package com.jiayi.controller;

import com.jiayi.common.R;
import com.jiayi.dto.UploadBase64DTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "avatars";
    private static final String PRODUCT_UPLOAD_DIR = "uploads" + File.separator + "products";

    private final HttpServletRequest request;

    public FileUploadController(HttpServletRequest request) {
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
        String ext = ".jpg";
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) ext = name.substring(name.lastIndexOf("."));
        String filename = UUID.randomUUID() + ext;
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();
        try {
            file.transferTo(new File(dir, filename));
            return R.ok(fullUrl("/uploads/avatars/" + filename));
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload/image-base64")
    public R<String> uploadImageBase64(@RequestBody UploadBase64DTO dto) {
        if (dto.getBase64() == null || dto.getBase64().isEmpty()) return R.error("数据为空");
        String ext = dto.getExt() != null && !dto.getExt().isEmpty() ? "." + dto.getExt() : ".jpg";
        String filename = UUID.randomUUID() + ext;
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();
        try {
            byte[] bytes = Base64.getDecoder().decode(dto.getBase64());
            Files.write(new File(dir, filename).toPath(), bytes);
            return R.ok(fullUrl("/uploads/avatars/" + filename));
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/admin/product/upload-image")
    public R<String> uploadProductImage(@RequestParam("file") MultipartFile file,
                                        @RequestParam("productType") Integer productType) {
        if (file.isEmpty()) return R.error("文件为空");
        String ext = ".jpg";
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) ext = name.substring(name.lastIndexOf("."));
        String filename = UUID.randomUUID() + ext;
        String typeDir = productType != null ? productType.toString() : "0";
        File dir = new File(PRODUCT_UPLOAD_DIR + File.separator + typeDir + File.separator + "temp");
        if (!dir.exists()) dir.mkdirs();
        try {
            file.transferTo(new File(dir, filename));
            return R.ok(fullUrl("/uploads/products/" + typeDir + "/temp/" + filename));
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }
}
