package com.ethercamp.authentication.service.impl;

import com.ethercamp.authentication.dto.ApplicationDTO;
import com.ethercamp.authentication.dto.AuthorizationDTO;
import com.ethercamp.authentication.service.TokenService;
import com.ethercamp.authentication.util.AccessTokenUtil;
import com.ethercamp.common.exception.BusinessException;
import com.ethercamp.common.util.CodeConstant;
import com.ethercamp.common.util.JedisUtil;

import com.ethercamp.authentication.dao.ApplicationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {


	
	@Autowired
    ApplicationDao applicationDao;

    @Override
    public AuthorizationDTO generateToken(AuthorizationDTO dto) {
    	ApplicationDTO adto = new ApplicationDTO();

    	adto.setAppKey(dto.getAppKey());
    	adto.setAppSecret(dto.getAppSecret());

    	int flag = applicationDao.getAuthentication(adto);
    	boolean validatePass = flag > 0 ? true : false;

        BusinessException businessException = null;
        if(!validatePass){
            businessException =
                    new BusinessException("invalid AppKey or appSecret!", CodeConstant.PARAM_ERROR);
            throw businessException;
        }


        validToken(dto);

        String token = AccessTokenUtil.generateToken(dto.getAppKey(), dto.getAppSecret());
    	String refreshToken = AccessTokenUtil.generateToken(dto.getAppKey(), dto.getAppSecret());

    	dto.setToken(token);
        dto.setExpiryTime(720);
        dto.setRefreshToken(refreshToken);
        dto.setScope("all");
        dto.setTokenType("grant");

        JedisUtil.set(CodeConstant.TOKEN + dto.getAppKey(), token, 720);
        JedisUtil.set(CodeConstant.REFRESH_TOKEN + dto.getAppKey(), refreshToken);
        JedisUtil.set(token, dto.getAppKey());
        JedisUtil.set(refreshToken, dto.getAppKey());


        return dto;


    }

    /**
     * 校验token是否存在，防止恶意重置token
     * @param dto
     */
    private void validToken(AuthorizationDTO dto) {
        BusinessException businessException;
        if(JedisUtil.hasKey(CodeConstant.TOKEN + dto.getAppKey())){
            businessException =
                    new BusinessException("token exists!", CodeConstant.SERVICE_REFUSED);

            throw businessException;
        }
    }

    @Override
    public AuthorizationDTO refreshToken(AuthorizationDTO dto) {
        BusinessException businessException = null;

        if(!JedisUtil.hasKey(dto.getRefreshToken())){
            businessException =
                    new BusinessException("refreshToken not exist!", CodeConstant.SERVICE_REFUSED);

            throw businessException;
        }

        String appKey = JedisUtil.getByKey(dto.getRefreshToken());

        dto.setAppKey(appKey);
        validToken(dto);

        String appSecret = applicationDao.findAppSecret(appKey);

        String token = AccessTokenUtil.generateToken(appKey, appSecret);

        dto.setToken(token);
        dto.setExpiryTime(720);
        dto.setScope("all");
        dto.setTokenType("grant");

        JedisUtil.set(CodeConstant.TOKEN + dto.getAppKey(), token, 720);
        JedisUtil.set(token, appKey);

        return dto;
    }
}
