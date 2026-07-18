package com.jiayi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.entity.PmsProduct;
import com.jiayi.mapper.PmsProductMapper;
import com.jiayi.service.PmsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class PmsProductServiceImpl extends ServiceImpl<PmsProductMapper, PmsProduct> implements PmsProductService {

    private static final Logger log = LoggerFactory.getLogger(PmsProductServiceImpl.class);

    private final String uploadDir;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PmsProductServiceImpl(@Value("${app.upload-dir:./uploads}") String uploadDir) {
        File dir = new File(uploadDir);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("user.dir"), uploadDir);
        }
        this.uploadDir = dir.getAbsolutePath();
        log.info("上传目录路径: {}", this.uploadDir);
    }

    @Override
    public void moveTempImages(PmsProduct product) {
        Integer type = product.getProductType();
        Long id = product.getId();
        if (type == null || id == null) {
            log.warn("moveTempImages 跳过: productType或id为空, type={}, id={}", type, id);
            return;
        }

        String typeDir = type.toString();
        String prodDir = id.toString();
        File targetDir = new File(uploadDir + File.separator + "products" + File.separator + typeDir + File.separator + prodDir);
        boolean created = targetDir.mkdirs();
        log.debug("创建商品图片目录: path={}, created={}", targetDir.getAbsolutePath(), created);

        String newMain = moveSingle(product.getMainImage(), typeDir, prodDir);
        if (newMain != null) {
            product.setMainImage(newMain);
            log.info("主图已移动到商品目录: {}", newMain);
        }

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            try {
                List<String> list = objectMapper.readValue(product.getImages(), new TypeReference<List<String>>() {});
                List<String> newList = new ArrayList<>();
                boolean changed = false;
                for (String img : list) {
                    String moved = moveSingle(img, typeDir, prodDir);
                    newList.add(moved != null ? moved : img);
                    if (moved != null) changed = true;
                }
                if (changed) {
                    product.setImages(objectMapper.writeValueAsString(newList));
                    log.info("商品图片已更新: id={}, 图片数={}", id, newList.size());
                }
            } catch (Exception e) {
                log.error("处理商品图片列表失败: id={}, error={}", id, e.getMessage());
            }
        }
    }

    private String moveSingle(String url, String typeDir, String prodDir) {
        if (url == null || !url.contains("/temp/")) return null;
        try {
            String relPath = url.substring(url.indexOf("/uploads/")).replace("/uploads/", "");
            File source = new File(uploadDir, relPath.replace("/", File.separator));
            if (!source.exists()) {
                log.warn("临时图片不存在, 跳过移动: {}", source.getAbsolutePath());
                return null;
            }
            String filename = source.getName();
            File target = new File(uploadDir + File.separator + "products" + File.separator + typeDir + File.separator + prodDir + File.separator + filename);
            if (target.exists()) {
                log.warn("目标文件已存在, 跳过: {}", target.getAbsolutePath());
                return null;
            }
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
            log.info("图片移动成功: {} -> {}", source.getAbsolutePath(), target.getAbsolutePath());
            return url.substring(0, url.indexOf("/uploads/")) + "/uploads/products/" + typeDir + "/" + prodDir + "/" + filename;
        } catch (Exception e) {
            log.error("图片移动失败: url={}, error={}", url, e.getMessage());
            return null;
        }
    }
}
