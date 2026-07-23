package com.jiayi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AiConfig {
    private String generator = "siliconflow";
    private SiliconFlow siliconflow = new SiliconFlow();
    private Bailian bailian = new Bailian();

    public String getGenerator() { return generator; }
    public void setGenerator(String generator) { this.generator = generator; }

    public SiliconFlow getSiliconflow() { return siliconflow; }
    public void setSiliconflow(SiliconFlow siliconflow) { this.siliconflow = siliconflow; }

    public Bailian getBailian() { return bailian; }
    public void setBailian(Bailian bailian) { this.bailian = bailian; }

    public static class SiliconFlow {
        private String apiKey = "";
        private String model = "black-forest-labs/FLUX.1-schnell";
        private String baseUrl = "https://api.siliconflow.cn/v1";

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }

    public static class Bailian {
        private String apiKey = "";
        private String model = "qwen-image-edit-plus";
        private String baseUrl = "https://dashscope.aliyuncs.com";

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }
}
