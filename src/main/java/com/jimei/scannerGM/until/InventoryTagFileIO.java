package com.jimei.scannerGM.until;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;

/**
 * 盘点 RFID标签信息的文件输入/输出管理类
 * 
 * @author lixin
 *
 */
public class InventoryTagFileIO {
	private static String txtPath;// 盘点文档路径
	public static PrintWriter printWriter = null;
	/**
	 * 盘点 往指定txt文档写标签
	 * 
	 * @param logBaseEpcInfo void lixin 2020年4月26日上午9:06:03
	 */
	public static void writeTxt(LogBaseEpcInfo logBaseEpcInfo,String txtPath) {
		setTxtPath(txtPath);
		if (InHanderlEpcStart.getCount() == 0) writeConnection();
		if (printWriter != null) {
//			System.out.println("在写");
			printWriter.println("start");
			printWriter.println("epc/" + logBaseEpcInfo.getEpc());
//			printWriter.println("bepc/"+Arrays.toString(logBaseEpcInfo.getbEpc()));
			printWriter.println("user/" + logBaseEpcInfo.getUserdata());
//			printWriter.println("buser,"+logBaseEpcInfo.getbUser());
			printWriter.println("tid/" + logBaseEpcInfo.getTid());
//			printWriter.println("btid,"+logBaseEpcInfo.getbTid());
			printWriter.println("end");
		}
	}

	/**
	 * 关闭打印输出流的连接 void lixin 2020年4月26日上午10:46:56
	 */
	public static void closeWrite() {
		printWriter.close();
		System.out.println("PrintWriter close");
	}

	/**
	 * 建立输出流 连接标签信息的输出位置
	 * 
	 * @return PrintWriter lixin 2020年4月26日上午10:47:18
	 */
	private static PrintWriter writeConnection() {
//		System.out.println("io路径==="+txtPath);
		try {
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(txtPath,true), "utf-8"), true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return printWriter;
	}

	public static String getTxtPath() {
		return txtPath;
	}

	public static void setTxtPath(String txtPath) {
		InventoryTagFileIO.txtPath = txtPath;
	}
}
