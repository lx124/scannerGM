package com.jimei.scannerGM.until;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;

import net.sf.json.JSONObject;

/**
 * 通用工具类
 * 
 * @author lixin
 *
 */
public class CommonUtils {
	private final static Logger log = LoggerFactory.getLogger(CommonUtils.class);
	private static final String tidData = CommonUtils.getProperties("RFIDConfig.properties", "AccessDoorIOFilePath");// 通道门每次出入数据(tid)存放文件位置

	// 计算pc值
	public static String getPc(int pcLen) {
		int iPc = pcLen << 11;
		BitBuffer buffer = BitBuffer.allocateDynamic();
		buffer.put(iPc);
		buffer.position(16);
		byte[] bTmp = new byte[2];
		buffer.get(bTmp);
		return HexUtils.bytes2HexString(bTmp);
	}

	// 写入数据不足4位后面补'0' AA00
	public static String padLeft(String src, int len, char ch) {
		int diff = len - src.length();
		if (diff <= 0) {
			return src;
		}

		char[] chars = new char[len];
		System.arraycopy(src.toCharArray(), 0, chars, 0, src.length());
		for (int i = src.length(); i < len; i++) {
			chars[i] = ch;
		}
		return new String(chars);
	}

	public static int getValueLen(String data) {
		data = data.trim();
		return data.length() % 4 == 0 ? data.length() / 4 : (data.length() / 4) + 1;
	}

	/**
	 * 获取Properties文件中数据
	 *
	 * @param filePath 资源文件路径
	 * @param key      根据key取value
	 * @return value 字符串类型
	 */
	public static String getProperties(String filePath, String key) {
		Properties prop = null;
		String value = null;
		try {
			// 通过Spring中的PropertiesLoaderUtils工具类进行获取
			prop = PropertiesLoaderUtils.loadAllProperties(filePath);
			// 根据关键字查询相应的值
			value = prop.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 生成指定位数的随机数
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandom(int length) {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			val += String.valueOf(random.nextInt(10));
		}
		return val;
	}

	/**
	 * 把字符串按照指定编码转成16进制字符串 采用utf-8
	 * 
	 * @param source 源字符串数据
	 * @return String 16进制数据 lixin 2020年4月29日上午10:59:56
	 */
	public static String encodeStringToHex(String source) throws Exception {
		String encodeStr = "";
//		try {
		byte[] sourceStrs = source.getBytes("utf-8");
		for (byte b : sourceStrs) {
			encodeStr += Integer.toHexString(b & 0xff);
			// System.out.println(encodeStr);
		}
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return encodeStr;
	}

	/**
	 * 获取访问ip
	 * 
	 * @param request
	 * @return String lixin 2020年6月30日下午7:10:29
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		try {
			ipAddress = request.getHeader("x-forwarded-for");
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
				if (ipAddress.equals("127.0.0.1")) {
					ipAddress = getHostAddress();
				}
			}
			// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
			if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length() == 15
				if (ipAddress.indexOf(",") > 0) {
					ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
				}
			}

			// 解决请求和响应的IP一致且通过浏览器请求时，request.getRemoteAddr()为"0:0:0:0:0:0:0:1"
			if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
				ipAddress = getHostAddress();
			}
		} catch (Exception e) {
			ipAddress = "";
		}

		return ipAddress;
	}

	private static String getHostAddress() throws UnknownHostException {
		// 根据网卡取本机配置的IP
		InetAddress inet = null;
		inet = InetAddress.getLocalHost();
		return inet.getHostAddress();
	}

	/**
	 * 十六进制转字节数组
	 * 
	 * @param src
	 * @return byte[] lixin 2020年4月29日下午1:38:10
	 */
	public static byte[] hexString2Bytes(String src) {
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
		}
		return ret;
	}

