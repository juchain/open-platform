package com.ethercamp.harmony.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ethercamp.harmony.dto.AccountDTO;

@Mapper
public interface AccountDao {

	public void inserAccount(AccountDTO accountDTO) ;
	
	public int userNameExists(String username);

	public int updateAccount(AccountDTO accountDTO);

	public AccountDTO checkLogin(AccountDTO accountDTO);

	public AccountDTO accountInfo(AccountDTO accountDTO);

}
