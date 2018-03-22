package com.blockshine.authentication.service.impl;

import com.blockshine.authentication.dao.AddressDao;
import com.blockshine.authentication.dto.AddressDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-03-19 18:22:22
 */


@Service
public class AddressServiceImpl {

	@Autowired
	private AddressDao addressDao;


	public AddressDO get(Long id){
		return addressDao.get(id);
	}


	public List<AddressDO> list(Map<String, Object> map){
		return addressDao.list(map);
	}


	public int count(Map<String, Object> map){
		return addressDao.count(map);
	}


	public int save(AddressDO address){
		return addressDao.save(address);
	}


	public int update(AddressDO address){
		return addressDao.update(address);
	}


	public int remove(Long id){
		return addressDao.remove(id);
	}


	public int batchRemove(Long[] ids){
		return addressDao.batchRemove(ids);
	}
}
