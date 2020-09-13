package com.jimei.scannerGM.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gg.reader.api.dal.GClient;
import com.jimei.scannerGM.until.Result;
/**
 * 盘点机 业务接口
 * @author lixin
 *
 */
@Service
public interface InventoryService {

	/**
	 * 建立连接 进行读取 为winfrom服务
	 * void
	 * lixin
	 * 2020年4月24日下午4:41:25
	 */
	public Result startInventoryForCS(GClient client,String conString);
	/**
	 * 读取数据 为winfrom服务
	 * void
	 * lixin
	 * 2020年4月24日下午4:41:25
	 */
	public Result readDataForCS(GClient client);
	/**
	 * 建立连接 红外  为winfrom服务
	 * @param client
	 * @return
	 */
	Result startConRead(GClient client,String conString);
}
