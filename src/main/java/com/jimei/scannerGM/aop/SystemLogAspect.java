package com.jimei.scannerGM.aop;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jimei.scannerGM.service.SystemService;

/**
 * 日志
 * 
 * @author lixin
 *
 */
@Aspect
@Component
public class SystemLogAspect {
	// 注入Service用于把日志保存
	@Resource 
	private SystemService systemLogService;
	private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

	// Controller层切点
	// 第一个*代表所有的返回值类型
	// 第二个*代表所有的类
	// 第三个*代表类所有方法
	// 最后一个..代表所有的参数。
//	@Pointcut("execution (* com.jimei.RFIDManager.controller..*.*(..))")
//	public void controllerAspect() {
//	}
	@Pointcut("@annotation( com.jimei.scannerGM.annotation.Log)")
    public void controllerAspect() {
    }
	@Around("controllerAspect()")
	public Object arround(ProceedingJoinPoint point) throws Throwable {
		logger.info("==环绕通知开始==");
		Object res = null;
		long time = System.currentTimeMillis();
		try {
			res = point.proceed();		
			time = System.currentTimeMillis() - time;
		} finally {
			// 方法执行完成后增加日志
			logger.info("==日志处理==");
			systemLogService.logManager(point, res, time);
			logger.info("==环绕通知结束==");
		}
		return res;
	}
//	/**
//	 * 前置通知 用于拦截controller
//	 * 
//	 * @param joinPoint void lixin 2020年5月22日下午12:22:56
//	 */
//	@Before("controllerAspect()")
//	public void doBefore(JoinPoint joinPoint) {
//		/*
//		 * System.out.println("==========执行controller前置通知===============");
//		 * if(logger.isInfoEnabled()){ logger.info("before " + joinPoint); }
//		 */
//
//		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//				.getRequest();
//		HttpSession session = request.getSession();
//		// 读取session中的用户
////   SysUser user = (SysUser) session.getAttribute("user"); 
////   if(user==null){
////  	 user=new SysUser();
////  	 user.setUserName("非注册用户");
////   }
//		// 请求的IP
//		String ip = request.getRemoteAddr();
//		try {
//
//			String targetName = joinPoint.getTarget().getClass().getName();
//			String methodName = joinPoint.getSignature().getName();
//			Object[] arguments = joinPoint.getArgs();
//			Class targetClass = Class.forName(targetName);
//			Method[] methods = targetClass.getMethods();
//			String operationType = "";
//			String operationName = "";
//			for (Method method : methods) {
//				if (method.getName().equals(methodName)) {
//					Class[] clazzs = method.getParameterTypes();
//					if (clazzs.length == arguments.length) {
//						operationType = method.getAnnotation(Log.class).operationType();
//						operationName = method.getAnnotation(Log.class).operationName();
//						break;
//					}
//				}
//			}
//			// *========控制台输出=========*//
//			System.out.println("=====controller前置通知开始=====");
//			System.out.println("请求方法:"
//					+ (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()")
//					+ "." + operationType);
//			System.out.println("方法描述:" + operationName);
////    System.out.println("请求人:" + user.getUserName()); 
//			System.out.println("请求IP:" + ip);
//			// *========数据库日志=========*//
//			SysLog log = new SysLog();
//			log.setDescription(operationName);
//			log.setMethod((joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()")
//					+ "." + operationType);
//			log.setLogType(0);
//			log.setRequestIp(ip);
//			log.setExceptionCode(null);
//			log.setExceptionDetail(null);
//			log.setParams(null);
////    log.setCreateBy(user.getUserName());
//			log.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//			log.setRequestIp(ip);
//			// 保存数据库
////    systemLogService.insert(log); 
//			System.out.println(log);
//			System.out.println("=====controller前置通知结束=====");
//		} catch (Exception e) {
//			// 记录本地异常日志
//			logger.error("==前置通知异常==");
//			logger.error("异常信息:{}", e.getMessage());
//		}
//
//	}
//
//	/**
//	 * 异常通知 用于拦截记录异常日志
//	 * 
//	 * @param joinPoint
//	 * @param e         void lixin 2020年5月22日下午12:21:33
//	 */
//	@AfterThrowing(pointcut = "controllerAspect()", throwing = "e")
//	public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
//
//		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//				.getRequest();
//		HttpSession session = request.getSession();
//		// 读取session中的用户
////   SysUser user = (SysUser) session.getAttribute("user"); 
////   if(user==null){
////   	 user=new SysUser();
////   	 user.setUserName("非注册用户");
////   }
//		// 请求的IP
//		String ip = request.getRemoteAddr();
//
//		String params = "";
//		if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
//
//			params = Arrays.toString(joinPoint.getArgs());
//		}
//		try {
//
//			String targetName = joinPoint.getTarget().getClass().getName();
//			String methodName = joinPoint.getSignature().getName();
//			Object[] arguments = joinPoint.getArgs();
//			Class targetClass = Class.forName(targetName);
//			Method[] methods = targetClass.getMethods();
//			String operationType = "";
//			String operationName = "";
//			for (Method method : methods) {
//				if (method.getName().equals(methodName)) {
//					Class[] clazzs = method.getParameterTypes();
//					if (clazzs.length == arguments.length) {
//						operationType = method.getAnnotation(Log.class).operationType();
//						operationName = method.getAnnotation(Log.class).operationName();
//						break;
//					}
//				}
//			}
//			/* ========控制台输出========= */
//			System.out.println("=====异常通知开始=====");
//			System.out.println("异常代码:" + e.getClass().getName());
//			System.out.println("异常信息:" + e.getMessage());
//			System.out.println("异常方法:"
//					+ (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()")
//					+ "." + operationType);
//			System.out.println("方法描述:" + operationName);
////    System.out.println("请求人:" + user.getUserName()); 
//			System.out.println("请求IP:" + ip);
//			System.out.println("请求参数:" + params);
//			// ==========数据库日志=========
//			SysLog log = new SysLog();
//			log.setDescription(operationName);
//			log.setExceptionCode(e.getClass().getName());
//			log.setLogType(1);
//			log.setExceptionDetail(e.getMessage());
//			log.setMethod(
//					(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
//			log.setParams(params);
////    log.setCreateBy(user.getUserName()); 
//			log.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//			log.setRequestIp(ip);
//			// 保存数据库
////    systemLogService.insert(log); 
//			System.out.println("=====异常通知结束=====");
//		} catch (Exception ex) {
//			// 记录本地异常日志
//			logger.error("==异常通知异常==");
//			logger.error("异常信息:{}", ex.getMessage());
//		}
//		// ==========记录本地异常日志==========
//		logger.error("异常方法:{}异常代码:{}异常信息:{}参数:{}",
//				joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName(), e.getClass().getName(),
//				e.getMessage(), params);
//
//	}

}
