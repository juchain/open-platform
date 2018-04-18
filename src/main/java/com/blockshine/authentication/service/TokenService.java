package com.blockshine.authentication.service;

import com.blockshine.authentication.dto.AuthorizationDTO;
import com.blockshine.authentication.dto.LoginDTO;


/**
 * Token generate and Token Refresh
 *
 * @author maxiaodong
 */
public interface TokenService {

    public AuthorizationDTO generateToken(AuthorizationDTO dto);
    public AuthorizationDTO refreshToken(AuthorizationDTO dto);
	public String getAppKey(String token);
	public LoginDTO generateLoginToken(LoginDTO dto);
	public boolean checkLogin(String token);

}
