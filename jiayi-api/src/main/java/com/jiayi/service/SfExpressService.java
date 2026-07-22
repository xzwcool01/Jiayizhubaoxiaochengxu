package com.jiayi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiayi.config.SfExpressConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.util.*;

@Service
public class SfExpressService {

    private final SfExpressConfig config;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(SfExpressService.class);

    public SfExpressService(SfExpressConfig config) {
        this.config = config;
    }

    public boolean isEnabled() {
        return config.isEnabled();
    }

    private String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5签名失败", e);
        }
    }

    private String md5Base64(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("MD5 Base64签名失败", e);
        }
    }

    private String extractXmlTag(String xml, String tag) {
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int start = xml.indexOf(open);
        if (start == -1) return null;
        start += open.length();
        int end = xml.indexOf(close, start);
        if (end == -1) return null;
        return xml.substring(start, end).trim();
    }

    public Map<String, Object> createOrder(
            String orderId,
            String receiverName, String receiverPhone,
            String receiverProvince, String receiverCity, String receiverDistrict, String receiverAddress,
            String itemName, int itemCount, double itemWeight) throws Exception {

        if (!config.isEnabled()) throw new RuntimeException("顺丰未配置");

        String msgData = objectMapper.writeValueAsString(Map.of(
                "language", "zh-CN",
                "orderId", orderId,
                "expressTypeId", 2,
                "payMethod", 1,
                "payZone", "Y",
                "custId", config.getClientCode(),
                "contactInfoList", List.of(
                        Map.of(
                                "contactType", 1,
                                "contact", config.getSenderName(),
                                "mobile", config.getSenderPhone(),
                                "province", config.getSenderProvince(),
                                "city", config.getSenderCity(),
                                "county", config.getSenderDistrict(),
                                "address", config.getSenderAddress()
                        ),
                        Map.of(
                                "contactType", 2,
                                "contact", receiverName,
                                "mobile", receiverPhone,
                                "province", receiverProvince,
                                "city", receiverCity,
                                "county", receiverDistrict,
                                "address", receiverAddress
                        )
                ),
                "parcels", List.of(Map.of(
                        "parcelQuantity", 1,
                        "totalLength", 20,
                        "totalWidth", 15,
                        "totalHeight", 10,
                        "totalWeight", String.format("%.1f", itemWeight)
                )),
                "cargoDetails", List.of(Map.of(
                        "name", itemName,
                        "count", itemCount,
                        "unit", "件",
                        "weight", String.format("%.1f", itemWeight)
                ))
        ));

        return callSfApi("EXP_RECE_CREATE_ORDER", msgData);
    }

    public Map<String, Object> queryRoute(String trackingNo, String phoneLast4) throws Exception {
        if (!config.isEnabled()) throw new RuntimeException("顺丰未配置");

        String msgData = objectMapper.writeValueAsString(Map.of(
                "trackingType", 1,
                "trackingNumber", List.of(trackingNo),
                "checkPhoneNo", phoneLast4
        ));

        return callSfApi("EXP_RECE_SEARCH_ROUTES", msgData);
    }

    public Map<String, Object> previewWaybill(String waybillNo, String templateCode) throws Exception {
        if (!config.isEnabled()) throw new RuntimeException("顺丰未配置");

        Map<String, Object> body = new java.util.HashMap<>();
        body.put("waybillNo", waybillNo);
        body.put("templateCode", templateCode);

        String msgData = objectMapper.writeValueAsString(body);
        return callSfApi("COM_RECE_CLOUD_PRINT_PARSEDDATA", msgData);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callSfApi(String serviceCode, String msgData) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String raw = msgData + timestamp + config.getCheckWord();
        String b64Digest = md5Base64(raw);
        String msgDigest = b64Digest;

        log.info("SF sign debug: msgData={}, timestamp={}, checkWord={}, b64Digest={}",
                msgData, timestamp, config.getCheckWord(), b64Digest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("partnerID", config.getClientCode());
        body.add("requestID", UUID.randomUUID().toString().replace("-", ""));
        body.add("serviceCode", serviceCode);
        body.add("timestamp", timestamp);
        body.add("msgData", msgData);
        body.add("msgDigest", msgDigest);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        log.info("SF API POST: serviceCode={}, msgData={}", serviceCode, msgData);
        String resp = restTemplate.postForObject(config.getRequestUrl(), request, String.class);
        log.info("SF API raw response: {}", resp);

        if (resp == null) throw new RuntimeException("顺丰API无响应");

        String errorCode = null;
        if (resp.trim().startsWith("{")) {
            Map<String, Object> json = objectMapper.readValue(resp, Map.class);
            errorCode = (String) json.get("apiResultCode");
        } else {
            errorCode = extractXmlTag(resp, "apiResultCode");
        }

        // A1006 = 签名无效，尝试用 Hex 编码重试
        if ("A1006".equals(errorCode)) {
            String hexDigest = md5Hex(raw);
            log.info("SF sign retry with Hex: hexDigest={}", hexDigest);
            body.set("msgDigest", hexDigest);
            request = new HttpEntity<>(body, headers);
            resp = restTemplate.postForObject(config.getRequestUrl(), request, String.class);
            log.info("SF API retry response: {}", resp);
            if (resp == null) throw new RuntimeException("顺丰API无响应");
        }

        if (resp.trim().startsWith("{")) {
            Map<String, Object> json = objectMapper.readValue(resp, Map.class);
            String code = (String) json.get("apiResultCode");
            String err = (String) json.get("apiErrorMsg");
            Object data = json.get("apiResultData");
            log.info("SF JSON response: code={}, error={}, data={}", code, err, data);
            if (!"A1000".equals(code)) {
                throw new RuntimeException("顺丰API错误: " + (err != null ? err : code));
            }
            if (data instanceof String) {
                Map<String, Object> apiResult = objectMapper.readValue((String) data, Map.class);
                if (!Boolean.TRUE.equals(apiResult.get("success"))) {
                    throw new RuntimeException("顺丰下单失败: " + apiResult.getOrDefault("errorMsg", data));
                }
                Object resultData = apiResult.get("msgData");
                if (resultData instanceof String) return objectMapper.readValue((String) resultData, Map.class);
                if (resultData instanceof Map) return (Map<String, Object>) resultData;
            }
            return Map.of();
        }

        String apiResultData = extractXmlTag(resp, "apiResultData");
        String apiResultCode = extractXmlTag(resp, "apiResultCode");
        String apiErrorMsg = extractXmlTag(resp, "apiErrorMsg");

        log.info("SF XML response: code={}, error={}, data={}", apiResultCode, apiErrorMsg, apiResultData);

        if (!"A1000".equals(apiResultCode)) {
            throw new RuntimeException("顺丰API错误: " + (apiErrorMsg != null ? apiErrorMsg : apiResultCode));
        }

        if (apiResultData == null || apiResultData.isEmpty()) return Map.of();

        Map<String, Object> apiResult = objectMapper.readValue(apiResultData, Map.class);
        if (!Boolean.TRUE.equals(apiResult.get("success"))) {
            String errorMsg = (String) apiResult.get("errorMsg");
            throw new RuntimeException("顺丰下单失败: " + (errorMsg != null ? errorMsg : apiResultData));
        }

        Object resultData = apiResult.get("msgData");
        if (resultData instanceof String) {
            return objectMapper.readValue((String) resultData, Map.class);
        }
        if (resultData instanceof Map) {
            return (Map<String, Object>) resultData;
        }
        return Map.of();
    }
}
