package com.ethercamp.authentication.dto;

import com.ethercamp.common.dto.BaseDTO;

/**
 * 授权api调用业务实体
 * @author maxiaodong
 */
public class AuthorizationDTO extends BaseDTO {
    private static final long serialVersionUID = -566690272527509128L;

    private String  appKey;

    private String appSecret;
//    权限范围
    private String scope;
//    token类型
    private String tokenType;
//    授权码失效时间
    private int expiryTime;
//    刷新码，可用于刷新授权码，不能作为授权码使用
    private String refreshToken;
//token
    private String token;


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }


    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(int expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AuthorizationDTO{" +
                "appKey='" + appKey + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", scope='" + scope + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiryTime=" + expiryTime +
                ", refreshToken='" + refreshToken + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
