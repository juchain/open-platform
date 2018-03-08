/*
 * Copyright 2015, 2016 Ether.Camp Inc. (US)
 * This file is part of Ethereum Harmony.
 *
 * Ethereum Harmony is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ethereum Harmony is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ethereum Harmony.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.blockshine.api.service;

import static com.blockshine.api.jsonrpc.TypeConverter.toJsonHex;
import static com.blockshine.api.util.StreamUtil.streamOf;
import static com.blockshine.api.util.exception.ContractException.compilationError;
import static com.blockshine.api.util.exception.ContractException.validationError;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.ethereum.util.ByteUtil.byteArrayToLong;
import static org.ethereum.util.ByteUtil.longToBytesNoLeadZeroes;
import static org.ethereum.util.ByteUtil.toHexString;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.Blockchain;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.PendingStateImpl;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.datasource.DbSource;
import org.ethereum.datasource.leveldb.LevelDbDataSource;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.ethereum.vm.program.Program;
import org.ethereum.vm.program.ProgramResult;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blockshine.contrdata.ContractDataService;
import com.blockshine.contrdata.contract.Ast;
import com.blockshine.contrdata.contract.ContractData;
import com.blockshine.contrdata.storage.Path;
import com.blockshine.contrdata.storage.Storage;
import com.blockshine.contrdata.storage.StorageEntry;
import com.blockshine.contrdata.storage.StoragePage;
import com.blockshine.contrdata.storage.dictionary.Layout;
import com.blockshine.contrdata.storage.dictionary.StorageDictionary;
import com.blockshine.contrdata.storage.dictionary.StorageDictionaryDb;
import com.blockshine.contrdata.storage.dictionary.StorageDictionaryVmHook;
import com.blockshine.api.dto.BShineRequest;
import com.blockshine.api.dto.BShineResponse;
import com.blockshine.api.dto.ContractObjects.ContractInfoDTO;
import com.blockshine.api.dto.ContractObjects.IndexStatusDTO;
import com.blockshine.api.jsonrpc.EthJsonRpcImpl;
import com.blockshine.api.jsonrpc.JsonRpc.CallArguments;
import com.blockshine.api.service.contracts.Source;
import com.blockshine.api.util.SolcUtils;
import com.blockshine.api.util.TrustSSL;
import com.blockshine.api.util.exception.ContractException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import fj.data.Validation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Viewing contract storage variables.
 * Depends on contract-data project.
 *
 * This class operates with hex address in lowercase without 0x.
 *
 * Created by Stan Reshetnyk on 17.10.16.
 */
@Slf4j(topic = "contracts")
@Service
public class ContractsService {

    private static final Pattern FUNC_HASHES_PATTERN = Pattern.compile("(PUSH4\\s+0x)([0-9a-fA-F]{2,8})(\\s+DUP2)?(\\s+EQ\\s+[PUSH1|PUSH2])");
    private static final Pattern SOLIDITY_HEADER_PATTERN = Pattern.compile("^\\s{0,}PUSH1\\s+0x60\\s+PUSH1\\s+0x40\\s+MSTORE.+");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final byte[] SYNCED_BLOCK_KEY = "syncedBlock".getBytes(UTF_8);

    @Autowired
    StorageDictionaryVmHook storageDictionaryVmHook;

    @Autowired
    ContractDataService contractDataService;

    @Autowired
    StorageDictionaryDb dictionaryDb;

    @Autowired
    SystemProperties config;

    @Autowired
    Ethereum ethereum;

    @Autowired
    Storage storage;

    @Autowired
    private Environment env;

    @Autowired
    private Blockchain blockchain;

    @Autowired
	PendingStateImpl pendingState;
    
    @Autowired
    EthJsonRpcImpl jspnRpc;
    
    DbSource<byte[]> contractsStorage;

    DbSource<byte[]> settingsStorage;

    DbSource<byte[]> contractCreation;

    /**
     * Contract data will be fully available from this block.
     * Usually this is pivot block in fast sync or zero block for regular sync.
     */
    volatile Optional<Long> syncedBlock = Optional.empty();   // undetected yet

    private ObjectToBytesFormat<ContractEntity> contractFormat = new ObjectToBytesFormat<>(ContractEntity.class);

