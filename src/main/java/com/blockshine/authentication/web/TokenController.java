package com.blockshine.authentication.web;

import com.alibaba.fastjson.JSON;
import com.blockshine.authentication.dto.AuthorizationDTO;
import com.blockshine.authentication.service.TokenService;
import com.blockshine.common.constant.CodeConstant;
import com.blockshine.common.util.R;
import com.blockshine.common.util.StringUtils;
import com.blockshine.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * token生成与刷新
 *
 * @author maxiaodong
 */
@RestController
@RequestMapping("/token")
public class TokenController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @RequestMapping("/apply")
    public R generateToken(@RequestBody AuthorizationDTO dto){
        if(StringUtils.isEmpty(dto.getAppKey()) || StringUtils.isEmpty(dto.getAppSecret())){
            return R.error(CodeConstant.PARAM_LOST, "appId or appSecret lost");
        }
        AuthorizationDTO tokenDTO = tokenService.generateToken(dto);

        R r = new R();
        r.put("tokenObject", tokenDTO);
        return r;
    }

    @RequestMapping("/refresh")
    public R refreshToken(@RequestBody AuthorizationDTO dto){

        if(StringUtils.isEmpty(dto.getRefreshToken())){
            return R.error(CodeConstant.PARAM_LOST, "refreshToken lost!");
        }

        AuthorizationDTO tokenDTO = tokenService.refreshToken(dto);

        R r = new R();
        r.put("tokenObject", tokenDTO);
        return r;
    }
    
    @RequestMapping("/check")
    public R checkToken(@RequestBody String token){
        if(StringUtils.isEmpty(token) && JSON.parseObject(token).get("token").toString() != null){
            return R.error(CodeConstant.PARAM_LOST, "token lost");
        }
        
        token = JSON.parseObject(token).get("token").toString();
        
    	String appKey = tokenService.getAppKey(token);
    	 if(StringUtils.isEmpty(appKey)){
    		 return R.error(CodeConstant.APPKEY_LOST, "token:"+token+" 对应的AppKey 不存在!");
		}
    	 
        R r = new R();
        r.put("appKey", appKey);
        return r;
    }

}
