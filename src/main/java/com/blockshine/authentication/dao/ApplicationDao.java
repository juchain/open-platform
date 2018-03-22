package com.blockshine.authentication.dao;

import com.blockshine.authentication.domain.ApplicationDO;
import com.blockshine.authentication.dto.ApplicationDTO;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApplicationDao {

	//根据 appId appSecret 获取授权
	public int getAuthentication(ApplicationDTO adto);

    String findAppSecret(String appKey);


	ApplicationDO get(Long appId);

	List<ApplicationDO> list(Map<String, Object> map);

	int count(Map<String, Object> map);

	int save(ApplicationDO application);

	int update(ApplicationDO application);

	int remove(Long app_id);

	int batchRemove(Long[] appIds);
}