    @PostConstruct
    public void init() {
        contractsStorage = new LevelDbDataSource("contractsStorage");
        contractsStorage.init();

        settingsStorage = new LevelDbDataSource("settings");
        settingsStorage.init();

        contractCreation = new LevelDbDataSource("contractCreation");
        contractCreation.init();

        syncedBlock = Optional.ofNullable(settingsStorage.get(SYNCED_BLOCK_KEY))
                .map(bytes -> byteArrayToLong(bytes));

        ethereum.addListener(new EthereumListenerAdapter() {
            @Override
            public void onBlock(Block block, List<TransactionReceipt> receipts) {
            	
                // if first loaded block is null - let's save first imported block as starting point for contracts
                // track block from which we started sync
                if (!syncedBlock.isPresent()) {
                    syncedBlock = Optional.of(block.getNumber());
                    settingsStorage.put(SYNCED_BLOCK_KEY, longToBytesNoLeadZeroes(block.getNumber()));
                    settingsStorage.flush();
                    log.info("Synced block is set to #{}", block.getNumber());
                }

                // store block number of each new contract
                receipts.stream()
                        .flatMap(r -> streamOf(r.getTransaction().getContractAddress()))
                        .forEach(address -> {
                            log.info("Marked contract creation block {} {}", Hex.toHexString(address), block.getNumber());
                            contractCreation.put(address, longToBytesNoLeadZeroes(block.getNumber()));
                            contractCreation.flush();
                        });
            }
        });
        log.info("Initialized contracts. Synced block is #{}", syncedBlock.map(Object::toString).orElseGet(() -> "Undefined"));

        TrustSSL.apply();
    }

    public boolean deleteContract(String address) {
        contractsStorage.delete(Hex.decode(address));
        return true;
    }

    public ContractInfoDTO addContract(String address, String src) {
        return compileAndSave(address, Arrays.asList(src));
    }

    public List<ContractInfoDTO> getContracts() {
        return contractsStorage.keys().stream()
                .map(a -> {
                    final ContractEntity contract = loadContract(a);
                    final Long blockNumber = getContractBlock(a);
                    return new ContractInfoDTO(Hex.toHexString(a), contract.getName(), blockNumber);
                })
                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                .collect(toList());
    }

    private long getContractBlock(byte[] address) {
        return Optional.ofNullable(contractCreation.get(address)).map(b -> byteArrayToLong(b)).orElse(-1L);
    }

    public ContractInfoDTO uploadContract(String address, MultipartFile[] files) {
        return compileAndSave(address, Source.toPlain(files));
    }

    public IndexStatusDTO getIndexStatus() throws IOException {
        final long totalSize = Arrays.asList("/storageDict", "/contractCreation").stream()
                .mapToLong(name -> FileUtils.sizeOfDirectory(new File(config.databaseDir() + name)))
                .sum();

        return new IndexStatusDTO(
                totalSize,
                SolcUtils.getSolcVersion(),
                syncedBlock.orElse(-1L));
    }

    /**
     * Get contract storage entries.
     *
     * @param hexAddress - address of contract
     * @param path - nested level of fields
     * @param pageable - for paging
     */
    public Page<StorageEntry> getContractStorage(String hexAddress, String path, Pageable pageable) {
        final byte[] address = Hex.decode(hexAddress);
        final ContractEntity contract = Optional.ofNullable(contractsStorage.get(address))
                .map(bytes -> contractFormat.decode(bytes))
                .orElseThrow(() -> new RuntimeException("Contract sources not found"));

        final StoragePage storagePage = getContractData(hexAddress, contract.getDataMembers(), Path.parse(path), pageable.getPageNumber(), pageable.getPageSize());

        final PageImpl<StorageEntry> storage = new PageImpl<>(storagePage.getEntries(), pageable, storagePage.getTotal());

        return storage;
    }


    protected StoragePage getContractData(String address, String contractDataJson, Path path, int page, int size) {
        byte[] contractAddress = Hex.decode(address);
        StorageDictionary dictionary = getDictionary(contractAddress);

        ContractData contractData = ContractData.parse(contractDataJson, dictionary);

        final boolean hasFullIndex = contractCreation.get(contractAddress) != null;

        if (!hasFullIndex) {
            contractDataService.fillMissingKeys(contractData);
        }

        return contractDataService.getContractData(contractAddress, contractData, false, path, page, size);
    }

    protected StorageDictionary getDictionary(byte[] address) {
        return dictionaryDb.getDictionaryFor(Layout.Lang.solidity, address);
    }

