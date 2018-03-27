package com.blockshine.authentication.domain;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-03-19 18:22:22
 */
public class AddressDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Long id;
	//创建时间
	private Date created;
	//修改时间
	private Date updated;
	//状态 0:禁用，1:正常
	private Integer status;
	//应用id
	private Long appId;
	//链类型0-共有链 1-私有链
	private Integer type;
	//转出地址
	private String addressFrom;
	//转入地址
	private String addressTo;
	private String appKey;
	private String password;

	/**
	 * 设置：
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public Long getId() {
		return id;
	}
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
	 * 设置：应用id
	 */
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	/**
	 * 获取：应用id
	 */
	public Long getAppId() {
		return appId;
	}
	/**
	 * 设置：链类型0-共有链 1-私有链
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取：链类型0-共有链 1-私有链
	 */
	public Integer getType() {
		return type;
	}

	public String getAddressTo() {
		return addressTo;
	}

	public String getAddressFrom() {
		return addressFrom;
	}

	public void setAddressFrom(String addressFrom) {
		this.addressFrom = addressFrom;
	}

	public void setAddressTo(String addressTo) {
		this.addressTo = addressTo;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
