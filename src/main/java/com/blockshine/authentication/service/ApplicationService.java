package com.blockshine.authentication.service;




import com.blockshine.authentication.domain.ApplicationDO;
import com.blockshine.authentication.dto.ApplicationDTO;
import com.blockshine.common.util.R;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-03-22 11:15:16
 */
public interface ApplicationService {
	
	ApplicationDO get(Long appId);
	
	List<ApplicationDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(ApplicationDO application);
	
	int update(ApplicationDO application);
	
	int remove(Long appId);
	
	int batchRemove(Long[] appIds);


	int getAuthentication(ApplicationDTO applicationDTO);

	String findAppSecret(Long[] appIds);

	R createApplication(ApplicationDO application);


}
