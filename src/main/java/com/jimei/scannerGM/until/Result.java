package com.jimei.scannerGM.until;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 统一返回结果 
 * @author lixin
 *
 */
@ApiModel(value = "Result",description = "统一返回结果")
public class Result {
    
    public static final String SUCCESS = "操作成功。";
    public static final String FAILURE = "操作失败！";
    @ApiModelProperty(name = "result",value = "操作正确与否",dataType = "boolean",required = true)
    private boolean result;
    @ApiModelProperty(name = "msg",value = "操作结果信息",dataType = "String",required = true)
    private String msg;
    @ApiModelProperty(name = "datas",value = "标签数据",dataType = "Object",required = true)
    private Object datas;
    @ApiModelProperty(name = "code",value = "返回状态码",dataType = "Integer",required = true)
    private Integer code;
    
    private Result() {}
    
    public static Result ok() {
        return Result.ok(SUCCESS);
    }
    
    public static Result ok(String msg) {
        return Result.ok(msg, null);
    }
    
    public static Result ok(Object datas) {
        return Result.ok(SUCCESS, datas);
    }
    
    @Override
	public String toString() {
		return "Result [result=" + result + ", msg=" + msg + ", datas=" + datas + ", code=" + code + "]";
	}

	public static Result ok(String msg, Object datas) {
        Result result = new Result();
        result.setResult(true);
        result.setMsg(msg);
        result.setDatas(datas);
        result.setCode(200);//成功
        return result;
    }
    
    public static Result fail() {
        return Result.fail(FAILURE);
    }
    
    public static Result fail(String msg) {
        return Result.fail(msg, null);
    }
    
    public static Result fail(Object datas) {
        return Result.fail(FAILURE, datas);
    }
    
    public static Result fail(String msg, Object datas) {
        Result result = new Result();
        result.setResult(false);
        result.setMsg(msg);
        result.setDatas(datas);
        result.setCode(400);//失败
        return result;
    }
    
    public boolean isResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getDatas() {
        return datas;
    }
    public void setDatas(Object datas) {
        this.datas = datas;
    }

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
    

//    public static void main(String[] args) {
//        System.out.println(JsonUtil.obj2String(Result.ok()));
//        System.out.println(JsonUtil.obj2String(Result.ok("haha")));
//        System.out.println(JsonUtil.obj2String(Result.ok("haha", "aaa")));
//        
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("aa", "111");
//        map.put("bb", "22");
//        map.put("ccc", "33");
//        System.out.println(JsonUtil.obj2String(Result.ok("haha", map)));
//        
//        List<String> list = new ArrayList<String>();
//        list.add("aaaaaaaa");
//        list.add("bbbbbb");
//        list.add("cccccccccccc");
//        list.add("ddddddddddd");
//        System.out.println(JsonUtil.obj2String(Result.ok("haha", list)));
//        
//        System.out.println(JsonUtil.obj2String(Result.fail()));
//        System.out.println(JsonUtil.obj2String(Result.fail("失败了")));
//        System.out.println(JsonUtil.obj2String(Result.fail(list)));
//        System.out.println(JsonUtil.obj2String(Result.fail("失败了", map)));
//    }
    
    
}
