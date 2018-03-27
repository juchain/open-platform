package com.blockshine.authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class AccountDTO {


	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String password;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String appKey;



	private Long appId;

	//链类型0-共有链 1-私有链
	private Integer type;


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}



	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}
}
