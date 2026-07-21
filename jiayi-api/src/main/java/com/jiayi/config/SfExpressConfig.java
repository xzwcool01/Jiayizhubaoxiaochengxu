package com.jiayi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sf")
public class SfExpressConfig {

    private String clientCode;
    private String checkWord;
    private String sandboxUrl = "https://sfapi-sbox.sf-express.com/std/service";
    private String prodUrl = "https://sfapi.sf-express.com/std/service";
    private boolean sandbox = true;
    private String senderName;
    private String senderPhone;
    private String senderProvince;
    private String senderCity;
    private String senderDistrict;
    private String senderAddress;

    public String getRequestUrl() {
        return sandbox ? sandboxUrl : prodUrl;
    }

    public boolean isEnabled() {
        return clientCode != null && !clientCode.isBlank();
    }

    public String getClientCode() { return clientCode; }
    public void setClientCode(String clientCode) { this.clientCode = clientCode; }
    public String getCheckWord() { return checkWord; }
    public void setCheckWord(String checkWord) { this.checkWord = checkWord; }
    public String getSandboxUrl() { return sandboxUrl; }
    public void setSandboxUrl(String sandboxUrl) { this.sandboxUrl = sandboxUrl; }
    public String getProdUrl() { return prodUrl; }
    public void setProdUrl(String prodUrl) { this.prodUrl = prodUrl; }
    public boolean isSandbox() { return sandbox; }
    public void setSandbox(boolean sandbox) { this.sandbox = sandbox; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }
    public String getSenderProvince() { return senderProvince; }
    public void setSenderProvince(String senderProvince) { this.senderProvince = senderProvince; }
    public String getSenderCity() { return senderCity; }
    public void setSenderCity(String senderCity) { this.senderCity = senderCity; }
    public String getSenderDistrict() { return senderDistrict; }
    public void setSenderDistrict(String senderDistrict) { this.senderDistrict = senderDistrict; }
    public String getSenderAddress() { return senderAddress; }
    public void setSenderAddress(String senderAddress) { this.senderAddress = senderAddress; }
}
