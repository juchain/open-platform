package com.ethercamp.authentication.service;

import com.ethercamp.authentication.dto.AuthorizationDTO;


/**
 * Token generate and Token Refresh
 *
 * @author maxiaodong
 */
public interface TokenService {

    public AuthorizationDTO generateToken(AuthorizationDTO dto);
    public AuthorizationDTO refreshToken(AuthorizationDTO dto);

}