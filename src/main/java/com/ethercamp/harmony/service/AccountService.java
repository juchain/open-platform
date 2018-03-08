package com.ethercamp.harmony.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import com.ethercamp.harmony.dao.AccountDao;
import com.ethercamp.harmony.dto.AccountDTO;
import com.ethercamp.harmony.jsonrpc.TypeConverter;
import com.ethercamp.harmony.jsonrpc.JsonRpc.CallArguments;
import com.ethercamp.harmony.jsonrpc.EthJsonRpcImpl;


@Service
@Slf4j(topic = "harmony")
public class AccountService {

	@Autowired
	EthJsonRpcImpl ethJsonRpc;

	@Autowired
	WalletService walletService;

	@Autowired
	AccountDao accountDao;

	// 注册账户
	@Transactional
	public AccountDTO register(AccountDTO accountDTO) {
		log.info("do register"+JSON.toJSONString(accountDTO));
		
		//校验用户名密码
		boolean check = checkUsernameAndPassword(accountDTO);
		if (!check) {
			accountDTO = new AccountDTO();
			accountDTO.setMessage("Failed ! No Username and Password");
			accountDTO.setCode(10);
			return accountDTO;
		}
		
		// 判断是否存在
		int userNameExists = accountDao.userNameExists(accountDTO.getUsername());
		if (userNameExists > 0) {
			log.info("账户已经被使用");
			// 账户已经被使用
			accountDTO = new AccountDTO();
			accountDTO.setMessage("Failed ! Account already exists");
			accountDTO.setCode(1);
			return accountDTO;
		}

		// 落库账户基本信息
		try {
			accountDao.inserAccount(accountDTO);
		} catch (Exception e) {
			log.error("账户写入数据库失败",e);
			e.printStackTrace();
			accountDTO = new AccountDTO();
			accountDTO.setMessage("Failed ! Save account error");
			accountDTO.setCode(2);
			return accountDTO;
		}

		// 创建以太坊地址
		String address = "";
		try {
			address = walletService.newAddress(accountDTO.getUsername(), accountDTO.getPassword());
		} catch (Exception e) {
			log.error("调用以太坊rpc接口 创建账号失败",e);
			e.printStackTrace();
			accountDTO = new AccountDTO();
			accountDTO.setMessage("Failed ! Save account to block error");
			accountDTO.setCode(3);
			return accountDTO;
		}
		if (!"".equals(address) && address != null) {
			// 将地址转为16进制
			address = TypeConverter.toJsonHex(address);
			accountDTO.setAddress(address);
		} else {
			// 创建以太坊账户出问题了
			accountDTO.setMessage("Failed ! Save account to block error");
			accountDTO.setCode(3);
			return accountDTO;
		}

		// 反向更新账户 加入 地址信息
		int isOk = accountDao.updateAccount(accountDTO);
		if (isOk <= 0) {
			accountDTO = new AccountDTO();
			// 反向更新账户数据库失败
			accountDTO.setMessage("Failed ! Update account error");
			accountDTO.setCode(4);
			return accountDTO;
		}

		// reset id & pwd
		accountDTO.setId(null);
		accountDTO.setPassword(null);
		
		// 最终成功
		accountDTO.setMessage("Success ! Please remember your address and password");
		accountDTO.setCode(0);
		return accountDTO;
	}

	// 登录账户
	public AccountDTO login(AccountDTO accountDTO) {
		log.info("do login"+JSON.toJSONString(accountDTO));
		
		//校验用户名密码
		boolean check = checkUsernameAndPassword(accountDTO);
		if (!check) {
			accountDTO = new AccountDTO();
			accountDTO.setMessage("Failed ! No Username and Password");
			accountDTO.setCode(10);
			return accountDTO;
		}
		
		// 查库是否成功登录
		accountDTO = accountDao.checkLogin(accountDTO);

		if (accountDTO == null) {
			accountDTO = new AccountDTO();
			accountDTO.setMessage("Failed ! User name and password error");
			accountDTO.setCode(5);
			return accountDTO;
		}

		// 判断是否是admin
		accountDTO.setIsAdmin("false");
		if ("Sam".equals(accountDTO.getUsername())) {
			accountDTO.setIsAdmin("true");
		}

		// 查询余额
		String balance = "";
		try {
			balance = ethJsonRpc.getBalance(accountDTO.getAddress(), null);
		} catch (Exception e) {
			log.error("调用以太坊查询余额失败",e);
			accountDTO = new AccountDTO();
			accountDTO.setMessage("Failed ! Get balance error");
			accountDTO.setCode(6);
		}
		
		accountDTO.setCode(0);
		accountDTO.setBalance(balance);
		return accountDTO;
	}

