package com.blockshine.authentication.dto;

import com.blockshine.common.dto.BaseDTO;

/**
 * 授权用户登录前端后台
 * 
 * @author Jet
 */
public class LoginDTO extends BaseDTO {

	private static final long serialVersionUID = 4040501456449475396L;
	//userId
	private String userId;
	// 权限范围
	private String scope;
	// token类型
	private String tokenType;
	// 授权码失效时间
	private int expiryTime;
	// 刷新码，可用于刷新授权码，不能作为授权码使用
	private String token;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
