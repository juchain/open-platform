package com.blockshine.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

public class SendDTO implements Serializable {

	private static final long serialVersionUID = -2204767789795951061L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String from;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String to;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String amount;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