	/**
	 * 把16进制数据转化成源数据字符串 utf-8 用于读取数据
	 * 
	 * @param HexData 16进制数据
	 * @return String lixin 2020年4月29日下午1:12:53
	 * @throws UnsupportedEncodingException
	 */
	public static String decodeHexToString(String HexData) throws UnsupportedEncodingException {
		String sourceStr = "";
		sourceStr = new String(hexString2Bytes(HexData), "utf-8");
		return sourceStr;
	}
	/**
	 * 把16进制数据按照指定编码转化成源数据字符串 用于读取数据
	 * 
	 * @param HexData 16进制数据
	 * @return String lixin 2020年4月29日下午1:12:53
	 * @throws UnsupportedEncodingException
	 */
	public static String decodeHexToString(String HexData,String charName) throws UnsupportedEncodingException {
		String sourceStr = "";
		sourceStr = new String(hexString2Bytes(HexData), charName);
		return sourceStr;
	}

	/**
	 * lixin 获取本机ip 2020 05 21 15:26:23
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getLocalIp() throws UnknownHostException {
		// 获取ip
		InetAddress ip4 = null;
		ip4 = Inet4Address.getLocalHost();
		String ip = ip4.getHostAddress();
		return ip;
	}

	/**
	 * 字节数组转16进制 无空格
	 * 
	 * @param bytes 需要转换的byte数组
	 * @return 转换后的Hex字符串
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() < 2) {
				sb.append(0);
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 传入需要连接的IP，返回是否连接成功
	 * 
	 * @param remoteInetAddr
	 * @return
	 */
	public static boolean isReachable(String remoteInetAddr) {
		boolean reachable = false;
		try {
			InetAddress address = InetAddress.getByName(remoteInetAddr);
			reachable = address.isReachable(5000);
		} catch (Exception e) {
			return false;
		}
		return reachable;
	}

	/**
	 * 功能描述: inputStream转化为字节数组
	 * 
	 * @param inputStream 输入流
	 * @return byte[] 数组
	 * @author xiaobu
	 * @date 2019/3/28 16:03
	 * @version 1.0
	 */
	public static byte[] inputStream2byte(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inputStream.read(buff, 0, 100)) > 0) {
			byteArrayOutputStream.write(buff, 0, rc);
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * 使用URLConnection调用http协议接口 post 超时时间30s
	 * 
	 * @param params
	 * @param URL
	 * @return String lixin 2020年6月19日下午2:04:55
	 */
	public static String doHttpPostPF(JSONObject params, String URL) {

		log.info("URLConnection调用,数据:" + params);
		log.info("URLConnection调用,url:" + URL);
		InputStream instr = null;
		OutputStream out = null;
		try {
			java.net.URL url = new URL(URL);
			URLConnection urlCon = url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setConnectTimeout(30000);// 设置超时时间
			// 数据格式 application/json
			urlCon.setRequestProperty("content-Type", "application/json");
			urlCon.setRequestProperty("charset", "utf-8");
			out = new DataOutputStream(urlCon.getOutputStream());
			// 写入请求的字符串  
			out.write((params.toString()).getBytes());
			out.flush();
			out.close();
			instr = urlCon.getInputStream();
			byte[] bis = inputStream2byte(instr);
			String ResponseString = new String(bis, "UTF-8");
			if ((ResponseString == null) || ("".equals(ResponseString.trim()))) {
				log.info("URLConnection调用结果返回空");
			}
			log.info("URLConnection返回数据为:" + ResponseString);
//			System.out.println("返回数据==" + ResponseString);
			return ResponseString;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return JSONObject.fromObject(Result.fail("上位机程序异常" + e.getMessage())).toString();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (instr != null) {
					instr.close();
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				return JSONObject.fromObject(Result.fail("上位机程序异常" + ex.getMessage())).toString();
			}
		}
	}

	/**
	 * 校验是否可以连通
	 * 
	 * @param ip
	 * @return true/false
	 */
	public static boolean isConnect(String ip) {
		boolean connect = false;
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec("ping " + ip);
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			isr.close();
			br.close();

			if (null != sb && !sb.toString().equals("")) {
				String logString = "";
				if (sb.toString().indexOf("TTL") > 0) {
					// 网络畅通
					connect = true;
				} else {
					// 网络不畅通
					connect = false;
				}
			}
		} catch (IOException e) {
			return false;
		}
		return connect;
	}

	/**
	 * Hashtable<String, Integer>的json字符串 转化Hashtable<String, Integer>
	 * 
	 * @param objHash
	 * @return Hashtable<String,String> lixin 2020年7月11日下午2:56:45
	 */
	public static Hashtable<String, Integer> getHashTableSIByJson(JSONObject objHash) {
		System.out.println("进来参数:" + objHash);
		Hashtable<String, Integer> result = new Hashtable<String, Integer>();
		Iterator<String> iterator = objHash.keys();
		String key = null;
		Integer value = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = Integer.parseInt(objHash.getString(key));
			result.put(key, value);
		}
		return result;
	}
	/**
	 * ip格式验证
	 * @param ip
	 * @return boolean
	 * lixin 2020年8月12日上午10:01:31
	 */
	public static boolean ipMatches(String ip) {
		if (ip != null && !ip.isEmpty()) {
			// 定义正则表达式
			String regex = "^(1\\d{2}|2[0-4 ]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
					+ "(1\\d{2}|2[0-4]\\ d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\ d|25[0-5]|[1-9]\\d|\\d)$";
			// 判断ip地址是否与正则表达式匹配
			if (ip.matches(regex)) {
				// 返回判断信息
//	                return text + "\n是一个合法的IP地址！"; 
				return true;
			} else {
				// 返回判断信息
				return false;
//	              return text + "\n不是一个合法的IP地址！";  
			}
		}
		return false;
	}

	// 返回判断信息
