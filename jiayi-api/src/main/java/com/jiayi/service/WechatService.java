package com.jiayi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.config.WechatProperties;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class WechatService {

    private final WechatProperties properties;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;
    private Instant tokenExpiry;

    public WechatService(WechatProperties properties) {
        this.properties = properties;
    }

    public String getOpenid(String code) throws Exception {
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + properties.getAppid()
                + "&secret=" + properties.getSecret()
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode json = objectMapper.readTree(response.body());
        if (json.has("errcode") && json.get("errcode").asInt() != 0) {
            throw new RuntimeException("jscode2session errcode=" + json.get("errcode") + " msg=" + json.get("errmsg"));
        }
        return json.get("openid").asText();
    }

    public String getAccessToken() throws Exception {
        if (accessToken != null && Instant.now().isBefore(tokenExpiry)) {
            return accessToken;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token"
                + "?grant_type=client_credential"
                + "&appid=" + properties.getAppid()
                + "&secret=" + properties.getSecret();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode json = objectMapper.readTree(response.body());
        if (json.has("errcode") && json.get("errcode").asInt() != 0) {
            throw new RuntimeException("getAccessToken errcode=" + json.get("errcode") + " msg=" + json.get("errmsg"));
        }
        accessToken = json.get("access_token").asText();
        int expiresIn = json.get("expires_in").asInt();
        tokenExpiry = Instant.now().plus(expiresIn - 60, ChronoUnit.SECONDS);
        return accessToken;
    }

    public byte[] getWXACodeUnlimit(String scene, String page) throws Exception {
        String token = getAccessToken();
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token;
        String jsonBody = objectMapper.writeValueAsString(Map.of(
                "scene", scene,
                "page", page,
                "width", 280,
                "auto_color", false,
                "is_hyaline", false
        ));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        String contentType = response.headers().firstValue("Content-Type").orElse("");
        if (contentType.contains("json")) {
            JsonNode json = objectMapper.readTree(response.body());
            int errcode = json.has("errcode") ? json.get("errcode").asInt() : -1;
            if (errcode != 0) {
                throw new RuntimeException("getWXACodeUnlimit errcode=" + errcode + " msg=" + json.get("errmsg"));
            }
        }
        return response.body();
    }
}
