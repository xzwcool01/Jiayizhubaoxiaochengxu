package com.jiayi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.entity.PmsProduct;
import com.jiayi.mapper.PmsProductMapper;
import com.jiayi.service.PmsProductService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class PmsProductServiceImpl extends ServiceImpl<PmsProductMapper, PmsProduct> implements PmsProductService {

    private static final String UPLOAD_BASE = "uploads" + File.separator + "products";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void moveTempImages(PmsProduct product) {
        Integer type = product.getProductType();
        Long id = product.getId();
        if (type == null || id == null) return;

        String typeDir = type.toString();
        String prodDir = id.toString();
        File targetDir = new File(UPLOAD_BASE + File.separator + typeDir + File.separator + prodDir);
        if (!targetDir.exists()) targetDir.mkdirs();

        product.setMainImage(moveSingle(product.getMainImage(), typeDir, prodDir));

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
                }
            } catch (Exception ignored) {}
        }
    }

    private String moveSingle(String url, String typeDir, String prodDir) {
        if (url == null || !url.contains("/temp/")) return null;
        try {
            String path = url.substring(url.indexOf("/uploads/"));
            File source = new File(path.replace("/uploads/", "uploads/").replace("/", File.separator));
            if (!source.exists()) return null;
            String filename = source.getName();
            File target = new File(UPLOAD_BASE + File.separator + typeDir + File.separator + prodDir + File.separator + filename);
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
            return url.substring(0, url.indexOf("/uploads/")) + "/uploads/products/" + typeDir + "/" + prodDir + "/" + filename;
        } catch (Exception e) {
            return null;
        }
    }
}
