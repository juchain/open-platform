package com.blockshine.authentication.web;

import com.blockshine.authentication.domain.ApplicationDO;
import com.blockshine.authentication.service.ApplicationService;
import com.blockshine.common.util.PageUtils;
import com.blockshine.common.util.Query;
import com.blockshine.common.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-03-22 11:15:16
 */
 
@Controller
@RequestMapping("/application")
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;


	@GetMapping()
	String Application(){
	    return "application/application/application";
	}
	
	@ResponseBody
	@GetMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<ApplicationDO> applicationList = applicationService.list(query);
		int total = applicationService.count(query);
		PageUtils pageUtils = new PageUtils(applicationList, total);
		R r =R.ok();
		r.put("data",pageUtils);
		return r;
	}

	
	@GetMapping("/add")
	String add(){
	    return "application/application/add";
	}

	@GetMapping("/edit/{appId}")
	String edit(@PathVariable("appId") Long appId,Model model){
		ApplicationDO application = applicationService.get(appId);
		model.addAttribute("application", application);
	    return "application/application/edit";
	}
	
	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	public R save(@RequestBody ApplicationDO application){

		return applicationService.createApplication(application);
	}
	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	public R update(@RequestBody ApplicationDO application){
		applicationService.update(application);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/remove")
	@ResponseBody
	public R remove( Long appId){
		if(applicationService.remove(appId)>0){
		return R.ok();
		}
		return R.error();
	}
	
	/**
	 * 删除
	 */
	@PostMapping( "/batchRemove")
	@ResponseBody
	public R remove(@RequestParam("ids[]") Long[] appIds){
		applicationService.batchRemove(appIds);
		return R.ok();
	}



	
}
