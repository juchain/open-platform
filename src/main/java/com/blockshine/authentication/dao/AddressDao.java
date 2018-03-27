package com.blockshine.authentication.dao;

import com.blockshine.authentication.domain.AddressDO;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-03-19 18:22:22
 */
@Mapper
public interface AddressDao {

	AddressDO get(Long id);
	
	List<AddressDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(AddressDO address);
	
	int update(AddressDO address);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);
}
