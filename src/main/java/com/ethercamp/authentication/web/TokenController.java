package com.ethercamp.authentication.web;

import com.ethercamp.authentication.dto.AuthorizationDTO;
import com.ethercamp.authentication.service.TokenService;
import com.ethercamp.common.util.CodeConstant;
import com.ethercamp.common.util.R;
import com.ethercamp.common.util.StringUtils;
import com.ethercamp.common.web.BaseController;
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

}
