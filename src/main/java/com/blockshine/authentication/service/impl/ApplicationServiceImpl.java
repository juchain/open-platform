package com.blockshine.authentication.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.blockshine.authentication.dao.ApplicationDao;
import com.blockshine.authentication.domain.ApplicationDO;
import com.blockshine.authentication.dto.ApplicationDTO;
import com.blockshine.authentication.service.ApplicationService;

import com.blockshine.authentication.service.BlockShineWebCallService;
import com.blockshine.common.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class ApplicationServiceImpl implements ApplicationService {
	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	BlockShineWebCallService blockShineWebCallService;
	
	@Override
	public ApplicationDO get(Long appId){
		return applicationDao.get(appId);
	}
	
	@Override
	public List<ApplicationDO> list(Map<String, Object> map){
		return applicationDao.list(map);
	}
	
	@Override
	public int count(Map<String, Object> map){
		return applicationDao.count(map);
	}
	
	@Override
	public int save(ApplicationDO application){

        return applicationDao.save(application);
	}
	
	@Override
	public int update(ApplicationDO application){
		return applicationDao.update(application);
	}
	
	@Override
	public int remove(Long appId){
		return applicationDao.remove(appId);
	}
	
	@Override
	public int batchRemove(Long[] appIds){
		return applicationDao.batchRemove(appIds);
	}

	@Override
	public int getAuthentication(ApplicationDTO applicationDTO) {
		return 0;
	}

	@Override
	public String findAppSecret(Long[] appIds) {
		return null;
	}

    @Override
    public R createApplication(ApplicationDO application) {
        R r = blockShineWebCallService.bsw_newAddress(application);

        if(!Integer.valueOf(0).equals(r.get("code"))){
            return r;
        }

        int save = applicationDao.save(application);
        if(save>0){
            r.put("msg","应用创建成功");

        }
        return r;

    }

}