package com.jimei.scannerGM.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.jimei.scannerGM.annotation.Log;
import com.jimei.scannerGM.model.LabelSimple;
import com.jimei.scannerGM.service.InventoryService;
import com.jimei.scannerGM.until.CommonUtils;
import com.jimei.scannerGM.until.InHanderlEpcStart;
import com.jimei.scannerGM.until.RFIDManager;
import com.jimei.scannerGM.until.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 盘点机控制器
 * 
 * @author lixin
 *
 */
@Api(tags = "盘点机扫描枪助手管理")
@RestController
@RequestMapping("/inventory")
public class InventoryController {
	private static boolean isSuspend = false;// 是否暂停
	private static final String inventoryPath = CommonUtils.getProperties("RFIDConfig.properties", "InventoryFile");
	private final static Logger log = LoggerFactory.getLogger(InventoryController.class);
	@Autowired
	private InventoryService inventoryService;
	// 连接对象
	public static GClient client = null;

//	//是否红外盘点
//	private static boolean redCode = false;

	/**
	 ** 建立连接 进行准备红外读取 for CS
	 * 
	 * @return ResultManager lixin 2020年4月24日下午3:58:35
	 */
	@ApiOperation(value = "建立连接红外读取", notes = "建立连接")
	@GetMapping("/con/openRed")
	public Result startConRed() {
		if(client != null) {
			Result.fail("当前连接未断开，不能建立!");
		}
		client = new GClient();
		String conString = "COM1:115200";
		Result result = inventoryService.startConRead(client, conString);
		if(!result.isResult()) {
			client = null;
		}
		return result;
	}
	/**
	 ** 建立连接 进行读取 for CS
	 * 
	 * @return ResultManager lixin 2020年4月24日下午3:58:35
	 */
	@ApiOperation(value = "建立连接", notes = "建立连接")
	@GetMapping("/con/open")
	public Result startConRead() {
		if(client != null) {
			Result.fail("当前连接未断开，不能建立!");
		}
		client = new GClient();
		String conString = "COM1:115200";
		Result result = inventoryService.startInventoryForCS(client, conString);
		if(!result.isResult()) {
			client = null;
		}
		return result;
	}
	/**
	 ** 读取数据 并清空数据 for CS
	 * 
	 * @return ResultManager lixin 2020年4月24日下午3:58:35
	 */
	@ApiOperation(value = "读取数据", notes = "读取数据")
	@GetMapping("/data/read/rfid")
	public Result readDataForCS() {
		Long start = System.currentTimeMillis();	
		if(client == null) {
			return Result.fail("连接断开，读取失败");
		}
		Result result = inventoryService.readDataForCS(client);
		System.out.println("耗时:"+(System.currentTimeMillis() - start));
		return result;
	}
	/**
	 ** 读取数据 并清空数据 for CS
	 * 
	 * @return ResultManager lixin 2020年4月24日下午3:58:35
	 */
	@ApiOperation(value = "读取数据", notes = "读取数据")
	@GetMapping("/data/read")
	public Result readDataForCS2() {
		if(client == null) {
			return Result.fail("连接断开，读取失败");
		}
		Long start = System.currentTimeMillis();	
		List<LogBaseEpcInfo> logs = null;
		if(InHanderlEpcStart.logs !=null && InHanderlEpcStart.logs.size() > 0) {
			logs = InHanderlEpcStart.logs;
			List<LabelSimple> datas = new ArrayList<LabelSimple>();
			for (LogBaseEpcInfo log : logs) {
				LabelSimple data = new LabelSimple();
				data.setTid(log.getTid());
				data.setEpc(log.getEpc());
//				data.setUser(log.getUserdata());
				datas.add(data);
			}
			InHanderlEpcStart.setCount(0);
			InHanderlEpcStart.logs.clear();
			InHanderlEpcStart.logs = new ArrayList<LogBaseEpcInfo>();
			System.out.println("耗时:"+(System.currentTimeMillis() - start));
			return Result.ok(datas);			
		} else {
			Result.fail("没有检测到数据!");
		}
		return Result.fail("没有检测到数据!");
		
	}
	/**
	 ** 断开连接 for CS
	 * 
	 * @return ResultManager lixin 2020年4月24日下午3:58:35
	 */
	@ApiOperation(value = "断开连接", notes = "断开连接")
	@GetMapping("/con/close")
	public Result endInventory() {
		if(client == null) {
			Result.fail("当前连接已断开，不必再停止!");
		}
		//RFIDManager.sendStopRead(client);
		client.close();
		client = null;
		return Result.ok();
	}

	

	/**
	 * 获取连接状态 是否连接
	 * 
	 * @return Result lixin 2020年5月14日上午10:21:18
	 */
	@ApiOperation(value = "获取连接状态 是否连接", notes = "获取连接状态 是否连接")
	@GetMapping(value = "/connection")
	public Result getConnectStatus() {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		if (this.client == null) {
//			hm.put("status", false);
//			hm.put("code", 400);
//			hm.put("msg", "连接已断开");
			return Result.fail("连接已断开");
		} else {
//			hm.put("status", true);
//			hm.put("code", 200);
//			hm.put("msg", "正在连接中");
			return Result.ok("正在连接中");
		}
		
	}

	
}
