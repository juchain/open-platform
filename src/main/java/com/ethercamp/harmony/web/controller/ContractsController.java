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

package com.ethercamp.harmony.web.controller;

import static com.ethercamp.harmony.dto.ActionStatus.createErrorStatus;
import static com.ethercamp.harmony.dto.ActionStatus.createSuccessStatus;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ethercamp.contrdata.storage.StorageEntry;
import com.ethercamp.harmony.dto.ActionStatus;
import com.ethercamp.harmony.dto.BShineResponse;
import com.ethercamp.harmony.dto.ContractObjects.ContractInfoDTO;
import com.ethercamp.harmony.dto.ContractObjects.IndexStatusDTO;
import com.ethercamp.harmony.jsonrpc.JsonRpc.CallArguments;
import com.ethercamp.harmony.service.ContractsService;
import com.ethercamp.harmony.service.ContractsService.InvokeContractRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Stan Reshetnyk on 18.10.16.
 */
@Slf4j
@RestController
public class ContractsController {

    @Autowired
    ContractsService contractsService;

    @RequestMapping("/contracts/{address}/storage")
    public Page<StorageEntry> getContractStorage(@PathVariable String address,
                                                 @RequestParam(required = false) String path,
                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                 @RequestParam(required = false, defaultValue = "5") int size) {
        return contractsService.getContractStorage(address, path, new PageRequest(page, size));
    }

    @RequestMapping(value = "/contracts/add", method = RequestMethod.POST)
    public ActionStatus<ContractInfoDTO> addContractSources(@RequestBody WatchContractDTO watchContract) {
        try {
            ContractInfoDTO contract = contractsService.addContract(watchContract.address, watchContract.sourceCode);
            return createSuccessStatus(contract);
        } catch (Exception e) {
            log.warn("Contract's source uploading error: ", e);
            return createErrorStatus(e.getMessage());
        }
    }

    @RequestMapping("/contracts/list")
    public List<ContractInfoDTO> getContracts() {
        return contractsService.getContracts();
    }

    @RequestMapping(value = "/contracts/{address}/delete", method = RequestMethod.POST)
    public boolean stopWatchingContract(@PathVariable String address) {
        return contractsService.deleteContract(address);
    }

    @RequestMapping(value = "/contracts/{address}/files", method = RequestMethod.POST)
    public ActionStatus<ContractInfoDTO> uploadContractFiles(
            @PathVariable String address,
            @RequestParam MultipartFile[] contracts,
            @RequestParam(required = false) String verifyRlp) {

        try {
            ContractInfoDTO contract = contractsService.uploadContract(lowerCase(address), contracts);
            log.info("Uploaded files for address: {}, contract name: {}" + address, contract.getName());
            return createSuccessStatus(contract);
        } catch (Exception e) {
            log.warn("Contract's source uploading error: ", e);
            return createErrorStatus(e.getMessage());
        }
    }

    @RequestMapping(value = "/contracts/{address}/importFromExplorer", method = RequestMethod.POST)
    public ActionStatus<Boolean> importContractDataFromExplorer(@PathVariable String address) {
        try {
            final boolean result = contractsService.importContractFromExplorer(address);
            return createSuccessStatus(result);
        } catch (Exception e) {
            return createErrorStatus(e.getMessage());
        }
    }

    @RequestMapping(value = "/contracts/{address}/clearContractStorage", method = RequestMethod.POST)
    public ActionStatus<Boolean> clearStorage(@PathVariable String address) {
        try {
            contractsService.clearContractStorage(address);
            return createSuccessStatus();
        } catch (Exception e) {
            return createErrorStatus(e.getMessage());
        }
    }

    @RequestMapping("/contracts/indexStatus")
    public ActionStatus<IndexStatusDTO> getIndexStatus() {
        try {
            IndexStatusDTO result = contractsService.getIndexStatus();
            return createSuccessStatus(result);
        } catch (Exception e) {
            log.warn("Contract's index status error: ", e);
            return createErrorStatus(e.getMessage());
        }
    }

    private static class WatchContractDTO {

        public String address;

        public String sourceCode;

    }
    
    //----------Block Shine APIs------------
	
	@RequestMapping(value = "/contract/compile", method = RequestMethod.POST)
	@ResponseBody
    public BShineResponse compileContract(String request) throws Exception {
        return contractsService.compileContract(request);
    }
	
	@RequestMapping(value = "/contract/deploy", method = RequestMethod.POST)
	@ResponseBody
    public BShineResponse deployContract(CallArguments request) throws Exception {
		return contractsService.deployContract(request);
	}
	
	@RequestMapping(value = "/contract/invoke", method = RequestMethod.POST)
	@ResponseBody
    public BShineResponse invokeContract(InvokeContractRequest request) throws Exception {
		return contractsService.invokeContract(request);
	}
	
	@RequestMapping(value = "/contract/address", method = RequestMethod.GET)
	@ResponseBody
    public BShineResponse queryContractAddress(String txHash) throws Exception {
		return contractsService.queryInvokeContractResult(txHash);
	}
	
	@RequestMapping(value = "/contract/invoke/result", method = RequestMethod.GET)
	@ResponseBody
    public BShineResponse queryInvokeContractResult(String txHash) throws Exception {
		return contractsService.queryInvokeContractResult(txHash);
	}
}
