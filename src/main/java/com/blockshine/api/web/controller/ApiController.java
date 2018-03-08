package com.blockshine.api.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blockshine.api.dto.BlockInfo;
import com.blockshine.api.jsonrpc.TransactionResultDTO;
import com.blockshine.api.jsonrpc.JsonRpc.BlockResult;
import com.blockshine.api.keystore.Keystore;
import com.blockshine.api.service.BlockchainInfoService;
import com.blockshine.api.service.WalletService;
import com.blockshine.api.util.ErrorCodes;
import com.blockshine.api.util.exception.HarmonyException;
import com.blockshine.api.web.controller.WalletController.NewAddressDTO;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LRUMap;
import org.ethereum.config.CommonConfig;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.*;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.ethereum.db.BlockStore;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.db.TransactionStore;
import org.ethereum.facade.Ethereum;
import org.ethereum.jsonrpc.TypeConverter;
import org.ethereum.listener.CompositeEthereumListener;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.manager.WorldManager;
import org.ethereum.mine.BlockMiner;
import org.ethereum.mine.EthashAlgo;
import org.ethereum.mine.MinerIfc;
import org.ethereum.net.client.ConfigCapabilities;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.rlpx.discover.NodeManager;
import org.ethereum.net.server.ChannelManager;
import org.ethereum.net.server.PeerServer;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.ethereum.sync.SyncManager;
import org.ethereum.util.BuildInfo;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.DataWord;
import org.ethereum.vm.LogInfo;
import org.ethereum.vm.program.invoke.ProgramInvokeFactory;
import org.spongycastle.util.encoders.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.blockshine.api.jsonrpc.TypeConverter.*;
import static java.math.BigInteger.valueOf;
import static org.ethereum.crypto.HashUtil.sha3;
import static org.ethereum.util.ByteUtil.*;

@RestController
@Slf4j(topic = "jsonrpc")
public class ApiController {

	private static final String BLOCK_LATEST = "latest";
	private volatile String hashrate;

	public class BinaryCallArguments {
		public long nonce;
		public long gasPrice;
		public long gasLimit;
		public String toAddress;
		public String fromAddress;
		public long value;
		public byte[] data;

		// public void setArguments(CallArguments args) throws Exception {
		// nonce = 0;
		// if (args.nonce != null && args.nonce.length() != 0)
		// nonce = JSonHexToLong(args.nonce);
		//
		// gasPrice = 0;
		// if (args.gasPrice != null && args.gasPrice.length() != 0)
		// gasPrice = JSonHexToLong(args.gasPrice);
		//
		// gasLimit = 4_000_000;
		// if (args.gas != null && args.gas.length() != 0)
		// gasLimit = JSonHexToLong(args.gas);
		//
		// toAddress = null;
		// if (args.to != null && !args.to.isEmpty())
		// toAddress = JSonHexToHex(args.to);
		//
		// fromAddress = null;
		// if (args.from != null && !args.from.isEmpty())
		// fromAddress = JSonHexToHex(args.from);
		//
		// value = 0;
		// if (args.value != null && args.value.length() != 0)
		// value = JSonHexToLong(args.value);
		//
		// data = null;
		//
		// if (args.data != null && args.data.length() != 0)
		// data = TypeConverter.StringHexToByteArray(args.data);
		// }
	}

	@Autowired
	WalletService walletService;

	@Autowired
	Keystore keystore;

	@Autowired
	public WorldManager worldManager;

	@Autowired
	public Repository repository;

	@Autowired
	BlockchainImpl blockchain;

	@Autowired
	Ethereum eth;

	@Autowired
	PeerServer peerServer;

	@Autowired
	SyncManager syncManager;

	@Autowired
	TransactionStore txStore;

	@Autowired
	ChannelManager channelManager;

	@Autowired
	NodeManager nodeManager;

	@Autowired
	CompositeEthereumListener compositeEthereumListener;

	@Autowired
	BlockMiner blockMiner;

	@Autowired
	TransactionStore transactionStore;

	@Autowired
	PendingStateImpl pendingState;

	@Autowired
	SystemProperties config;

	@Autowired
	ConfigCapabilities configCapabilities;

	@Autowired
	BlockStore blockStore;

	@Autowired
	ProgramInvokeFactory programInvokeFactory;

	@Autowired
	CommonConfig commonConfig = CommonConfig.getDefault();

	@Autowired
	BlockchainInfoService blockchainInfoService;

	//////////////// ACCOUNTS
	// 账户清单
	// http://localhost:8090/accounts
	// {"accounts":["0xf73e9086f0bfa3cc7f9ed49aadb522bed0cf7f1d","0x53427da8220f6cf9f3f73bc130f00b5845985059"]}
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String[]> accounts() {
		String[] accounts = keystore.listStoredKeys();
		Map<String, String[]> result = new HashMap<>();
		result.put("accounts", accounts);
		return result;
	}

