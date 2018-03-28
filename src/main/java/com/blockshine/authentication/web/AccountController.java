package com.blockshine.authentication.web;

import com.alibaba.fastjson.JSONObject;
import com.blockshine.authentication.domain.ApplicationDO;
import com.blockshine.authentication.service.ApplicationService;
import com.blockshine.authentication.service.BlockShineWebCallService;
import com.blockshine.common.util.PageUtils;
import com.blockshine.common.util.Query;
import com.blockshine.common.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-03-22 11:15:16
 */
 
@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private BlockShineWebCallService blockShineWebCallService;


	@ResponseBody
	@PostMapping("/init")
	public JSONObject list(@RequestBody Map<String, Object> params){
		JSONObject jsonObject = blockShineWebCallService.accountInit(params);

		return jsonObject;

	}





	
}
