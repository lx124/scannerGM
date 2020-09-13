package com.jimei.scannerGM.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.jimei.scannerGM.model.LabelSimple;
import com.jimei.scannerGM.service.InventoryService;
import com.jimei.scannerGM.until.CommonUtils;
import com.jimei.scannerGM.until.InHanderlEpcEnd;
import com.jimei.scannerGM.until.InHanderlEpcStart;
import com.jimei.scannerGM.until.RFIDManager;
import com.jimei.scannerGM.until.Result;

import net.sf.json.JSONObject;
@Service
public class InventoryServiceImpl implements InventoryService {

	//盘点 winfrom
		@Override
		public Result startInventoryForCS(GClient client, String conString) {
				System.out.println("readerName="+conString);
//				if(RFIDManager.RS232Connection(client, ConnectedConstant.Invaentory_RS232, 2000)) {
				if(RFIDManager.RS232Connection(client,conString, 2000)) {
					MsgBaseStop msgBaseStop = new MsgBaseStop();
		            client.sendSynMsg(msgBaseStop);
		            if (0 == msgBaseStop.getRtCode()) {
		                System.out.println("Stop successful.");
		                RFIDManager.read6cForCS(client, null);
						RFIDManager.sendMsg(client);
		                return Result.ok("连接成功！开始检测!");
		            } else {
		                System.out.println("Stop error.");
		                client = null;
		                return Result.fail("RS232 连接失败！");
		            }				
				} else {
					return Result.fail("RS232 连接失败！");	
				}
		}
		// 读取数据 for CS
		@Override
		public Result readDataForCS(GClient client) {
			InHanderlEpcStart.setCount(0);
			if(InHanderlEpcStart.logs !=null)
				InHanderlEpcStart.logs.clear();
			// 先进行读取 是否有标签或者多个标签
			RFIDManager.read6cForCS(client, null);
			RFIDManager.sendMsg(client);
			try {
				Thread.sleep(200);// 200ms读取
			} catch (InterruptedException e) {
				e.printStackTrace();
//				log.error("servie延时250ms错误--异常:" + e.getMessage());
//				return Result.fail("服务器错误!!!");
			}
			RFIDManager.sendStopRead(client);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
//			List<LogBaseEpcInfo> logs = HanderlEpcStart.logs;
			List<LogBaseEpcInfo> logs = null;
			if(InHanderlEpcStart.logs !=null && InHanderlEpcStart.logs.size() > 0) {
				logs = InHanderlEpcStart.logs;
				List<LabelSimple> datas = new ArrayList<LabelSimple>();
				for (LogBaseEpcInfo log : logs) {
					LabelSimple data = new LabelSimple();
					data.setTid(log.getTid());
					data.setEpc(log.getEpc());
					data.setUser(log.getUserdata());
					datas.add(data);
				}
				return Result.ok(datas);			
			} else {
				Result.fail("没有检测到数据!");
			}
			return Result.fail("没有检测到数据!");
		}
	//建立连接 红外 for CS
		@Override
		public Result startConRead(GClient client,String conString) {
			System.out.println("readerName="+conString);
//			if(RFIDManager.RS232Connection(client, ConnectedConstant.Invaentory_RS232, 2000)) {
			if(RFIDManager.RS232Connection(client,conString, 2000)) {
				MsgBaseStop msgBaseStop = new MsgBaseStop();
	            client.sendSynMsg(msgBaseStop);
	            if (0 == msgBaseStop.getRtCode()) {
	                System.out.println("Stop successful.");
	                InHanderlEpcStart epcStart = new InHanderlEpcStart(null);
					client.onTagEpcLog = epcStart;
					client.onTagEpcOver = new InHanderlEpcEnd();
	                return Result.ok("连接成功！开始检测!");
	            } else {
	                System.out.println("Stop error.");
	                client = null;
	                return Result.fail("RS232 连接失败！");
	            }				
			} else {
				return Result.fail("RS232 连接失败！");	
			}
		}
}
