package com.blockshine.authentication.service;

import com.alibaba.fastjson.JSONObject;

import com.blockshine.authentication.domain.ApplicationDO;
import com.blockshine.authentication.dto.AccountDTO;
import com.blockshine.authentication.dto.AddressDO;
import com.blockshine.authentication.service.impl.AddressServiceImpl;
import com.blockshine.authentication.util.HttpClientUtils;
import com.blockshine.common.constant.CodeConstant;
import com.blockshine.common.util.Base64Utils;
import com.blockshine.common.util.MD5Utils;
import com.blockshine.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
@Slf4j
public class BlockShineWebCallService {

    @Autowired
	AddressServiceImpl addressService;

	@Value("${bswurl}")
	private String bswurl;

	// 创建账户
	public R bsw_newAddress(ApplicationDO applicationDO)  {
	    JSONObject jsonObject =new JSONObject();
        jsonObject.put("appKey",applicationDO.getAppKey());

        String password = "";

        try {
            password = Base64Utils.decode(MD5Utils.encrypt(applicationDO.getAppKey()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("密码生成失败",e);
        }

        jsonObject.put("password",password);

		JSONObject jo = HttpClientUtils.httpPost(bswurl + "account/init",jsonObject);

		R r =R.ok();

        if("0".equals(jo.get("code"))){
            AddressDO addressDo = new AddressDO();
            addressDo.setAddressFrom((String)jo.get("from"));
            addressDo.setAddressTo((String)jo.get("from"));
            addressDo.setCreated(new Date());
            addressDo.setAppId(applicationDO.getAppId());
           //addressDo.setType(spplicationDO.getType());
            addressService.save(addressDo);
            r.put("code", 0);
            r.put("msg", "账户创建成功");
        }else{
            log.info("账户创建失败");
            r.put("code", CodeConstant.ACCOUNT_CREATE);
            r.put("msg", "账户创建成功");
        }
		return r;
	}




}