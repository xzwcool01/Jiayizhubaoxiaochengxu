package com.jiayi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.config.WechatProperties;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WechatService {

    private final WechatProperties properties;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
}
