package com.blockshine.authentication.service;

import com.blockshine.authentication.dto.AuthorizationDTO;


/**
 * Token generate and Token Refresh
 *
 * @author maxiaodong
 */
public interface TokenService {

    public AuthorizationDTO generateToken(AuthorizationDTO dto);
    public AuthorizationDTO refreshToken(AuthorizationDTO dto);
	public String getAppKey(String token);

}
