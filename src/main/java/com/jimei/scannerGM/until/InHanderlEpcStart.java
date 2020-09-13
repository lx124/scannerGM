package com.jimei.scannerGM.until;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;

/**
 * 6c标签上传事件实现类 监听状态开始类
 * 
 * @author lixin
 *
 */
public class InHanderlEpcStart implements HandlerTagEpcLog {
	private final static Logger log = LoggerFactory.getLogger(InHanderlEpcStart.class);

//	private static String txtPath;//路径
	private static Integer count = 0;// 计数
//	private static boolean redCode = false;// 是否红外
	private static Long start;//开始时间戳 ms
	public static volatile List<LogBaseEpcInfo> logs = new ArrayList<LogBaseEpcInfo>();
	private LogBaseEpcInfo logBaseEpcInfo;
	//检测恢复指令 是否恢复状态 恢复指令为null则是开始，不为null就是恢复
	public InHanderlEpcStart(String recoveryCode) {
//		this.txtPath = txtPath;
//		this.redCode = redCode;
		if(recoveryCode != null) {
//			System.out.println();
//			this.logs = InventoryTagFileIO.getLogs(txtPath);
//			System.out.println("logs长度=="+logs.size());
		}
	}
	/**
	 * 标签上报事件 执行体处于监听状态时检测到标签则执行 相当于线程的run方法
	 */
	/**
	 * 标签上报事件 执行体处于监听状态时检测到标签则执行 相当于线程的run方法
	 */
	public void log(String arg0, LogBaseEpcInfo logBaseEpcInfo) {		
		System.out.println("执行了" + count + "次");
//		System.out.println("redcode:"+AllConfig.redCode);
//		if(this.count == 0) {
//			this.logs = InventoryTagFileIO.getLogs(txtPath);
//		}
//		if(count == 0) {
//			if(logs!=null) {logs.clear();}
//			logs = new ArrayList<LogBaseEpcInfo>();
////			start = System.currentTimeMillis();
//		}
//		log.info("执行了" + count + "次");
		
		if (logBaseEpcInfo != null) {
//			System.out.println("logBaseEpcInfo=="+logBaseEpcInfo);
//			System.out.println("tid=="+logBaseEpcInfo.getTid());
//			if(logBaseEpcInfo.getTid() == null) {
//				AllConfig.redCode = true;
//			}
			//测试 为了让epc不重复
//			logBaseEpcInfo.setEpc(logBaseEpcInfo.getEpc()+CommonUtils.getRandom(3));
			//测试 为了让tid不重复
//			logBaseEpcInfo.setTid(logBaseEpcInfo.getTid()+CommonUtils.getRandom(3));
			//去重
			if (logs.size() > 0) {
//				boolean isRepeat = AllConfig.redCode ? isRepeatRed(logBaseEpcInfo) : isRepeat(logBaseEpcInfo);
				boolean isRepeat = isRepeatTid(logBaseEpcInfo);			
				log.info("去重=="+isRepeat);
				if(!isRepeat) {
					logs.add(logBaseEpcInfo);
					//写文件
//					InventoryTagFileIO.writeTxt(logBaseEpcInfo,txtPath);
				}
			} else {
				System.out.println("初始化加入logs");
				logs.add(logBaseEpcInfo);
//				InventoryTagFileIO.writeTxt(logBaseEpcInfo,txtPath);
			}
		}
		count++;
//		log.info("logs长度:"+logs.size() + " ");
//		log.info("执行完毕");
		System.out.println("执行完毕");
//		System.out.println("logs"+logs.toString());
		System.out.println("logs长度=="+logs.size());
	}
	/**
	 * 去重处理 tid
	 * @param logBaseEpcInfo
	 * @return boolean 重复为true
	 * lixin 2020年5月15日上午9:14:34
	 */
	private static boolean isRepeatTid(LogBaseEpcInfo logBaseEpcInfo) {
		for (int i = 0; i < logs.size(); i++) {
//			 else {
				if(logs.get(i).getTid().equals(logBaseEpcInfo.getTid())) {
					return true;
				} 
//			}
		}
		return false;
	}
//	/**
//	 ** 去重处理 红外盘点 采用epc
//	 * @param logBaseEpcInfo
//	 * @return boolean
//	 * lixin 2020年6月3日上午11:27:23
//	 */
//	private static boolean isRepeatEpc(LogBaseEpcInfo logBaseEpcInfo) {
//		for (int i = 0; i < logs.size(); i++) {
//			
//			if(logs.get(i).getEpc().equals(logBaseEpcInfo.getEpc())) {
//				return true;
//			} 
//		}
//		return false;
//	}
	public static Integer getCount() {
		return count;
	}

	public static void setCount(Integer count) {
		InHanderlEpcStart.count = count;
	}
	public LogBaseEpcInfo getLogBaseEpcInfo() {
		return logBaseEpcInfo;
	}
	public void setLogBaseEpcInfo(LogBaseEpcInfo logBaseEpcInfo) {
		this.logBaseEpcInfo = logBaseEpcInfo;
	}
}