    private String getValidatedAbi(String address, String contractName, CompilationResult result) {
        log.debug("getValidatedAbi address:{}, contractName: {}", address, contractName);
        final ContractMetadata metadata = result.getContracts().get(contractName);
        if (metadata == null) {
            throw validationError("Contract with name '%s' not found in uploaded sources.", contractName);
        }

        final String abi = metadata.getAbi();
        final CallTransaction.Contract contract = new CallTransaction.Contract(abi);
        if (ArrayUtils.isEmpty(contract.functions)) {
            throw validationError("Contract with name '%s' not found in uploaded sources.", contractName);
        }

        final List<CallTransaction.FunctionType> funcTypes = asList(CallTransaction.FunctionType.function, CallTransaction.FunctionType.constructor);
        final Set<String> funcHashes = stream(contract.functions)
                .filter(function -> funcTypes.contains(function.type))
                .map(func -> {
                    log.debug("Compiled funcHash " + toHexString(func.encodeSignature()) + " " + func.name);
                    return toHexString(func.encodeSignature());
                })
                .collect(toSet());


        final String code = toHexString(ethereum.getRepository().getCode(Hex.decode(address)));
        final String asm = getAsm(code);
        if (isBlank(asm)) {
            throw validationError("Wrong account type: account with address '%s' hasn't any code.", address);
        }

        final Set<String> extractFuncHashes = extractFuncHashes(asm);
        extractFuncHashes.forEach(h -> log.debug("Extracted ASM funcHash " + h));
        extractFuncHashes.forEach(funcHash -> {
            if (!funcHashes.contains(funcHash)) {
                throw validationError("Incorrect code version: function with hash '%s' not found.", funcHash);
            }
        });
        log.debug("Contract is valid " + contractName);
        return abi;
    }

    public static Set<String> extractFuncHashes(String asm) {
        Set<String> result = new HashSet<>();

//        String beforeJumpDest = substringBefore(asm, "JUMPDEST");
        Matcher matcher = FUNC_HASHES_PATTERN.matcher(asm);
        while (matcher.find()) {
            String hash = matcher.group(2);
            result.add(leftPad(hash, 8, "0"));
        }

        return result;
    }

    private static CompilationResult compileAbi(byte[] source) throws ContractException {
        try {
            SolidityCompiler.Result result = SolidityCompiler.compile(source, true, SolidityCompiler.Options.ABI);

            if (result.isFailed()) {
                throw compilationError(result.errors);
            }

            return parseCompilationResult(result.output);
        } catch (IOException e) {
            log.error("solc compilation error: ", e);
            throw compilationError(e.getMessage());
        }
    }

    private static Ast compileAst(byte[] source) {
        try {
            SolidityCompiler.Result result = SolidityCompiler.compile(source, false, SolidityCompiler.Options.AST);

            if (result.isFailed()) {
                throw compilationError(result.errors);
            }

            return Ast.parse(result.output);
        } catch (IOException e) {
            log.error("solc compilation error: ", e);
            throw compilationError(e.getMessage());
        }
    }

    private String getAsm(String code) {
        if (isBlank(code)) return StringUtils.EMPTY;

        try {
            return Program.stringify(Hex.decode(code));
        } catch (Program.IllegalOperationException e) {
            return e.getMessage();
        }
    }

    /**
     * Try to compile each file and check if it's interface matches to asm functions hashes
     * at the deployed contract.
     * Save contract if valid one is found, or merge names.
     * @return contract name(s) from matched file
     */
    private ContractInfoDTO compileAndSave(String hexAddress, List<String> files) {
        final byte[] address = Hex.decode(hexAddress);

        final byte[] codeBytes = ethereum.getRepository().getCode(address);
        if (codeBytes == null || codeBytes.length == 0) {
            throw validationError("Account with address '%s' hasn't any code. Please ensure blockchain is fully synced.", hexAddress);
        }

        // get list of contracts which match to deployed code
        final List<Validation<ContractException, ContractEntity>> validationResult = files.stream()
                .flatMap(src -> {
                    final CompilationResult result = compileAbi(src.getBytes());

                    return result.getContracts().entrySet().stream()
                            .map(entry -> validateContracts(hexAddress, src, result, entry.getKey()));

                }).collect(Collectors.toList());

        final List<ContractEntity> validContracts = validationResult.stream()
                .filter(v -> v.isSuccess())
                .map(v -> v.success())
                .collect(toList());

        if (!validContracts.isEmpty()) {
            // SUCCESS

            // join contract names if there are few with same signature
            // in that way we will provide more information for a user
            final String contractName = validContracts.stream()
                    .map(cc -> cc.getName())
                    .distinct()
                    .collect(joining("|"));

            // save
            validContracts.stream()
                    .findFirst()
                    .ifPresent(entity -> {
                        entity.name = contractName;
                        contractsStorage.put(address, contractFormat.encode(entity));
                        contractsStorage.flush();
                    });

            return new ContractInfoDTO(hexAddress, contractName, getContractBlock(address));
        } else {
            if (validationResult.size() == 1) {
                throw validationResult.stream()
                        .findFirst()
                        .map(v -> v.fail())
                        .get();
            } else {
                throw validationError("Target contract source not found within uploaded sources.");
            }
        }
    }

