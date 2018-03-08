package com.ethercamp.harmony.web.controller;

import com.ethercamp.harmony.dto.AccountDTO;
import com.ethercamp.harmony.dto.SendDTO;
import com.ethercamp.harmony.service.AccountService;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j(topic = "harmony")
public class AccountController {


	@Autowired
	AccountService accountService;

	//注册接口
	@RequestMapping(value = "/account/register", method = RequestMethod.POST, consumes = "application/json")
	public AccountDTO register(@RequestBody AccountDTO accountDTO) {
		accountDTO = accountService.register(accountDTO);
		return accountDTO;
	}

	//登录接口
	@RequestMapping(value = "/account/login", method = RequestMethod.POST, consumes = "application/json")
	public AccountDTO login(@RequestBody AccountDTO accountDTO) {
		accountDTO = accountService.login(accountDTO);
		return accountDTO;
	}

	@RequestMapping(value = "/account/info", method = RequestMethod.POST, consumes = "application/json")
	public AccountDTO info(@RequestBody AccountDTO accountDTO) {
		accountDTO = accountService.loadAccountInfo(accountDTO);
		return accountDTO;
	}

	//转账接口
	@RequestMapping(value = "/account/send", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, String> sendTransaction(@RequestBody SendDTO sendDTO) {

		Map<String, String> result = new HashMap<>();
		String transHash =  "";
		try {
			transHash = accountService.sendCoin(sendDTO.getFrom(), sendDTO.getTo(), sendDTO.getAmount(),
					sendDTO.getPassword());
		} catch (Exception e) {
			log.error("转账失败",e);
			result.put("code", "9");
			result.put("message", "Failed ! Send error ");
			return result;
		}
		//根据返回值判断 是否是16进制的交易哈希 或者 是验证参数有误
		if ("".equals(transHash) || "0x".equals(transHash)||"check error".equals(transHash)) {
			result.put("code", "10");
			result.put("message", "Failed ! Send error");
			if ("check error".equals(transHash)) {
				result.put("message", "Failed ! send args error");
			}
			return result;
		}

		result.put("transHash", transHash);
		result.put("code", "0");
		result.put("message", "Success ! ");
		return result;
	}

}
