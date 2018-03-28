package com.blockshine.authentication.service;

import com.alibaba.fastjson.JSONObject;

import com.blockshine.authentication.domain.AddressDO;
import com.blockshine.authentication.domain.ApplicationDO;
import com.blockshine.authentication.dto.AccountDTO;
import com.blockshine.authentication.service.impl.AddressServiceImpl;
import com.blockshine.authentication.util.HttpClientUtils;
import com.blockshine.common.constant.CodeConstant;
import com.blockshine.common.util.Base64Utils;
import com.blockshine.common.util.MD5Utils;
import com.blockshine.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlockShineWebCallService {


    private static Logger logger =Logger.getLogger(BlockShineWebCallService.class);

    @Autowired
	AddressServiceImpl addressService;

	@Value("${bswurl}")
	private String bswurl;

	// 创建账户
    @Transactional
	public R bsw_newAddress(ApplicationDO applicationDO)  {
        Map map  =new HashMap();
        map.put("appKey",applicationDO.getAppKey());
        String password = "";
        try {
            password = MD5Utils.encrypt(applicationDO.getAppKey());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("密码生成失败",e);
        }
        map.put("password",password);


        String params = JSONObject.toJSONString(map);
        String url =bswurl+"account/init";
        logger.info("url:"+url+"======params:"+params);

        JSONObject jo = null;
        try {
            jo = HttpClientUtils.httpPostJsonStr(bswurl + "account/init",params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        R r =R.ok();

        if(jo!=null &"0".equals(jo.get("code"))){
            AddressDO addressDo = new AddressDO();
            addressDo.setAddressFrom((String)jo.get("from"));
            addressDo.setAddressTo((String)jo.get("to"));
            addressDo.setCreated(new Date());
            addressDo.setAppId(applicationDO.getAppId());
            addressDo.setAppKey(applicationDO.getAppKey());
            addressDo.setPassword(password);
            addressDo.setType(CodeConstant.CHAIN_TYPE.CHAIN_TYPE_PRIVATE);
            addressDo.setStatus(1);
            addressService.save(addressDo);
            r.put("code", 0);
            r.put("msg", "账户创建成功");
        }else{
            logger.info("账户创建失败");
            r.put("code", CodeConstant.ACCOUNT_CREATE);
            r.put("msg", "账户创建成功");
        }
		return r;
	}




    public JSONObject accountInit(Map<String, Object> params) {
        String paramsString = JSONObject.toJSONString(params);
        String url =bswurl+"account/init";
        logger.info("url:"+url+"======params:"+paramsString);
        JSONObject jo = null;
        try {
            jo = HttpClientUtils.httpPostJsonStr(bswurl + "account/init",paramsString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jo;

    }
}