//	        return "请输入要验证的IP地址！";  
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	public static void main(String[] args) throws Exception {
//		boolean a = ipMatches("192.256.0.3");
//		System.out.println(a);
//
//		boolean b = isInteger("66666");
//		System.out.println(b);
		//
//		URL url = null;
//        Boolean bon = false;
//        try {
//        url = new URL("http://baicu.com/");
//        InputStream in = url.openStream();//打开到此 URL 的连接并返回一个用于从该连接读入的 InputStream
//        System.out.println("连接正常");
//        in.close();//关闭此输入流并释放与该流关联的所有系统资源。
//        } catch (IOException e) {
//        System.out.println("无法连接到：" + url.toString());
//        }
//        bon = isReachable("192.168.0.251");
//        System.out.println("pingIP：" + bon);
       String a = encodeStringToHex("123");
       System.out.println(a);
       String bb = decodeHexToString(a,"utf-8");
       System.out.println(bb);
//		InOutParameter inOutParameter = new InOutParameter();
//		List<InOutParameter> inList = new ArrayList<InOutParameter>();
//		List<String> refs = new ArrayList<String>();
//		List<String> userId = new ArrayList<String>();
//		refs.add("123456");
//		refs.add("445566");
//		userId.add("abcde");
//		userId.add("abqwer");
//		inOutParameter.setTaskName("task1");
//		inOutParameter.setReferenceNum(refs);
//		inOutParameter.setUserId(userId);
//		inList.add(inOutParameter);
//		inOutParameter = new InOutParameter();
//		List<String> refs1 = new ArrayList<String>();
//		List<String> userId1 = new ArrayList<String>();
//		refs1.add("123457");
//		refs1.add("445567");
//		userId1.add("abcdr");
//		userId1.add("abqwerr");
//		inOutParameter.setTaskName("task2");
//		inOutParameter.setReferenceNum(refs1);
//		inOutParameter.setUserId(userId1);
//		inList.add(inOutParameter);
//		AccessDoorFileIO.writeUserIdsBySqlite(inList);
//		encodeStringToHex
	}
}
