package com.blockshine.authentication.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;

public class ApplicationDTO {

	// 应用Id
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer appId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String appKey;

	// 创建时间
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Date created;

	// 最后修改时间
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Date updated;

	// 是否可用 0不可用 1可用
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private int status;

	// 用户ID
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer userId;

	// 应用名称
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String appName;

	// 应用类型
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String appType;// true or false

	// 应用状态
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String appStatus;

	// 应用秘钥
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String appSecret;

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
}
