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
@ConditionalOnProperty(name = "app.ai.generator", havingValue = "bailian")
public class BailianGenerator implements AiImageGenerator {

    private static final Logger log = LoggerFactory.getLogger(BailianGenerator.class);
    private static final String API_PATH = "/api/v1/services/aigc/multimodal-generation/generation";

    private final RestTemplate restTemplate;
    private final AiConfig aiConfig;

    public BailianGenerator(RestTemplate restTemplate, AiConfig aiConfig) {
        this.restTemplate = restTemplate;
        this.aiConfig = aiConfig;
    }

    @Override
    public String generate(String prompt, String userPhotoPath, String productImagePath, String outputDir) {
        String apiKey = aiConfig.getBailian().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Bailian API Key 未配置，返回占位图");
            return fallbackPlaceholder();
        }

        String baseUrl = aiConfig.getBailian().getBaseUrl();
        String model = aiConfig.getBailian().getModel();

        String userImageB64 = imageToBase64(userPhotoPath);
        String productImageB64 = productImagePath != null ? imageToBase64(productImagePath) : null;

        List<Object> content = new ArrayList<>();
        if (userImageB64 != null) {
            content.add(Map.of("image", "data:image/png;base64," + userImageB64));
        }
        if (productImageB64 != null) {
            content.add(Map.of("image", "data:image/png;base64," + productImageB64));
        }
        content.add(Map.of("text", prompt));

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "user");
        message.put("content", content);

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("messages", List.of(message));

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("size", "1024*1024");
        parameters.put("n", 1);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("input", input);
        body.put("parameters", parameters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            log.info("Bailian API 请求: model={}, hasUserImage={}, hasProductImage={}",
                    model, userImageB64 != null, productImageB64 != null);

            String endpoint = baseUrl;
            if (!endpoint.endsWith("/")) endpoint += "/";
            if (!endpoint.contains(API_PATH)) {
                int idx = endpoint.indexOf("/compatible-mode");
                if (idx > 0) endpoint = endpoint.substring(0, idx);
                idx = endpoint.indexOf("/v1");
                if (idx > 0) endpoint = endpoint.substring(0, idx);
                if (!endpoint.endsWith("/")) endpoint += "/";
                endpoint = endpoint + API_PATH.substring(1);
            }

            ResponseEntity<Map> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.error("Bailian API 返回异常: status={}, body={}",
                        response.getStatusCode(), response.getBody());
                return fallbackPlaceholder();
            }

            Map<String, Object> respBody = response.getBody();

            if (respBody.containsKey("code")) {
                log.error("Bailian API 返回错误: code={}, message={}",
                        respBody.get("code"), respBody.get("message"));
                return fallbackPlaceholder();
            }

            log.info("Bailian API 响应: requestId={}",
                    respBody.get("request_id"));

            String imageUrl = extractResultUrl(respBody);
            if (imageUrl == null) {
                log.error("未能从响应中提取图片, 完整响应: {}", respBody);
                return fallbackPlaceholder();
            }

            log.info("Bailian API 返回图片: type={}, length={}",
                    imageUrl.startsWith("data:") ? "base64" : "url",
                    imageUrl.length());

            return downloadImage(imageUrl, outputDir);
        } catch (Exception e) {
            log.error("Bailian API 调用失败: {}", e.getMessage(), e);
            return fallbackPlaceholder();
        }
    }

    @SuppressWarnings("unchecked")
    private String extractResultUrl(Map<String, Object> respBody) {
        Map<String, Object> output = (Map<String, Object>) respBody.get("output");
        if (output == null) return null;

        List<Map<String, Object>> results = (List<Map<String, Object>>) output.get("results");
        if (results != null && !results.isEmpty()) {
            String url = (String) results.get(0).get("url");
            if (url != null && !url.isBlank()) return url;
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> msg = (Map<String, Object>) choice.get("message");
            if (msg != null) {
                Object content = msg.get("content");
                if (content instanceof List) {
                    for (Map<String, Object> item : (List<Map<String, Object>>) content) {
                        String image = (String) item.get("image");
                        if (image != null && !image.isBlank()) return image;
                    }
                } else if (content instanceof String) {
                    return (String) content;
                }
            }
        }

        String outputText = (String) output.get("output_text");
        if (outputText != null && !outputText.isBlank()) return outputText;

        return null;
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
            byte[] imageBytes;
            if (imageUrl.startsWith("data:")) {
                String base64Data = imageUrl.substring(imageUrl.indexOf(",") + 1);
                imageBytes = Base64.getDecoder().decode(base64Data);
            } else {
                imageBytes = restTemplate.getForObject(URI.create(imageUrl), byte[].class);
            }
            if (imageBytes == null || imageBytes.length == 0) {
                log.error("下载图片内容为空, url={}", imageUrl);
                return imageUrl;
            }
            Files.write(target.toPath(), imageBytes);
            log.info("图片已下载: {} ({} bytes)", target.getAbsolutePath(), imageBytes.length);
        } catch (Exception e) {
            log.error("下载生成图片失败: {}", e.getMessage());
            return imageUrl;
        }

        if (!target.exists()) {
            log.error("图片文件不存在(写入可能失败): {}", target.getAbsolutePath());
            return fallbackPlaceholder();
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
