package com.jimei.scannerGM.until;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.MsgAppGetReaderInfo;
import com.gg.reader.api.protocol.gx.MsgAppGetSerialParam;
import com.gg.reader.api.protocol.gx.MsgAppSetGpo;
import com.gg.reader.api.protocol.gx.MsgAppSetSerialParam;
import com.gg.reader.api.protocol.gx.MsgBaseGetBaseband;
import com.gg.reader.api.protocol.gx.MsgBaseGetCapabilities;
import com.gg.reader.api.protocol.gx.MsgBaseGetFreqRange;
import com.gg.reader.api.protocol.gx.MsgBaseGetPower;
import com.gg.reader.api.protocol.gx.MsgBaseGetTagLog;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseSetFreqRange;
import com.gg.reader.api.protocol.gx.MsgBaseSetPower;
import com.gg.reader.api.protocol.gx.MsgBaseSetTagLog;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.MsgBaseWriteEpc;
import com.gg.reader.api.protocol.gx.ParamEpcFilter;
import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;

import ch.qos.logback.core.net.server.Client;

/**
 ** 关于rfid的管理的参数配置
 * 
 * @author lixin
 *
 */
public class RFIDManager {
	private static MsgBaseInventoryEpc msgBaseInventoryEpc;
	private static Logger log = LoggerFactory.getLogger(RFIDManager.class);
	public static long alarmTime = 1000;// 报警时间

	/**
	 * 建立RS232连接
	 * 
	 * @param client
	 * @param conString
	 * @param timeout
	 * @returnGClient lixin 2020年4月24日下午4:29:23
	 */
	public static boolean RS232Connection(GClient client, String conString, int timeout) {
		return client.openSerial(conString, timeout);
	}

	/**
	 * 建立TCP连接
	 * 
	 * @param client
	 *            连接对象
	 * @param conString
	 *            连接字符串
	 * @param timeout
	 *            超时时间
	 * @returnGClient lixin 2020年4月24日下午4:29:29
	 */
	public static boolean TCPConnection(GClient client, String conString, int timeout) {
		return client.openTcp(conString, timeout);
	}

	/**
	 ** 关闭连接
	 * 
	 * @param client
	 *            void lixin 2020年4月24日下午4:31:13
	 */
	public static void closeGClient(GClient client) {
		client.close();
	}

	/**
	 * 过滤时间参数配置 10ms单位
	 * 
	 * @param client
	 * @param time
	 * @return boolean lixin 2020年4月24日下午4:37:32
	 */
	public boolean tagTime(GClient client, int time) {
		MsgBaseSetTagLog log = new MsgBaseSetTagLog();
		log.setRepeatedTime(time);
		client.sendSynMsg(log);
		if (log.getRtCode() == 0) {
			return true;
		} else {
			System.out.println(log.getRtMsg());
			return false;
		}
	}