	// 查询用户基本信息by username
	public AccountDTO loadAccountInfo(AccountDTO accountDTO) {
		log.info("do loadAccountInfo"+JSON.toJSONString(accountDTO));
		
		//验证用户名
		boolean check = checkUsername(accountDTO);
		if (!check) {
			accountDTO = new AccountDTO();
			accountDTO.setMessage("Failed ! No Username");
			accountDTO.setCode(10);
			return accountDTO;
		}
		
		//根据用户名查询数据库
		AccountDTO  adto= accountDao.accountInfo(accountDTO);
		
		//如果用户名不存在直接return回去
		if (adto==null) {
			adto = new AccountDTO();
			adto.setMessage("Failed ! Username not exists");
			adto.setCode(7);
			return adto;
		}
		
		// 判断是否是admin
		adto.setIsAdmin("false");
		if ("Sam".equals(adto.getUsername())) {
			adto.setIsAdmin("true");
		}
		
		// 查询余额
		String balance = "";
		try {
			balance = ethJsonRpc.getBalance(adto.getAddress(), null);
		} catch (Exception e) {
			log.error("调用以太坊查询余额失败",e);
			adto = new AccountDTO();
			adto.setMessage("Failed ! Get balance error");
			adto.setCode(6);
		}
		adto.setBalance(balance);
		adto.setMessage("Success ! ");
		adto.setCode(0);
		return adto;
	}

	// 转账
	public String sendCoin(String from, String to, String amount, String password) throws Exception{
		log.info("do sendCoin"+" from:"+from+" to:"+to+" amount:"+amount+" password:"+password);
		
		if ("".equals(from)||from==null||
				"".equals(to)||to==null||
				"".equals(amount)||amount==null||
				"".equals(password)||password==null
				) {
			return "check error";
		}
		
		//构建交易参数
		CallArguments callArguments = new CallArguments();
		callArguments.from = from;
		callArguments.to = to;
		callArguments.value = amount;
		
		//调用rpc接口获取最大的count 用于下一笔交易的nonce
		String nonce = "";
		nonce =  ethJsonRpc.eth_getTransactionCount(from, "latest");
		callArguments.nonce = nonce;
		
//		gasPrice gas默认 21000 暂时不传入 传入有其他未知问题 未找到
//		callArguments.gasPrice = ethJsonRpc.eth_gasPrice();
//		callArguments.gas = TypeConverter.toJsonHex("21000");
		
		String result = "";
		//解锁账户
		ethJsonRpc.unlockAccount(from, password);
		//rpc调用转账
		result = ethJsonRpc.eth_sendTransaction(callArguments);
		//账户加锁
		ethJsonRpc.personal_lockAccount(from);
		
		return result;
	}
	
	//校验用户名密码
	private boolean checkUsernameAndPassword(AccountDTO accountDTO) {
		if (accountDTO==null) {
			return false;
		}
		if ("".equals(accountDTO.getUsername())||accountDTO.getUsername()==null) {
			return false;
		}
		if ("".equals(accountDTO.getPassword())||accountDTO.getPassword()==null) {
			return false;
		}
		return true;
	}
	
	//校验用户名
	private boolean checkUsername(AccountDTO accountDTO) {
		if (accountDTO==null) {
			return false;
		}
		if ("".equals(accountDTO.getUsername())||accountDTO.getUsername()==null) {
			return false;
		}
		return true;
	}

}
