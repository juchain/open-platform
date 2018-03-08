package com.ethercamp.authentication.dao;

import com.ethercamp.authentication.dto.ApplicationDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationDao {

	//根据 appId appSecret 获取授权
	public int getAuthentication(ApplicationDTO adto);

    String findAppSecret(String appKey);
}