    private Validation<ContractException, ContractEntity> validateContracts(String address, String src,
                                                                             CompilationResult result,
                                                                             String name) {
        try {
            final String abi = getValidatedAbi(address, name, result);
            final String dataMembers = compileAst(src.getBytes()).getContractAllDataMembers(name).toJson();

            final ContractEntity contract = new ContractEntity(name, src, dataMembers, abi);

            return Validation.success(contract);
        } catch (ContractException e) {
            log.debug("Problem with contract. " + e.getMessage());
            return Validation.fail(e);
        }
    }

    private ContractEntity loadContract(byte[] address) {
        final byte[] loadedBytes = contractsStorage.get(address);
        return contractFormat.decode(loadedBytes);
    }

    private static CompilationResult parseCompilationResult(String rawJson) throws IOException {
        return new ObjectMapper().readValue(rawJson, CompilationResult.class);
    }

    private boolean equals(byte[] b1, byte[] b2) {
        return new ByteArrayWrapper(b1).equals(new ByteArrayWrapper(b2));
    }

    public boolean importContractFromExplorer(String hexAddress) throws Exception {
        final byte[] address = Hex.decode(hexAddress);
        final String explorerHost = Optional.ofNullable(blockchain.getBlockByNumber(0l))
                .map(block -> Hex.toHexString(block.getHash()))
                .flatMap(hash -> BlockchainConsts.getNetworkInfo(env, hash).getSecond())
                .orElseThrow(() -> new RuntimeException("Can't import contract for this network"));

        final String url = String.format("%s/api/v1/accounts/%s/smart-storage/export", explorerHost, hexAddress);
        log.info("Importing contract:{} from:{}", hexAddress, url);
        final JsonNode result = Unirest.get(url).asJson().getBody();

        final JSONObject resultObject = result.getObject();
        final Map<String, String> map = new HashedMap<>();
        resultObject.keySet().stream()
                .forEach(k -> map.put((String) k, resultObject.getString((String) k)));

        contractDataService.importDictionary(address, map);

        contractCreation.put(address, longToBytesNoLeadZeroes(-2L));
        contractCreation.flush();
        return true;
    }

    /**
     * For testing purpose.
     */
    public void clearContractStorage(String hexAddress) throws Exception {
        final byte[] address = Hex.decode(hexAddress);
        log.info("Clear storage of contract:{}", hexAddress);
        contractDataService.clearDictionary(address);
        contractCreation.delete(address);

        // re-import to fill members
        final ContractEntity contractEntity = loadContract(address);
        compileAndSave(hexAddress, Arrays.asList(contractEntity.src));
    }
    
    @SuppressWarnings("rawtypes")
	public BShineResponse compileContract(String contract) throws Exception {
        SolidityCompiler.Result res = SolidityCompiler.compile(
                contract.getBytes(), true, SolidityCompiler.Options.ABI, 
                SolidityCompiler.Options.BIN, SolidityCompiler.Options.INTERFACE);
        if (res.isFailed()) {
        		return BShineResponse.createErrorStatus("Compilation error occurred: " + res.errors);
        }
        org.ethereum.solidity.compiler.CompilationResult result = org.ethereum.solidity.compiler.CompilationResult.parse(res.output);
        org.ethereum.solidity.compiler.CompilationResult.ContractMetadata contractMetadata = result.contracts.values().iterator().next();
        if (contractMetadata.bin == null || contractMetadata.bin.isEmpty()) {
        		log.warn("Compilation failed("+ contract +"), no binary returned:\n" + res.errors);
            return BShineResponse.createErrorStatus("Compilation failed, no binary code returned:\n" + res.errors);
        } else {
        		ContractCompiledResult result1 = new ContractCompiledResult();
        		result1.binaryCode = toJsonHex(contractMetadata.bin);
        		result1.functionList = new CallTransaction.Contract(contractMetadata.abi).functions.toString();
	        return BShineResponse.createSuccessStatus(result1);
        }
    }

