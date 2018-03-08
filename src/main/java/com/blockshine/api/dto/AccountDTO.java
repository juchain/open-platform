package com.blockshine.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

public class AccountDTO implements Serializable {

    private static final long serialVersionUID = 2252191552416550290L;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String address;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String isAdmin;// true or false

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;// 消息

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer code;//编码 0成功 非0 失败

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String balance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	@Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                ", message='" + message + '\'' +
                ", code=" + code +
                '}';
    }
}