	/**
	 * 发送读取6c标签的最终指令
	 * 
	 * @param client
	 * @return void lixin 2020年4月24日下午4:33:21
	 */
	public static void sendMsg(GClient client) {
		client.sendSynMsg(msgBaseInventoryEpc);
		if (0x00 == msgBaseInventoryEpc.getRtCode()) {
			System.out.println("MsgBaseInventoryEpc[OK].");
		} else {
			System.out.println(msgBaseInventoryEpc.getRtMsg());
		}
	}

	
	/**
	 ** 定义读取哪些信息6c 开启标签上报事件 ，默认epc 10字 用户区20字 保留区4字
	 * 
	 * @param client
	 * @param txtPath
	 * @param recoveryCode
	 * @return MsgBaseInventoryEpc lixin 2020年4月30日下午3:38:20
	 */
	public static MsgBaseInventoryEpc read6c(GClient client, String txtPath, String recoveryCode) {
		InHanderlEpcStart epcStart = new InHanderlEpcStart(recoveryCode);
		client.onTagEpcLog = epcStart;
		client.onTagEpcOver = new InHanderlEpcEnd();
		MsgBaseInventoryEpc msgBaseInventoryEpc = new MsgBaseInventoryEpc();
		// msgBaseInventoryEpc.setAntennaEnable(EnumG.AntennaNo_1 | EnumG.AntennaNo_2);
		msgBaseInventoryEpc.setAntennaEnable(EnumG.AntennaNo_1);
		msgBaseInventoryEpc.setInventoryMode(EnumG.InventoryMode_Inventory);
		// msgBaseInventoryEpc.setInventoryMode(EnumG.InventoryMode_Single);
		ParamEpcReadTid tid = new ParamEpcReadTid();
		tid.setMode(EnumG.ParamTidMode_Auto);
		tid.setLen(10);// 读epc区
		msgBaseInventoryEpc.setReadTid(tid);
		// 读用户区
		ParamEpcReadUserdata user = new ParamEpcReadUserdata();
		user.setStart(0);
		user.setLen(20);
		msgBaseInventoryEpc.setReadUserdata(user);
		// 读保留区
		ParamEpcReadReserved reserved = new ParamEpcReadReserved();
		reserved.setStart(0);
		reserved.setLen(4);
		msgBaseInventoryEpc.setReadReserved(reserved);
		RFIDManager.setMsgBaseInventoryEpc(msgBaseInventoryEpc);
		return msgBaseInventoryEpc;
	}
	
	/**
	 ** 定义读取哪些信息6c 开启标签上报事件 ，默认epc 10字 用户区20字 保留区4字
	 *  为winfrom服务
	 * @param client
	 * @param txtPath
	 * @param recoveryCode
	 * @return MsgBaseInventoryEpc lixin 2020年4月30日下午3:38:20
	 */
	public static MsgBaseInventoryEpc read6cForCS(GClient client,String recoveryCode) {
		InHanderlEpcStart epcStart = new InHanderlEpcStart(recoveryCode);
		client.onTagEpcLog = epcStart;
		client.onTagEpcOver = new InHanderlEpcEnd();
		MsgBaseInventoryEpc msgBaseInventoryEpc = new MsgBaseInventoryEpc();
		// msgBaseInventoryEpc.setAntennaEnable(EnumG.AntennaNo_1 | EnumG.AntennaNo_2);
		msgBaseInventoryEpc.setAntennaEnable(EnumG.AntennaNo_1);
		msgBaseInventoryEpc.setInventoryMode(EnumG.InventoryMode_Inventory);
		// msgBaseInventoryEpc.setInventoryMode(EnumG.InventoryMode_Single);
		ParamEpcReadTid tid = new ParamEpcReadTid();
		tid.setMode(EnumG.ParamTidMode_Auto);// tid读取长度自适应 最长不超过6word
		tid.setLen(6);
		msgBaseInventoryEpc.setReadTid(tid);
		// 读用户区
		ParamEpcReadUserdata user = new ParamEpcReadUserdata();
		user.setStart(0);
		user.setLen(20);
		msgBaseInventoryEpc.setReadUserdata(user);
		// 读保留区
		ParamEpcReadReserved reserved = new ParamEpcReadReserved();
		reserved.setStart(0);
		reserved.setLen(4);
		msgBaseInventoryEpc.setReadReserved(reserved);
		RFIDManager.setMsgBaseInventoryEpc(msgBaseInventoryEpc);
		return msgBaseInventoryEpc;
	}
	/**
	 * 停止读取
	 * 
	 * @param client
	 * @return boolean lixin 2020年4月24日下午4:30:46
	 */
	public static boolean sendStopRead(GClient client) {
		MsgBaseStop stopMsg = new MsgBaseStop();
		client.sendSynMsg(stopMsg);
		if (0x00 == stopMsg.getRtCode()) {
			System.out.println("MsgBaseStop Success");
			return true;
		} else {
			System.out.println("MsgBaseStop Fail");
			return false;
		}
	}

