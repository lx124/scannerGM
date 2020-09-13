package com.jimei.scannerGM.service;

import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;
/**
 * 业务层
 * @author lixin
 *
 */
@Service
public interface SystemService {
	/**
	 * 日志拦截记录
	 * @param joinPoint
	 * @param res
	 * @param time void
	 * lixin 2020年7月17日上午8:56:32
	 */
	void logManager(JoinPoint joinPoint, Object res, long time)throws Exception;
}
