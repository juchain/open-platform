package com.blockshine.authentication.domain;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-03-22 11:15:16
 */
public class ApplicationDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private String appId;
	private String id;
	//创建时间
	private Date created;
	//修改时间
	private Date updated;
	//状态 0:禁用，1:正常
	private Integer status;
	//用户id
	private Long userId;
	//应用名称
	private String appName;
	//应用类型
	private String appType;
	//应用状态
	private String appStatus;
	//应用key
	private String appKey;
	//应用秘钥
	private String appSecret;
	//1-dev 2-test 3-pre 4-pro
	private Integer envType;
	//应用描述
	private String remark;
	//0-无 1-有  appstore 有无应用
	private Integer appStore;
	//0 -无 1-有
	private Integer android;
	//
	private String webSite;

	/**
	 * 设置：创建时间
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreated() {
		return created;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置：修改时间
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	/**
	 * 获取：修改时间
	 */
	public Date getUpdated() {
		return updated;
	}
	/**
	 * 设置：状态 0:禁用，1:正常
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：状态 0:禁用，1:正常
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置：用户id
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：用户id
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：应用名称
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	/**
	 * 获取：应用名称
	 */
	public String getAppName() {
		return appName;
	}
	/**
	 * 设置：应用类型
	 */
	public void setAppType(String appType) {
		this.appType = appType;
	}
	/**
	 * 获取：应用类型
	 */
	public String getAppType() {
		return appType;
	}
	/**
	 * 设置：应用状态
	 */
	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}
	/**
	 * 获取：应用状态
	 */
	public String getAppStatus() {
		return appStatus;
	}
	/**
	 * 设置：应用key
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	/**
	 * 获取：应用key
	 */
	public String getAppKey() {
		return appKey;
	}

	/**
	 * 设置：应用秘钥
	 */
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	/**
	 * 获取：应用秘钥
	 */
	public String getAppSecret() {
		return appSecret;
	}
	/**
	 * 设置：1-dev 2-test 3-pre 4-pro
	 */

	public Integer getEnvType() {
		return envType;
	}

	public void setEnvType(Integer envType) {
		this.envType = envType;
	}

	/**

	 * 设置：应用描述
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 获取：应用描述
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * 设置：0-无 1-有  appstore 有无应用
	 */
	public void setAppStore(Integer appStore) {
		this.appStore = appStore;
	}
	/**
	 * 获取：0-无 1-有  appstore 有无应用
	 */
	public Integer getAppStore() {
		return appStore;
	}
	/**
	 * 设置：0 -无 1-有
	 */
	public void setAndroid(Integer android) {
		this.android = android;
	}
	/**
	 * 获取：0 -无 1-有
	 */
	public Integer getAndroid() {
		return android;
	}
	/**
	 * 设置：
	 */
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	/**
	 * 获取：
	 */
	public String getWebSite() {
		return webSite;
	}
}