	// 查询余额
	@RequestMapping(value = "/account/balance", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> eth_getBalance(String address, String blockId) throws Exception {
		Objects.requireNonNull(address, "address is required");
		blockId = blockId == null ? BLOCK_LATEST : blockId;
		byte[] addressAsByteArray = TypeConverter.StringHexToByteArray(address);
		BigInteger balance = getRepoByJsonBlockId(blockId).getBalance(addressAsByteArray);
		System.out.println(balance);
		// 16进制
		// return TypeConverter.toJsonHex(balance);
		Map<String, String> result = new HashMap<>();
		result.put("balance", balance + "");
		return result;

	}

	// 创建账户
	@RequestMapping(value = "/account/create", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> newAddress(NewAddressDTO data) {
		String address = walletService.newAddress(data.getName(), data.getSecret());
		Map<String, String> result = new HashMap<>();
		result.put("address", address);
		return result;
	}

	//////////////////// BLOCK & TRANSACTION

	// 返回最近块的数量
	// http://localhost:8090/block/number
	// {"blockNumber":"0"}
	@RequestMapping(value = "/block/number", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> eth_blockNumber() {
		Long blockNumber = blockchain.getBestBlock().getNumber();
		Map<String, String> result = new HashMap<>();
		result.put("blockNumber", blockNumber + "");
		return result;
	}

	// Returns the number of transactions sent from an address.
	// 返回从该地址发送的交易总数
	// String "earliest" for the earliest/genesis block
	// String "latest" - for the latest mined block
	// String "pending" - for the pending state/transactions
	// http://localhost:8090/block/transactionCount?address=0x1aB7D15092b4f5742Acb6Ba11322739511A5F193&blockId=earliest
	// {"nonce":"0"}
	@RequestMapping(value = "/block/transactionCount", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> eth_getTransactionCount(String address, String blockId) throws Exception {
		byte[] addressAsByteArray = TypeConverter.StringHexToByteArray(address);
		BigInteger nonce = getRepoByJsonBlockId(blockId).getNonce(addressAsByteArray);

		Map<String, String> result = new HashMap<>();
		result.put("nonce", nonce + "");

		return result;// TypeConverter.toJsonHex(nonce);
	}

	// Returns information about a block by block number.
	// 通过块编号返回有关块的信息
	// http://localhost:8090/block/info?bnOrId=0x1942&fullTransactionObjects=false
	//
	@RequestMapping(value = "/block/info", method = RequestMethod.GET)
	@ResponseBody
	public BlockResult eth_getBlockByNumber(String bnOrId, Boolean fullTransactionObjects) throws Exception {
		final Block b;
		if ("pending".equalsIgnoreCase(bnOrId)) {
			b = blockchain.createNewBlock(blockchain.getBestBlock(), pendingState.getPendingTransactions(),
					Collections.<BlockHeader>emptyList());
		} else {
			b = getByJsonBlockId(bnOrId);
		}
		BlockResult br = (b == null ? null : getBlockResult(b, fullTransactionObjects));
		return br;
	}

	//查询一组区块头信息 从 指定hash开始 固定 qty条
	@RequestMapping(value = "/block/headers", method = RequestMethod.GET)
	@ResponseBody
//http://localhost:8090/block/currentBlocks    
//	[
//	    {
//	        "parentHash":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
//	        "unclesHash":"HcxN6N7HXXqrhbVntszUGtMSRRuUinQT8KFC/UDUk0c=",
//	        "coinbase":"AAAAAAAAAAAAAAAAAAAAAAAAAAA=",
//	        "stateRoot":"RID1IcSLlnyBbPffmMylibyovjLrBKR/XBi+zYfd/90=",
//	        "txTrieRoot":"VugfFxvMVab/g0XmksD4bltI4BuZbK3AAWIvteNjtCE=",
//	        "logsBloom":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==",
//	        "difficulty":"AgAA",
//	        "timestamp":0,
//	        "number":0,
//	        "gasLimit":"TEtA",
//	        "gasUsed":0,
//	        "mixHash":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
//	        "extraData":"Ebvo2040e06Mk3wcg3Dkte0zrbPbacvbejjh5Qsbgvo=",
//	        "nonce":"AAAAAAAAAEI=",
//	        "encoded":"+QIToAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoB3MTejex116q4W1Z7bM1BrTEkUblIp0E/ChQv1A1JNHlAAAAAAAAAAAAAAAAAAAAAAAAAAAoESA9SHEi5Z8gWz335jMpYm8qL4y6wSkf1wYvs2H3f/doFboHxcbzFWm/4NF5pLA+G5bSOAbmWytwAFiL7XjY7QhoFboHxcbzFWm/4NF5pLA+G5bSOAbmWytwAFiL7XjY7QhuQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIMCAACAg0xLQICAoBG76NtONHtOjJN8HINw5LXtM62z22nL23o44eULG4L6oAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAiAAAAAAAAABC",
//	        "powBoundary":"AACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
//	        "hash":"GUJYZrfTKYoVrXmszzArqdIYWRdOeumc5VLgXxPw76M=",
//	        "receiptsRoot":"VugfFxvMVab/g0XmksD4bltI4BuZbK3AAWIvteNjtCE=",
//	        "difficultyBI":131072,
//	        "genesis":true,
//	        "encodedWithoutNonce":"+QHpoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoB3MTejex116q4W1Z7bM1BrTEkUblIp0E/ChQv1A1JNHlAAAAAAAAAAAAAAAAAAAAAAAAAAAoESA9SHEi5Z8gWz335jMpYm8qL4y6wSkf1wYvs2H3f/doFboHxcbzFWm/4NF5pLA+G5bSOAbmWytwAFiL7XjY7QhoFboHxcbzFWm/4NF5pLA+G5bSOAbmWytwAFiL7XjY7QhuQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIMCAACAg0xLQICAoBG76NtONHtOjJN8HINw5LXtM62z22nL23o44eULG4L6",
//	        "shortDescr":"#0 (194258 <~ 000000)"
//	    }
//	]
	public List<BlockHeader> getBlocksEndWith(byte[] hash, Long qty) {
		if (qty == null) {
			qty = 10L;
		}
		
		//查询最近一笔区块
		Block block = blockchain.getBlockByNumber(0);
		//没有传入hash 查询最近的一个区块
		if (hash==null) {
			hash = block.getHash();
		}
		return blockStore.getListHeadersEndWith(hash, qty.longValue());
	}

	// test controller method
	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	@ResponseBody
	public Object welcome() {
		log.info("welcome to BlockShine");
		return "welcome to BlockShine";
	}

	///////////////// 其他依赖方法 在下面

	private Repository getRepoByJsonBlockId(String id) {
		if ("pending".equalsIgnoreCase(id)) {
			return pendingState.getRepository();
		} else {
			Block block = getByJsonBlockId(id);
			return this.repository.getSnapshotTo(block.getStateRoot());
		}
	}

	private Block getByJsonBlockId(String id) {
		if ("earliest".equalsIgnoreCase(id)) {
			return blockchain.getBlockByNumber(0);
		} else if ("latest".equalsIgnoreCase(id)) {
			return blockchain.getBestBlock();
		} else if ("pending".equalsIgnoreCase(id)) {
			return null;
		} else {
			long blockNumber = StringHexToBigInteger(id).longValue();
			return blockchain.getBlockByNumber(blockNumber);
		}
	}

	protected Account importAccount(ECKey key, String password) {
		final Account account = new Account();
		account.init(key);

		keystore.storeKey(key, password);
		return account;
	}

	protected BlockResult getBlockResult(Block block, boolean fullTx) {
		if (block == null)
			return null;
		boolean isPending = ByteUtil.byteArrayToLong(block.getNonce()) == 0;
		BlockResult br = new BlockResult();
		br.number = isPending ? null : toJsonHex(block.getNumber());
		br.hash = isPending ? null : toJsonHex(block.getHash());
		br.parentHash = toJsonHex(block.getParentHash());
		br.nonce = isPending ? null : toJsonHex(block.getNonce());
		br.sha3Uncles = toJsonHex(block.getUnclesHash());
		br.logsBloom = isPending ? null : toJsonHex(block.getLogBloom());
		br.transactionsRoot = toJsonHex(block.getTxTrieRoot());
		br.stateRoot = toJsonHex(block.getStateRoot());
		br.receiptRoot = toJsonHex(block.getReceiptsRoot());
		br.miner = isPending ? null : toJsonHex(block.getCoinbase());
		br.difficulty = toJsonHex(block.getDifficultyBI());
		br.totalDifficulty = toJsonHex(blockStore.getTotalDifficultyForHash(block.getHash()));
		if (block.getExtraData() != null)
			br.extraData = toJsonHex(block.getExtraData());
		br.size = toJsonHex(block.getEncoded().length);
		br.gasLimit = toJsonHex(block.getGasLimit());
		br.gasUsed = toJsonHex(block.getGasUsed());
		br.timestamp = toJsonHex(block.getTimestamp());

		List<Object> txes = new ArrayList<>();
		if (fullTx) {
			for (int i = 0; i < block.getTransactionsList().size(); i++) {
				txes.add(new TransactionResultDTO(block, i, block.getTransactionsList().get(i)));
			}
		} else {
			for (Transaction tx : block.getTransactionsList()) {
				txes.add(toJsonHex(tx.getHash()));
			}
		}
		br.transactions = txes.toArray();

		List<String> ul = new ArrayList<>();
		for (BlockHeader header : block.getUncleList()) {
			ul.add(toJsonHex(header.getHash()));
		}
		br.uncles = ul.toArray(new String[ul.size()]);

		return br;
	}

}
