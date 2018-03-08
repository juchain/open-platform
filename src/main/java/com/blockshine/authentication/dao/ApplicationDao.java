package com.blockshine.authentication.dao;

import com.blockshine.authentication.dto.ApplicationDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationDao {

	//根据 appId appSecret 获取授权
	public int getAuthentication(ApplicationDTO adto);

    String findAppSecret(String appKey);
}