    /**
     * deploy the contract in async ways.
     * 
     * @param args
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public BShineResponse deployContract(CallArguments args) throws Exception {
    		BShineResponse result = compileContract(args.data);
    		if (result.getStatus() == BShineResponse.ResultStatus.Failed) {
    			return result;
    		}
    		CompilationAndDeployResult result1 = new CompilationAndDeployResult();
	    args.data = ((ContractCompiledResult)result.getResult()).binaryCode;
	    result1.txHash = jspnRpc.eth_sendTransaction(args);
	    result1.ccResult = ((ContractCompiledResult)result.getResult());
	    return BShineResponse.createSuccessStatus(result1);
	}
    
    @SuppressWarnings("rawtypes")
    public BShineResponse queryContractAddress(String txHash) throws Exception {
    		com.blockshine.api.jsonrpc.TransactionReceiptDTO receipt = jspnRpc.eth_getTransactionReceipt(txHash);
		if (receipt == null || receipt.getContractAddress() == null) {
			log.info("Contract does not exit");
			return BShineResponse.createErrorStatus("Contract does not exit.");
		}
	    return BShineResponse.createSuccessStatus(receipt);
	}
    
    /**
     * @param transactionHash
     * @return String - If its a call the result data, 
     *         if its a send transaction a created contract address, or the transaction hash, 
     *         see web3.eth.sendTransaction for details.
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public BShineResponse invokeContract(InvokeContractRequest request) throws Exception {
		CallTransaction.Contract contract = new CallTransaction.Contract(request.functionList);
		CallTransaction.Function inc = contract.getByName(request.invokedFunction);
	    if (inc == null) {
	    		return BShineResponse.createErrorStatus("Unable to find this function: " + request.invokedFunction);
	    }
	    if (inc.constant) {
	    		ProgramResult r = ethereum.callConstantFunction(request.getContractAddress(), inc);
	        Object[] ret = inc.decodeResult(r.getHReturn());
	        return BShineResponse.createSuccessStatus(Arrays.toString(ret));
	    }
	    byte[] functionCallBytes = inc.encode(request.getParameters());
        CallArguments transArgu = new CallArguments();
        transArgu.from = request.getContractAddress();
        transArgu.to = request.getContractAddress();
        transArgu.data = com.blockshine.api.jsonrpc.TypeConverter.toJsonHex(functionCallBytes);
        transArgu.gas = request.gas;
        transArgu.gasPrice = request.gasPrice;
        transArgu.nonce = request.nonce;
        String txHash = jspnRpc.eth_sendTransaction(transArgu);
		return BShineResponse.createSuccessStatus(txHash);
	}
    
    @SuppressWarnings("rawtypes")
    public BShineResponse queryInvokeContractResult(String txHash) throws Exception {
    		com.blockshine.api.jsonrpc.TransactionReceiptDTO receipt = jspnRpc.eth_getTransactionReceipt(txHash);
		if (receipt == null) {
			log.info("Contract does not exit");
			return BShineResponse.createErrorStatus("Contract does not exit.");
		}
	    return BShineResponse.createSuccessStatus(receipt);
	}
    
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompilationAndDeployResult {
        public String txHash;//sent transaction with hash code;
        public ContractCompiledResult ccResult;

        @Override
        public String toString() {
            return "CompilationAndDeployResult{" +
                    "txHash='" + txHash + '\'' +
                    ", ccResult=" + ccResult +
                    '}';
        }
    }
    
    @Data
    @NoArgsConstructor
    public static class InvokeContractRequest extends BShineRequest {

        public String from; //合约调用者地址
        public String to;
        public String gas;
        public String gasPrice;
        public String value;
        public String nonce;
        
        private String contractAddress;//合约地址
        
        private String functionList; 
        
        private String invokedFunction; 
        
        private String[] parameters;

    }
    
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompilationResult {

        private Map<String, ContractMetadata> contracts;
        private String version;

    }

    @Data
    public static class ContractCompiledResult {
        private String functionList; // Application Binary Interface
        private String binaryCode;
    }
    
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContractMetadata {

        private String abi;

    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContractDTD {

        private Map<String, ContractMetadata> contracts;
        private String version;

    }

    /**
     * For storing in key-value database in json format.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContractEntity {

        private String name;
        private String src;
        private String dataMembers;
        private String abi;

    }

    /**
     * Helper for encoding/decoding entity to bytes via json intermediate step.
     */
    public static class ObjectToBytesFormat<T> {

        final ObjectMapper mapper = new ObjectMapper();

        final Class<T> type;

        public ObjectToBytesFormat(Class<T> type) {
            this.type = type;
        }

        public byte[] encode(T entity) {
            try {
                final String json = mapper.writeValueAsString(entity);
                return json.getBytes(UTF_8);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public T decode(byte[] bytes) {
            try {
                return mapper.readValue(new String(bytes, UTF_8), type);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
