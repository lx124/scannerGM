package com.jimei.scannerGM.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
/**
 * 数据库日志 实体类
 * @author lixin
 *
 */
@Data
public class LogEntity {




	//private String type = "";// 操作日志，数据日志

	private String event = "";// 操作具体的内容

	private String eventType = "";// 操作的类型

	//private String target = "";

	//private List<AuditDetailEntity> detail = new ArrayList<AuditDetailEntity>();

	private String userId = "";// userId

	private String userName = "";

	private String url = "";//请求url

	private String requestType = "";//请求类型

	private String method = "";//请求方法 类+方法

	private String requestParameter = "";//参数

	private String ip = "";//ip

	//private List<String> files = new ArrayList<String>();

	private Long costTime;

	private String returnValue = "";
	
	//private Date createdAt = new Date();

	

}