	/**
	 ** 往epc写数据 通过tid
	 * 
	 * @param client
	 *            连接对象
	 * @param epcData
	 *            16进制epc数据
	 * @param tid
	 *            tid数据，为null则不指定标签
	 * @return boolean lixin 2020年4月30日下午3:50:51
	 */
	private static boolean writeEpc(GClient client, String epcData, String tid) {
		MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
		msg.setAntennaEnable(EnumG.AntennaNo_1 | EnumG.AntennaNo_2);
		msg.setStart(1);// word
		msg.setArea(EnumG.WriteArea_Epc);
		String sWriteHexData = epcData; // 写入数据（16进制）
		int iWordLen = CommonUtils.getValueLen(sWriteHexData);
		sWriteHexData = CommonUtils.getPc(iWordLen)
				+ CommonUtils.padLeft(sWriteHexData.toUpperCase(), 4 * iWordLen, '0'); // PC值+数据内容
		msg.setHexWriteData(sWriteHexData);
		// 匹配参数 匹配pid 如果tid为null，则不匹配，默认写信号强度最好的
		if (tid != null) {
			ParamEpcFilter filter = new ParamEpcFilter();
			filter.setArea(EnumG.ParamFilterArea_TID);
			filter.setHexData(tid);
			filter.setBitStart(0);
			filter.setBitLength(tid.length() * 4);
			msg.setFilter(filter);
		}
		client.sendSynMsg(msg);
		if (0 == msg.getRtCode()) {
			System.out.println("Write successful.");
			return true;
		} else {
			System.out.println(msg.getRtMsg());
			return false;
		}
	}

	/**
	 * 通道门是否报警 lixin 2020 05 13：59：20
	 * 
	 * @param Integer
	 *            alarmCode 1报警;0不报警
	 * @param GClient
	 *            client
	 */
	public static void accessDoorIsAlarm(GClient client, int alarmCode) {
		System.out.println("报警参数==" + alarmCode);
		log.info("报警client=" + client);
		// gpo1报警红灯 gpo2不报警绿灯
		MsgAppSetGpo msg = new MsgAppSetGpo();
		if (alarmCode == 0) {// 不报警
			// System.out.println("不报警");
			msg.setGpo2(1);// gpo1
			client.sendSynMsg(msg);
			try {
				Thread.sleep(alarmTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			msg.setGpo2(0);// gpo1
			client.sendSynMsg(msg);
		} else {
			// System.out.println("开始报警");
			msg.setGpo1(1);// gpo1
			client.sendSynMsg(msg);
			try {
				Thread.sleep(alarmTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			msg.setGpo1(0);// gpo1
			client.sendSynMsg(msg);
		}
		if (0x00 == msg.getRtCode()) {
			log.info("报警结果:Set gpo success");
			// System.out.println("Set gpo success");
		} else {
			log.info("报警失败:" + msg.getRtMsg());
			// System.out.println(msg.getRtMsg());
		}
	}

	
	
	
	
	
	public static MsgBaseInventoryEpc getMsgBaseInventoryEpc() {
		return msgBaseInventoryEpc;
	}

	public static void setMsgBaseInventoryEpc(MsgBaseInventoryEpc msgBaseInventoryEpc) {
		RFIDManager.msgBaseInventoryEpc = msgBaseInventoryEpc;
	}

	public static void main(String[] args) {
		// GClient client = new GClient();
		// if(client.openSerial(ConnectedConstant.DesktopPlatform_RS232, 2000)) {
		// Scanner sc =new Scanner(System.in);
		// System.out.println("请输入开始写入");
		// sc.hasNext();
		// writeEpc(client, "ABCD1234ABCD3333", "E200600418568A6D");
		// sendStopRead(client);
		// closeGClient(client);
		// }
//		TestUntil until = new TestUntil();
//		System.out.println(until.toString());
	}
}
