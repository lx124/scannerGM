package com.jimei.scannerGM.service.impl;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jimei.scannerGM.annotation.Log;
import com.jimei.scannerGM.model.LogEntity;
import com.jimei.scannerGM.service.SystemService;
import com.jimei.scannerGM.until.CommonUtils;

import net.sf.json.JSONObject;
/**
 * 系统业务层 实现
 * @author lixin
 *
 */
@Service
public class SystemServiceImpl implements SystemService {
	private static final Logger logger = LoggerFactory.getLogger(SystemServiceImpl.class);

	@Override
	public void logManager(JoinPoint joinPoint, Object res, long time) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		//HttpSession session = request.getSession();
		LogEntity logEntity = new LogEntity();
		// 请求的IP
//		String ip = request.getRemoteAddr();
		String ip = CommonUtils.getIpAddr(request);
		try {
			String targetName = joinPoint.getTarget().getClass().getName();
			String methodName = joinPoint.getSignature().getName();
			Object[] arguments = joinPoint.getArgs();
			Class targetClass = Class.forName(targetName);
			Method[] methods = targetClass.getMethods();
			String operationType = "";
			String operationName = "";
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					Class[] clazzs = method.getParameterTypes();
					if (clazzs.length == arguments.length) {
						operationType = method.getAnnotation(Log.class).operationType();
						operationName = method.getAnnotation(Log.class).operationName();
						break;
					}
				}
			}
			if(operationType.equals("查询日志内容")) {
				return;
			}
			// *========控制台输出=========* //	
			System.out.println("请求方法:"
					+ (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()")
					+ "." + operationType);
			System.out.println("方法描述:" + operationName);
			//System.out.println("请求人:" + BaseUntil.getUserByToken()); 
			System.out.println("请求IP:" + ip);	
			// *========数据库日志=========*//
			logEntity.setEvent(operationName);
			logEntity.setEventType(operationType);
			logEntity.setMethod(targetName + "." + methodName + "()");
			//logEntity.setUserId(BaseUntil.getUserByToken());
			logEntity.setIp(ip);
			logEntity.setRequestType(request.getMethod());
			logEntity.setUrl(request.getRequestURL().toString());
			logEntity.setCostTime(time);
			if(res != null)
			logEntity.setReturnValue(res.toString());
			//获取用户姓名
			//String token = request.getHeader("authorization");
			//String apiStr = BaseUntil.getProperties("baseConfig.properties", "ServerGetUserInfo");
			//String url = "http://" + BaseUntil.getProperties("baseConfig.properties", "ServerIp") + ":" + BaseUntil.getProperties("baseConfig.properties", "ServerPort") + apiStr;
//			System.out.println("url="+url);
//			System.out.println("token="+token);
			//JSONObject obj = JSONObject.fromObject(BaseUntil.httpGet(url, token));
//			System.out.println("loginObj="+obj);
//			if((Integer)obj.get("code") == 200) {
//				logEntity.setUserName(JSONObject.fromObject(JSONObject.fromObject(obj.getString("data")).getString("user")).getString("name"));
//			}
			//参数处理
			Signature signature = joinPoint.getSignature();
		    MethodSignature methodSignature = (MethodSignature) signature;
		    // 通过这获取到方法的所有参数名称的字符串数组
		    String[] paraNames = methodSignature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            StringBuilder sb = new StringBuilder();
            if (paraNames != null && paraNames.length > 0 && args != null && args.length > 0) {
                for (int i = 0; i < paraNames.length; i++) {
                    sb.append(paraNames[i] + ":" + args[i] + ",");
                }
                sb.delete(sb.length() - 1, sb.length() + 1);
            }
            
            String param = sb.toString();
            System.out.println("参数="+param);
			logEntity.setRequestParameter(param);
			// 保存数据库
			//systemMapper.addLog(logEntity);
			//System.out.println("保存数据库");
			logger.info("本次记录:"+logEntity);
			//System.out.println("log记录="+logEntity);
		} catch (Exception e) {
			e.printStackTrace();
			// 记录本地异常日志
			logger.error("==环绕通知异常==");
			logger.error("异常信息:{}", e.getMessage());
		}

	}

}
