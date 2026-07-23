package com.jiayi.service.impl;

import com.jiayi.config.AiConfig;
import com.jiayi.service.AiImageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.*;
import java.util.Base64;

@Service
@ConditionalOnProperty(name = "app.ai.generator", havingValue = "siliconflow", matchIfMissing = true)
public class SiliconFlowGenerator implements AiImageGenerator {

    private static final Logger log = LoggerFactory.getLogger(SiliconFlowGenerator.class);

    private final RestTemplate restTemplate;
    private final AiConfig aiConfig;

    public SiliconFlowGenerator(RestTemplate restTemplate, AiConfig aiConfig) {
        this.restTemplate = restTemplate;
        this.aiConfig = aiConfig;
    }

    @Override
    public String generate(String prompt, String userPhotoPath, String productImagePath, String outputDir) {
        String apiKey = aiConfig.getSiliconflow().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("SiliconFlow API Key 未配置，返回占位图");
            return fallbackPlaceholder();
        }

        String baseUrl = aiConfig.getSiliconflow().getBaseUrl();
        String model = aiConfig.getSiliconflow().getModel();

        String userImageBase64 = imageToBase64(userPhotoPath);
        String productImageB64 = productImagePath != null ? imageToBase64(productImagePath) : null;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("prompt", prompt);
        body.put("n", 1);
        body.put("size", "1024x1024");

        if (userImageBase64 != null) {
            body.put("image", "data:image/jpeg;base64," + userImageBase64);
            body.put("strength", 0.3);
        }

        if (productImageB64 != null) {
            body.put("ref_image", "data:image/jpeg;base64," + productImageB64);
            body.put("ref_strength", 0.65);
        }

        body.put("negative_prompt", "额外多出首饰，多个戒指，多余饰品，水珠，液体特效，画面美化，油画质感，手绘，二次元，滤镜，扭曲饰品结构，饰品变形，漂浮饰品，边缘锯齿，改动原图背景，改变原图肤色，改变原图手部形态，新增无关物体，重影，模糊，光影冲突");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            log.info("SiliconFlow API 请求: model={}, prompt={}, hasUserImage={}, hasProductImage={}",
                    model, prompt, userImageBase64 != null, productImageB64 != null);
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/images/generations",
                    HttpMethod.POST,
                    request,
                    Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.error("SiliconFlow API 返回异常: {}", response.getStatusCode());
                return fallbackPlaceholder();
            }

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.getBody().get("data");
            if (dataList == null || dataList.isEmpty()) {
                log.error("SiliconFlow API 返回空数据");
                return fallbackPlaceholder();
            }

            String imageUrl = (String) dataList.get(0).get("url");
            if (imageUrl == null || imageUrl.isBlank()) {
                return fallbackPlaceholder();
            }

            return downloadImage(imageUrl, outputDir);
        } catch (Exception e) {
            log.error("SiliconFlow API 调用失败: {}", e.getMessage());
            return fallbackPlaceholder();
        }
    }

    private String imageToBase64(String path) {
        if (path == null || path.isBlank()) return null;
        try {
            byte[] bytes;
            if (path.startsWith("http://") || path.startsWith("https://")) {
                bytes = restTemplate.getForObject(URI.create(path), byte[].class);
                if (bytes == null) return null;
            } else {
                File file = new File(path);
                if (!file.exists()) {
                    log.warn("图片文件不存在: {}", path);
                    return null;
                }
                bytes = Files.readAllBytes(file.toPath());
            }
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.warn("读取图片失败: {}", e.getMessage());
            return null;
        }
    }

    private String downloadImage(String imageUrl, String outputDir) {
        File dir = new File(outputDir);
        if (!dir.exists() && !dir.mkdirs()) {
            log.warn("创建目录失败: {}", outputDir);
            return imageUrl;
        }

        String filename = "ai_result_" + System.currentTimeMillis() + ".jpg";
        File target = new File(dir, filename);

        try {
            byte[] imageBytes = restTemplate.getForObject(URI.create(imageUrl), byte[].class);
            if (imageBytes == null || imageBytes.length == 0) {
                log.error("下载图片内容为空");
                return imageUrl;
            }
            Files.write(target.toPath(), imageBytes);
            log.info("图片已下载: {}", target.getAbsolutePath());
        } catch (Exception e) {
            log.error("下载生成图片失败: {}", e.getMessage());
            return imageUrl;
        }

        String outputPath = outputDir.replace("\\", "/");
        int idx = outputPath.indexOf("/uploads/");
        if (idx >= 0) {
            return outputPath.substring(idx) + "/" + filename;
        }
        return "/uploads/ai-wear/result/" + filename;
    }

    private String fallbackPlaceholder() {
        return "https://via.placeholder.com/600x800/775836/ffffff?text=AI+Wear+Result+" + System.currentTimeMillis();
    }
}
